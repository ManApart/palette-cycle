package rak.pixellwp

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder

class ImageWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return ImageWallpaperEngine()
    }

    inner class ImageWallpaperEngine : Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private val image = BitmapFactory.decodeResource(resources, R.drawable.beach)

        private var visible = true
        private var imageSrc = Rect(0, 0, image.width, image.height)
        private var screenDimensions = Rect()

        private var scaleFactor = 1f
        private val scaleDetector = ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                scaleFactor *= (detector?.scaleFactor ?: 1f)
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5f))
                return true
            }
        })

        private val panDetector = object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                val left = imageSrc.left + distanceX
                val top = imageSrc.top + distanceY
                val right = left + screenDimensions.width() / scaleFactor
                val bottom = top + screenDimensions.height() / scaleFactor

                imageSrc = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                Log.d(Log.DEBUG.toString(), "Dist x: $distanceX, y: $distanceY, scale: $scaleFactor = new dimensions: $imageSrc")
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        }


        private val detector: GestureDetectorCompat = GestureDetectorCompat(applicationContext, panDetector)

        init {
            handler.post(drawRunner)
        }

        override fun onTouchEvent(event: MotionEvent?) {
            scaleDetector.onTouchEvent(event)
            detector.onTouchEvent(event)
            super.onTouchEvent(event)
            draw()
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
            screenDimensions = Rect(0, 0, width, height)
            super.onSurfaceChanged(holder, format, width, height)
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null){
//                    canvas.scale(scaleFactor, scaleFactor)
                    canvas.drawColor(Color.BLACK)
                    canvas.drawBitmap(image, imageSrc, screenDimensions, null)
//                    Log.d(Log.DEBUG.toString(), "Drew canvas with src: $imageSrc")
                }
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
            if (visible) handler.postDelayed(drawRunner, 1000)
        }
    }
}