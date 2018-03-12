package gorden.lib.anko

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import gorden.lib.anko.static.logError
import gorden.lib.anko.static.logWarn
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * document
 * Created by Gordn on 2017/7/11.
 */
object KCrash : Thread.UncaughtExceptionHandler {
    private const val TAG = "crash_log"
    private lateinit var mContext: Context
    private var mCrashHandler: Thread.UncaughtExceptionHandler? = null

    fun start(context: Context) {
        mContext = context
        mCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        logError("开始打印错误日志", tag = TAG)
        val stringWriter = StringWriter()
        e?.printStackTrace(PrintWriter(stringWriter))
        stringWriter.close()
        logError(stringWriter.toString(), TAG)

        saveException(e)

        //If the system provides a default exception handler
        if (mCrashHandler != null) {
            mCrashHandler?.uncaughtException(t, e)
        } else {
            Process.killProcess(Process.myPid())
        }
    }

    fun saveError(ex: Throwable?) {
        val jsonObject = JSONObject()
        val jsonObjectError = JSONObject()

        try {

            var i = 0

            jsonObjectError.put(i++.toString(), ex?.message)

            val stackTraceElement = ex?.stackTrace
            for (element in stackTraceElement!!) {
                jsonObjectError.put(i++.toString(), element.toString())
            }

            jsonObject.put("ErrorInfo", jsonObjectError)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        KPreferences.get(mContext).apply {
            setValue("error" to jsonObject.toString())
        }
    }

    /**
     * 本地保存
     */
    private fun saveException(ex: Throwable?) {

        val crashPath = KPath.externalRootDir(mContext)

        if (crashPath.isNullOrEmpty()) {
            logWarn("sdcard unmounted,skip dump exception")
            return
        }
        val crashFile = File(crashPath, "crashLog")
        if (!crashFile.exists()) {
            crashFile.mkdirs()
        }

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Calendar.getInstance().time)
        val file = File(crashFile, "crash_$time.log")

        try {
            PrintWriter(BufferedWriter(FileWriter(file))).use {
                //异常发生时间
                it.println("time:$time")
                //设备信息
                phoneInfo(it)
                //异常信息
                it.println()
                ex?.printStackTrace(it)
                saveError(ex)
            }
        }catch (ex:IOException){
            logError("dump crash info failed", TAG)
        }
    }

    private fun phoneInfo(pw: PrintWriter) {
        //应用的版本名称和版本号
        val pm = mContext.packageManager
        val pi = pm.getPackageInfo(mContext.packageName, PackageManager.GET_ACTIVITIES)
        pw.print("App Version: ")
        pw.print(pi.versionName)
        pw.print('_')
        pw.println(pi.versionCode)

        //android版本号
        pw.print("OS Version: ")
        pw.print(Build.VERSION.RELEASE)
        pw.print("_")
        pw.println(Build.VERSION.SDK_INT)

        //手机制造商
        pw.print("Vendor: ")
        pw.println(Build.MANUFACTURER)

        //手机型号
        pw.print("Model: ")
        pw.println(Build.MODEL)

        //cpu架构
        pw.print("CPU ABI: ")
        pw.println(Build.CPU_ABI)
    }
}