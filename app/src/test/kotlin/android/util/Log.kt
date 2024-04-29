package android.util

class Log {
    companion object {
        @JvmStatic
        fun isLoggable(tag: String, level: Int): Boolean {
            return true
        }

        @JvmStatic
        fun println(priority: Int, tag: String, msg: String): Int {
            println("[$tag] $msg")
            return 0
        }
    }
}