package rak.pixellwp.cycling

import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.SurfaceHolder
import java.util.*

class PaletteDrawer(private val engine: CyclingWallpaperService.CyclingWallpaperEngine, val image: ColorCyclingImage) : Thread() {
    private val handlerThread = HandlerThread("drawThread")
    init {
        handlerThread.start()
    }
    private val handler = Handler(handlerThread.looper)
    private val runner = Runnable { doDraw() }
    private val drawDelay = 10L
    private val startTime = Date().time
    private var visible = true
    var drawTime = 0L
    var drawCount = 0

    fun startDrawing(){
        handler.post(runner)
    }

    fun setVisible(visible: Boolean){
        this.visible = visible
        if (visible){
            startDrawing()
        } else {
            stopDrawing()
        }
    }

    private fun drawAfterDelay(delay: Long){
        handler.postDelayed(runner, delay)
    }

    private fun stopDrawing(){
        handler.removeCallbacks(runner)
    }

    private fun doDraw(){
        val timePassed = Date().time - startTime

        if (drawCount == 100){
            Log.d("optimize", "drew $drawCount times every $drawDelay seconds with average speed of ${drawTime/drawCount}")
            drawTime = 0
            drawCount = 0
        }
        val drawStart = Date().time

        image.advance(timePassed)

        drawTime += Date().time - drawStart
        drawCount++
        if (drawCount % 10 == 0){
            Log.d("pass", "draw count: $drawCount, elapsed in frame: ${Date().time - drawStart}, elapsed total: $drawTime")
    }
        drawFrame(engine.surfaceHolder, engine.imageSrc, engine.screenDimensions)
    }

    private fun drawFrame(surfaceHolder: SurfaceHolder, imageSrc: Rect, screenDimensions: Rect){
        var canvas: Canvas? = null
        try {
            canvas = surfaceHolder.lockCanvas()
            if (canvas != null && image != null){
//                Log.d("Cycle Wallpaper", "Attempting to startDrawing $image. Scale = $scaleFactor. Dimensions = $imageSrc")
                canvas.drawBitmap(image.getBitmap(), imageSrc, screenDimensions, null)
            }
        } finally {
            if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
        }
        stopDrawing()
        if (visible) drawAfterDelay(drawDelay)
    }
}
