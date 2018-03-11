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
    val id = System.currentTimeMillis()

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
        if (visible && !surfaceHolder.isCreating) {
            try {
                val canvas = surfaceHolder.lockCanvas()
                if (canvas == null) {
                    Log.e(logTag, "Can't lock canvas; killing drawer + $id")
                    stop()
                } else {
                    if (image != null) {
                        canvas.drawBitmap(image.getBitmap(), imageSrc, screenDimensions, null)
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            } catch (e: Exception) {
                Log.e(logTag, "failed to draw frame, id: $id")
                Log.e(logTag, e.toString())
            }
            stopDrawing()
        }
        if (visible) drawAfterDelay(drawDelay)
    }
}
