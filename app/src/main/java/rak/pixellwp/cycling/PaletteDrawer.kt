package rak.pixellwp.cycling

import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.SurfaceHolder
import java.util.*

class PaletteDrawer(private val engine: CyclingWallpaperService.CyclingWallpaperEngine, val image: ColorCyclingImage) {
    private val logTag = "PaletteDrawer"
    private val handlerThread = HandlerThread("drawThread")
    private val id = System.currentTimeMillis()

    init {
        Log.d(logTag, "Creating drawer with id $id")
        handlerThread.start()
    }

    private val handler = Handler(handlerThread.looper)
    private val runner = Runnable { draw() }
    private val drawDelay = 50L
    private val startTime = Date().time
    private var visible = true

    fun startDrawing() {
        handler.post(runner)
    }

    fun stop() {
        setVisible(false)
        handlerThread.quitSafely()
    }

    fun setVisible(visible: Boolean) {
        this.visible = visible
        if (visible) {
            startDrawing()
        } else {
            stopDrawing()
        }
    }

    fun drawNow() {
        handler.post(runner)
    }

    private fun drawAfterDelay(delay: Long) {
        handler.postDelayed(runner, delay)
    }

    private fun stopDrawing() {
        handler.removeCallbacks(runner)
    }

    private fun draw() {
        val timePassed = Math.floor((Date().time - startTime).toDouble()).toInt()
        image?.advance(timePassed)
        drawFrame(engine.surfaceHolder, engine.getOffsetImage(), engine.screenDimensions)
    }

    private fun drawFrame(surfaceHolder: SurfaceHolder, imageSrc: Rect, screenDimensions: Rect) {
        val canvas = surfaceHolder.lockCanvas()
        if (canvas == null) {
//            Log.e(logTag, "Can't lock canvas; killing this drawer")
//            stop()
        } else if (image != null) {
            canvas.drawBitmap(image.getBitmap(), imageSrc, screenDimensions, null)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
        stopDrawing()
        if (visible) drawAfterDelay(drawDelay)
    }
}
