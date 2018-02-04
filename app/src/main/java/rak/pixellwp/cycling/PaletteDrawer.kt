package rak.pixellwp.cycling

import android.os.Handler
import android.os.HandlerThread

class PaletteDrawer(val engine: CyclingWallpaperService.CyclingWallpaperEngine) : Thread() {
    private val handlerThread = HandlerThread("drawThread")
    init {
        handlerThread.start()
    }
    private val handler = Handler(handlerThread.looper)
    private val runner = Runnable { engine.draw() }

    fun draw(){
        handler.post(runner)
    }

    fun drawDelayed(delay: Long){
        handler.postDelayed(runner, delay)
    }

    fun removeCallbacks(){
        handler.removeCallbacks(runner)
    }
}
