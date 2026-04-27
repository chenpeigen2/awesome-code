import platform.windows.GetSystemTimeAsFileTime
import platform.windows.FILETIME
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr

actual fun getPlatformName(): String = "Windows (MinGW)"

actual fun getSystemInfo(): String = "Windows x86_64"

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long = memScoped {
    val ft: FILETIME = alloc()
    GetSystemTimeAsFileTime(ft.ptr)
    // FILETIME is 100-nanosecond intervals since 1601-01-01
    // Convert to Unix epoch millis: subtract 11644473600000 ms
    val combined = (ft.dwHighDateTime.toLong() shl 32) or (ft.dwLowDateTime.toLong() and 0xFFFFFFFFL)
    combined / 10000L - 11644473600000L
}
