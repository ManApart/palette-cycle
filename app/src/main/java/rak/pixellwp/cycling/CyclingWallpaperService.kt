package rak.pixellwp.cycling

import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import com.beust.klaxon.Klaxon
import rak.pixellwp.cycling.jsonModels.ColorJsonConverter
import rak.pixellwp.cycling.jsonModels.ImgJson
import java.io.InputStream

class CyclingWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return CyclingWallpaperEngine()
    }

    inner class CyclingWallpaperEngine : Engine() {
        private val handler = Handler()
        private val drawRunner = Runnable { draw() }
        private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@CyclingWallpaperService)

        private var visible = true

        private val image: Bitmap = getBitmap()

        private var imageSrc = Rect(prefs.getInt(LEFT, 0), prefs.getInt(TOP, 0), prefs.getInt(RIGHT, image.width), prefs.getInt(BOTTOM, image.height))
        private var screenDimensions = Rect(imageSrc)

        private var scaleFactor = prefs.getFloat(SCALE_FACTOR, 1f)
        private val scaleDetector = ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                scaleFactor *= (detector?.scaleFactor ?: 1f)
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5f))
                prefs.edit().putFloat(SCALE_FACTOR, scaleFactor).apply()
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
                        .putInt(LEFT, imageSrc.left)
                        .putInt(TOP, imageSrc.top)
                        .putInt(RIGHT, imageSrc.right)
                        .putInt(BOTTOM, imageSrc.bottom).apply()
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
            if (orientationHasChanged(width, height)) {
                val right = imageSrc.left + imageSrc.height()
                val bottom = imageSrc.top + imageSrc.width()
                imageSrc = Rect(imageSrc.left, imageSrc.top, right, bottom)
            }
            super.onSurfaceChanged(holder, format, width, height)
        }

        private fun orientationHasChanged(width: Int, height: Int) =
                (imageSrc.width() > imageSrc.height()) != (width > height)

        private fun getBitmap() : Bitmap {
            val img: ImgJson = Klaxon().converter(ColorJsonConverter).parse<ImgJson>(this@CyclingWallpaperService.assets.open("SampleFile.json"))!!
            return Bitmap(img)
        }

        private fun draw() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null && image != null){
                    Log.d("RAK", "Attempting to draw $image")
                    canvas.drawColor(Color.BLACK)
//                    val paint = Paint()
//                    paint.color = 1
//                    canvas.drawPaint(paint)
//                    canvas.drawBitmap(image.render(), imageSrc, screenDimensions, null)
                }
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
            }
            handler.removeCallbacks(drawRunner)
        }
    }
}