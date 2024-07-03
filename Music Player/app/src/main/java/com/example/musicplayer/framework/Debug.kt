package com.example.musicplayer.framework

import android.util.Log

class Debug {

    companion object {

         var logTag = "Amir"
         var logLevel = LOG_LEVEL.INFO

        fun logVerbose(message: Any) = log(LOG_LEVEL.VERBOSE, message)
        fun logDebug(message: Any) = log(LOG_LEVEL.DEBUG, message)
        fun logInfo(message: Any) = log(LOG_LEVEL.INFO, message)
        fun logWarning(message: Any) = log(LOG_LEVEL.WARN, message)
        fun logError(message: Any) = log(LOG_LEVEL.ERROR, message)

        private fun log(logLevel: LOG_LEVEL, message: Any) {

            if (logLevel.ord < Companion.logLevel.ord) {
                return
            }

            when (logLevel) {
                LOG_LEVEL.VERBOSE -> Log.v(logTag, "" + message)
                LOG_LEVEL.DEBUG -> Log.d(logTag, "" + message)
                LOG_LEVEL.INFO -> Log.i(logTag, "" + message)
                LOG_LEVEL.WARN -> Log.w(logTag, "" + message)
                LOG_LEVEL.ERROR -> Log.e(logTag, "" + message)
                else -> {}
            }

        }
    }
}

enum class LOG_LEVEL(val ord: Int) {
    VERBOSE(1),
    DEBUG(2),
    INFO(3),
    WARN(4),
    ERROR(5),
    DISABLE(6),

}