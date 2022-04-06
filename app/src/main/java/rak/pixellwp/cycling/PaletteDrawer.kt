package rak.pixellwp.cycling

import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import rak.pixellwp.cycling.models.PaletteImage
import rak.pixellwp.cycling.wallpaperService.CyclingWallpaperService
import rak.pixellwp.cycling.wallpaperService.getOffsetImage
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.floor

class PaletteDrawer(private val engine: CyclingWallpaperService.CyclingWallpaperEngine, var image: PaletteImage) {
    private val logTag = "PaletteDrawer"
    val id = System.currentTimeMillis()

    private val drawDelay = 20L
    private val startTime = Date().time
    private var visible = true

    fun startDrawing() {
        Log.v(logTag, "$id: Start drawing")
        thread {
            while (true) {
                runBlocking {
                    if (visible) {
                        launch {
                            try {
                                advanceAndDraw()
                            } catch (e: Throwable) {
                                Log.e(logTag, "Failed to post, killing drawer + $id")
                                Log.e(logTag, e.toString())
                                stop()
                            }
                        }
                    }
                    delay(drawDelay)
                }
            }
        }
    }

    fun drawNow() {
//        if (!this.visible) {
        this.visible = true
//            startDrawing()

//        }


    }

    fun stop() {
        Log.v(logTag, "$id: Stop drawing")
        setVisible(false)
    }

    fun setVisible(visible: Boolean) {
        this.visible = visible
        if (visible) {
            drawNow()
        }
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
        }
    }
}
