package rak.pixellwp.cycling

import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.SurfaceHolder
import rak.pixellwp.cycling.models.ColorCyclingImage
import rak.pixellwp.cycling.models.PaletteImage
import java.util.*
import kotlin.math.floor

class PaletteDrawer(private val engine: CyclingWallpaperService.CyclingWallpaperEngine, var image: PaletteImage) {
    private val logTag = "PaletteDrawer"
    private var handlerThread = HandlerThread("drawThread")
    val id = System.currentTimeMillis()

    init {
        handlerThread.start()
    }
    private var handler = Handler(handlerThread.looper)
    private val runner = Runnable { advanceAndDraw() }
    private val drawDelay = 20L
    private val startTime = Date().time
    private var visible = true

    fun startDrawing() {
        Log.v(logTag, "$id: Start drawing")
        handlerThread.quitSafely()
        handlerThread = HandlerThread("drawThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        drawNow()
    }

    fun drawNow() {
        try {
            handler.post(runner)
        } catch (e: Throwable){
            Log.e(logTag, "Failed to post, killing drawer + $id")
            Log.e(logTag, e.toString())
            stop()
        }
    }

    fun stop() {
        Log.v(logTag, "$id: Stop drawing")
        setVisible(false)
        handlerThread.quitSafely()
    }

    fun setVisible(visible: Boolean) {
        this.visible = visible
        if (visible) {
            drawNow()
        } else {
            handler.removeCallbacks(runner)
        }
    }

    private fun drawAfterDelay() {
        handler.postDelayed(runner, drawDelay)
    }

    private fun advanceAndDraw() {
        val timePassed = floor((Date().time - startTime).toDouble()).toInt()
        image.advance(timePassed)
        drawFrame(engine.surfaceHolder, engine.getOffsetImage(), engine.screenDimensions)
    }

    private fun drawFrame(surfaceHolder: SurfaceHolder, imageSrc: Rect, screenDimensions: Rect) {
        if (visible && !surfaceHolder.isCreating) {
            try {
                surfaceHolder.lockCanvas()?.let { canvas ->
                    canvas.drawBitmap(image.getBitmap(), imageSrc, screenDimensions, null)
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            } catch (e: Exception) {
                Log.e(logTag, "$id: failed to advanceAndDraw frame; probably couldn't lock surface")
            }
            handler.removeCallbacks(runner)
        }
        if (visible) drawAfterDelay()
    }
}
