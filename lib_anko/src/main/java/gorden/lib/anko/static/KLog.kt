@file:Suppress("NOTHING_TO_INLINE", "unused")

package gorden.lib.anko.static

import android.util.Log

/**
 * Kotlin Logger
 */

private const val TAG_DEFAULT = "KLog   >"
private const val MIN_STACK_OFFSET = 4
private const val NULL_MESSAGE = "Null Message"
private const val DEFAULT_MESSAGE = "execute in here"

private var IS_SHOW_LOG = true
private var TAG_NAME = TAG_DEFAULT

/**
 * Klog初始化配置
 */
fun logInit(showLog: Boolean, tagName: String = TAG_NAME) {
    IS_SHOW_LOG = showLog
    TAG_NAME = tagName
}

fun logError(msg: Any? = DEFAULT_MESSAGE, tag: String = TAG_NAME) {
    printLog(Log.ERROR, tag, msg)
}

fun logVerbose(msg: Any? = DEFAULT_MESSAGE, tag: String = TAG_NAME) {
    printLog(Log.VERBOSE, tag, msg)
}

fun logDebug(msg: Any? = DEFAULT_MESSAGE, tag: String = TAG_NAME) {
    printLog(Log.DEBUG, tag, msg)
}

fun logInfo(msg: Any? = DEFAULT_MESSAGE, tag: String = TAG_NAME) {
    printLog(Log.INFO, tag, msg)
}

fun logWarn(msg: Any? = DEFAULT_MESSAGE, tag: String = TAG_NAME) {
    printLog(Log.WARN, tag, msg)
}

fun logAssert(msg: Any? = DEFAULT_MESSAGE, tag: String = TAG_NAME) {
    printLog(Log.ASSERT, tag, msg)
}


private fun printLog(level: Int, tagName: String, msg: Any?) {
    if (!IS_SHOW_LOG) return

    val confirmMsg = "${headerContent()}${msg ?: NULL_MESSAGE}"

    val bytes = confirmMsg.toByteArray()
    val length = bytes.size
    val maxLength = 3999
    if (length <= maxLength) {
        logContent(level, tagName, confirmMsg)
        return
    }

    var i = 0
    while (i < length) {
        val count = Math.min(length - i, maxLength)
        logContent(level, tagName, String(bytes, i, count))
        i += maxLength
    }
}

private fun logContent(type: Int, tag: String, chunk: String) {
    val lines = chunk.split(System.getProperty("line.separator").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    for (line in lines) {
        logChunk(type, tag, line)
    }
}

private inline fun headerContent(): String {
    val stackTrace = Thread.currentThread().stackTrace
    val stackOffset = getStackOffset(stackTrace) + 1
    val builder = StringBuilder()
    builder.append("[ (")
            .append(stackTrace[stackOffset].fileName)
            .append(":")
            .append(stackTrace[stackOffset].lineNumber)
            .append(")")
            .append("#${stackTrace[stackOffset].methodName} ] ")
    return builder.toString()
}

/**
 * 打印数据
 */
private inline fun logChunk(level: Int, tag: String, chunk: String) {
    when (level) {
        Log.VERBOSE -> Log.v(tag, chunk)
        Log.DEBUG -> Log.d(tag, chunk)
        Log.INFO -> Log.i(tag, chunk)
        Log.WARN -> Log.w(tag, chunk)
        Log.ERROR -> Log.e(tag, chunk)
        Log.ASSERT -> Log.wtf(tag, chunk)
    }
}

private fun getStackOffset(trace: Array<StackTraceElement>): Int {
    var i = MIN_STACK_OFFSET
    while (i < trace.size) {
        val e = trace[i]
        val name = e.className
        if (name != "gorden.lib.anko.static.KLogKt") {
            return --i
        }
        i++
    }
    return -1
}