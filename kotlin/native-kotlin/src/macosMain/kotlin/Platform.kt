import platform.posix.uname
import platform.posix.utsname
import platform.posix.gettimeofday
import platform.posix.timeval
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString

actual fun getPlatformName(): String = "macOS"

@OptIn(ExperimentalForeignApi::class)
actual fun getSystemInfo(): String = memScoped {
    val info: utsname = alloc()
    uname(info.ptr)
    "${info.sysname.toKString()} ${info.release.toKString()} (${info.machine.toKString()})"
}

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long = memScoped {
    val tv: timeval = alloc()
    gettimeofday(tv.ptr, null)
    tv.tv_sec * 1000L + tv.tv_usec / 1000L
}
