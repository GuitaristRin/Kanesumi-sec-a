"""Windows 日常用机入口：Job Object 限制整个进程树，退出时回收子进程。"""
import argparse
import ctypes as C
from ctypes import wintypes as W
import os
from pathlib import Path
import subprocess


class StartupInfo(C.Structure):
    _fields_ = [("cb", W.DWORD), ("reserved", W.LPWSTR), ("desktop", W.LPWSTR),
                ("title", W.LPWSTR)] + [(n, W.DWORD) for n in
                ("x", "y", "width", "height", "chars_x", "chars_y", "fill", "flags")] + [
                ("show", W.WORD), ("reserved_size", W.WORD), ("reserved_ptr", C.c_void_p),
                ("stdin", W.HANDLE), ("stdout", W.HANDLE), ("stderr", W.HANDLE)]


class ProcessInfo(C.Structure):
    _fields_ = [("process", W.HANDLE), ("thread", W.HANDLE), ("pid", W.DWORD), ("tid", W.DWORD)]


class BasicLimits(C.Structure):
    _fields_ = [("process_time", C.c_longlong), ("job_time", C.c_longlong), ("flags", W.DWORD),
                ("min_working", C.c_size_t), ("max_working", C.c_size_t), ("active", W.DWORD),
                ("affinity", C.c_size_t), ("priority", W.DWORD), ("scheduling", W.DWORD)]


class IoCounters(C.Structure):
    _fields_ = [(n, C.c_ulonglong) for n in ("read_ops", "write_ops", "other_ops", "read", "write", "other")]


class ExtendedLimits(C.Structure):
    _fields_ = [("basic", BasicLimits), ("io", IoCounters)] + [
        (n, C.c_size_t) for n in ("process_memory", "job_memory", "peak_process", "peak_job")]


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--log", type=Path, required=True)
    parser.add_argument("--priority", choices=("below-normal", "normal"), default="below-normal")
    parser.add_argument("command", nargs=argparse.REMAINDER)
    args = parser.parse_args()
    command = args.command[1:] if args.command[:1] == ["--"] else args.command
    if os.name != "nt" or not command:
        parser.error("需要 Windows 和 -- 后的可执行命令")
    import msvcrt
    kernel = C.WinDLL("kernel32", use_last_error=True)
    signatures = {
        "CreateJobObjectW": (W.HANDLE, [C.c_void_p, W.LPCWSTR]),
        "GetCurrentProcess": (W.HANDLE, []),
        "GetProcessAffinityMask": (W.BOOL, [W.HANDLE, C.POINTER(C.c_size_t), C.POINTER(C.c_size_t)]),
        "SetInformationJobObject": (W.BOOL, [W.HANDLE, C.c_int, C.c_void_p, W.DWORD]),
        "CreateProcessW": (W.BOOL, [W.LPCWSTR, W.LPWSTR, C.c_void_p, C.c_void_p, W.BOOL,
                                     W.DWORD, C.c_void_p, W.LPCWSTR, C.POINTER(StartupInfo), C.POINTER(ProcessInfo)]),
        "AssignProcessToJobObject": (W.BOOL, [W.HANDLE, W.HANDLE]),
        "ResumeThread": (W.DWORD, [W.HANDLE]),
        "WaitForSingleObject": (W.DWORD, [W.HANDLE, W.DWORD]),
        "GetExitCodeProcess": (W.BOOL, [W.HANDLE, C.POINTER(W.DWORD)]),
        "TerminateProcess": (W.BOOL, [W.HANDLE, W.UINT]),
        "TerminateJobObject": (W.BOOL, [W.HANDLE, W.UINT]),
        "CloseHandle": (W.BOOL, [W.HANDLE]),
    }
    for name, (result, parameters) in signatures.items():
        function = getattr(kernel, name)
        function.restype, function.argtypes = result, parameters

    def checked(result):
        if not result:
            raise C.WinError(C.get_last_error())
        return result

    available, system = C.c_size_t(), C.c_size_t()
    checked(kernel.GetProcessAffinityMask(kernel.GetCurrentProcess(), C.byref(available), C.byref(system)))
    allowed = [1 << i for i in range(C.sizeof(C.c_size_t) * 8) if available.value & (1 << i)][:2]
    affinity = sum(allowed)
    job = checked(kernel.CreateJobObjectW(None, None))
    process = ProcessInfo()
    try:
        limits = ExtendedLimits()
        # 整树亲和性 + 低优先级 + 句柄关闭即回收；子进程不能靠另开线程绕过。
        limits.basic.flags = 0x10 | 0x20 | 0x2000
        priority = 0x4000 if args.priority == "below-normal" else 0x20
        limits.basic.affinity, limits.basic.priority = affinity, priority
        checked(kernel.SetInformationJobObject(job, 9, C.byref(limits), C.sizeof(limits)))
        # 整树硬亲和性即两核上限；不再叠加会相对父 Job 折算的时间片 quota。
        # https://learn.microsoft.com/windows/win32/api/winnt/ns-winnt-jobobject_basic_limit_information
        args.log.parent.mkdir(parents=True, exist_ok=True)
        with args.log.open("wb") as output, open(os.devnull, "rb") as input_file:
            startup = StartupInfo()
            startup.cb, startup.flags = C.sizeof(startup), 0x100 | 0x1
            startup.stdin = msvcrt.get_osfhandle(input_file.fileno())
            startup.stdout = startup.stderr = msvcrt.get_osfhandle(output.fileno())
            for handle in (startup.stdin, startup.stdout):
                os.set_handle_inheritable(handle, True)
            line = C.create_unicode_buffer(subprocess.list2cmdline(command))
            # 先挂起创建、纳入 Job，再恢复；避免 child 在限额设置前抢先启动。
            checked(kernel.CreateProcessW(None, line, None, None, True, 0x4 | priority | 0x08000000,
                                           None, None, C.byref(startup), C.byref(process)))
            checked(kernel.AssignProcessToJobObject(job, process.process))
            if kernel.ResumeThread(process.thread) == 0xFFFFFFFF:
                raise C.WinError(C.get_last_error())
            ceiling = 100 * len(allowed) / (os.cpu_count() or 1)
            print(f"PID={process.pid} affinity=0x{affinity:x} cores={len(allowed)} priority={args.priority} host_cpu_ceiling={ceiling:.2f}%", flush=True)
            if kernel.WaitForSingleObject(process.process, 0xFFFFFFFF) != 0:
                raise C.WinError(C.get_last_error())
            exit_code = W.DWORD()
            checked(kernel.GetExitCodeProcess(process.process, C.byref(exit_code)))
            return exit_code.value
    finally:
        if process.process:
            kernel.TerminateJobObject(job, 1)
            kernel.TerminateProcess(process.process, 1)
            kernel.CloseHandle(process.thread)
            kernel.CloseHandle(process.process)
        kernel.CloseHandle(job)


if __name__ == "__main__":
    raise SystemExit(main())
