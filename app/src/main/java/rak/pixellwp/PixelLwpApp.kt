package rak.pixellwp

import android.app.Application
import android.content.Context
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImageInfo
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

//import com.squareup.leakcanary.LeakCanary

class PixelLwpApp : Application() {
    private val logTag = "PixelLWPApp"

    override fun onCreate() {
        super.onCreate()
        startLogging()
//        if (LeakCanary.isInAnalyzerProcess(this)){
//            return
//        }
//        LeakCanary.install(this)

    }


    private fun startLogging() {
        val logFileName = "log-${System.currentTimeMillis()}.txt"
        Log.d(logTag, "logging to file $logFileName")

        try {
            val logFileStream = OutputStreamWriter(baseContext.openFileOutput(logFileName, Context.MODE_PRIVATE))
            logFileStream.write("begin log")
            logFileStream.close()

            val logDirectory = baseContext.getDir("logs", Context.MODE_PRIVATE)
            val logFile = File("${logDirectory.path}/$logFileName")

            Runtime.getRuntime().exec("logcat -c")
            val fileCommand = "logcat -f $logFile *:W $logTag:D CyclingWallpaperService:D ImageLoader:D JsonDownloader:D PaletteDrawer:D"
            Runtime.getRuntime().exec(fileCommand)
        } catch (e: IOException){
            e.printStackTrace()
        }

    }


}