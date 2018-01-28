package rak.pixellwp

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.preference.PreferenceManager
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
        private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@ImageWallpaperService)

        private var visible = true
        private var imageSrc = Rect(prefs.getInt("left", 0), prefs.getInt("top", 0), prefs.getInt("right", image.width), prefs.getInt("bottom", image.height))
        private var screenDimensions = Rect(imageSrc)

        private var scaleFactor = 1f
        private val scaleDetector = ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                scaleFactor *= (detector?.scaleFactor ?: 1f)
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5f))
                return true
            }
        })

        private val panDetector: GestureDetectorCompat = GestureDetectorCompat(applicationContext, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                val left = imageSrc.left + distanceX
                val top = imageSrc.top + distanceY
                val right = left + screenDimensions.width() / scaleFactor
                val bottom = top + screenDimensions.height() / scaleFactor

                imageSrc = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                prefs.edit()
                        .putInt("left", imageSrc.left)
                        .putInt("top", imageSrc.top)
                        .putInt("right", imageSrc.right)
                        .putInt("bottom", imageSrc.bottom).apply()
//                Log.d(Log.DEBUG.toString(), "prefs: ${prefs.all}")
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })

        init {
            handler.post(drawRunner)
        }

        override fun onTouchEvent(event: MotionEvent?) {
            if (isPreview) {
                scaleDetector.onTouchEvent(event)
                panDetector.onTouchEvent(event)
                super.onTouchEvent(event)
                draw()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            Log.d(Log.DEBUG.toString(), "visibility changed")
            this.visible = visible
            if (visible) handler.post(drawRunner) else handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            Log.d(Log.DEBUG.toString(), "surface destroyed")
            super.onSurfaceDestroyed(holder)
            this.visible = false
            handler.removeCallbacks(drawRunner)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            Log.d(Log.DEBUG.toString(), "surface changed")
            screenDimensions = Rect(0, 0, width, height)
//            imageSrc = Rect(screenDimensions)
            super.onSurfaceChanged(holder, format, width, height)
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null){
                    canvas.drawColor(Color.BLACK)
                    canvas.drawBitmap(image, imageSrc, screenDimensions, null)
                    Log.d(Log.DEBUG.toString(), "drew $imageSrc")
                }
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
        }
    }
}