package rak.pixellwp.cycling

import android.content.Context
import android.util.Log
import java.io.*
import java.nio.channels.FileChannel

class LoggingManager(private val context: Context) {
    private val logTag = "LoggingManager"

    fun startLogging() {
        val logFileName = "log-${System.currentTimeMillis()}.txt"
        Log.d(logTag, "logging to file $logFileName")
        val logDirectory = context.getDir("logs", Context.MODE_PRIVATE)
        val logFile = File("${logDirectory.path}/$logFileName")

        try {
            Runtime.getRuntime().exec("logcat -c")
            val fileCommand = "logcat -f $logFile *:W $logTag:D CyclingWallpaperService:D ImageLoader:D JsonDownloader:D PaletteDrawer:D"
            Runtime.getRuntime().exec(fileCommand)
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun writeLogsToExternal() {
        val logDirectory = context.getDir("logs", Context.MODE_PRIVATE)
        Log.d(logTag, "found ${logDirectory.listFiles().size} log files")

        for (log in logDirectory.listFiles ()){
            val fileOut = File(context.getExternalFilesDir(null), log.name)
            var inChannel: FileChannel? = null
            var outChannel: FileChannel? = null

            try {
                inChannel = FileInputStream(log).channel
                outChannel = FileOutputStream(fileOut).channel
                inChannel!!.transferTo(0, inChannel!!.size(), outChannel)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (inChannel != null) inChannel!!.close()
                if (outChannel != null) outChannel!!.close()
            }
            Log.d(logTag, "wrote file: ${fileOut.absolutePath}")
        }
        Log.d(logTag, "finished writing log files")

    }
}