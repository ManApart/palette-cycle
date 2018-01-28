package rak.pixellwp

import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import rak.pixellwp.drawing.CirclePoint


class CircleWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return CircleWallpaperEngine()
    }

    inner class CircleWallpaperEngine : Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@CircleWallpaperService)

        private var width: Int = 0
        private var height: Int = 0
        private var maxNumber: Int = prefs.getString("numberOfCircles", "4").toInt()
        private var circles = mutableListOf<CirclePoint>()
        private var visible = true
        private var touchEnabled: Boolean = prefs.getBoolean("touch", false)

        private val paint = Paint()

        init {
            paint.isAntiAlias = true
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = 10f
            handler.post(drawRunner)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) handler.post(drawRunner) else handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            this.width = width
            this.height = height
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onTouchEvent(event: MotionEvent?) {
            if (touchEnabled){
                val x: Float = event!!.x
                val y: Float = event!!.y
                var canvas: Canvas? = null
                try {
                    canvas = surfaceHolder.lockCanvas()
                    if (canvas != null){
                        canvas.drawColor(Color.BLACK)
                        circles.clear()
                        circles.add(CirclePoint((circles.size+1).toString(), x, y))
                        drawCircles(canvas, circles)
                    }

                } finally {
                    if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
                }
                super.onTouchEvent(event)
            }
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null){
                    if (circles.size >= maxNumber){
                        circles.clear()
                    }
                    val x: Int = (width * Math.random()).toInt()
                    val y: Int = (height * Math.random()).toInt()
                    circles.add(CirclePoint((circles.size+1).toString(), x.toFloat(), y.toFloat()))
                    drawCircles(canvas, circles)
                }
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
            if (visible) handler.postDelayed(drawRunner, 1000)
        }


        private fun drawCircles(canvas: Canvas, circles: MutableList<CirclePoint>) {
            canvas.drawColor(Color.BLACK)
            for(point in circles){
                canvas.drawCircle(point.x, point.y, 20f, paint)
            }
        }
    }

}