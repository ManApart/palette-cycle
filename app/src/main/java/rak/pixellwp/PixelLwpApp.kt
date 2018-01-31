package rak.pixellwp

import android.app.Application
//import com.squareup.leakcanary.LeakCanary

class PixelLwpApp : Application() {

    override fun onCreate() {
        super.onCreate()
//        if (LeakCanary.isInAnalyzerProcess(this)){
//            return
//        }
//        LeakCanary.install(this)
    }
}