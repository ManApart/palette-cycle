package rak.pixellwp.cycling

import android.content.SharedPreferences
import android.graphics.Rect
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import rak.pixellwp.cycling.jsonModels.ImgJson

class CyclingWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return CyclingWallpaperEngine()
    }

    inner class CyclingWallpaperEngine : Engine() {
        private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@CyclingWallpaperService)

        private val drawRunner = PaletteDrawer(this, getBitmap())
        var imageSrc = Rect(prefs.getInt(LEFT, 0), prefs.getInt(TOP, 0), prefs.getInt(RIGHT, drawRunner.image.width), prefs.getInt(BOTTOM, drawRunner.image.height))
        var screenDimensions = Rect(imageSrc)
        private var scaleFactor = prefs.getFloat(SCALE_FACTOR, 1f)


        private var minScaleFactor = 0.1f
        private val scaleDetector = ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                scaleFactor *= (detector?.scaleFactor ?: 1f)
                scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, 10f))
                prefs.edit().putFloat(SCALE_FACTOR, scaleFactor).apply()
                return true
            }
        })

        private val panDetector: GestureDetectorCompat = GestureDetectorCompat(applicationContext, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                val overlapLeft: Float = drawRunner.image.width - screenDimensions.width()/scaleFactor
                val overLapTop: Float = drawRunner.image.height - screenDimensions.height()/scaleFactor

                val left = clamp(imageSrc.left + distanceX/scaleFactor, 0f, overlapLeft)
                val top = clamp(imageSrc.top + distanceY/scaleFactor, 0f, overLapTop)

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
            drawRunner.startDrawing()
        }

        override fun onTouchEvent(event: MotionEvent?) {
            if (isPreview) {
                scaleDetector.onTouchEvent(event)
                panDetector.onTouchEvent(event)
                super.onTouchEvent(event)
                drawRunner.startDrawing()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            drawRunner.setVisible(visible)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            drawRunner.setVisible(false)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            screenDimensions = Rect(0, 0, width, height)
            if (orientationHasChanged(width, height)) {
                val right = imageSrc.left + imageSrc.height()
                val bottom = imageSrc.top + imageSrc.width()
                imageSrc = Rect(imageSrc.left, imageSrc.top, right, bottom)
            }
            determineMinScaleFactor()
            super.onSurfaceChanged(holder, format, width, height)
        }

        private fun determineMinScaleFactor() {
            //Find the smallest scale factor that leaves no border on one side
            val w: Float = screenDimensions.width() / drawRunner.image.width.toFloat()
            val h: Float = screenDimensions.height() / drawRunner.image.height.toFloat()
            minScaleFactor = Math.max(w, h)
        }

        private fun clamp(value: Float, min: Float, max: Float) : Float{
            return Math.min(Math.max(value, min), max)
        }

        private fun orientationHasChanged(width: Int, height: Int) =
                (imageSrc.width() > imageSrc.height()) != (width > height)

        private fun getBitmap() : ColorCyclingImage {
            val json = this@CyclingWallpaperService.assets.open("SampleFile.json")
            val img: ImgJson = jacksonObjectMapper().readValue(json)
            return ColorCyclingImage(img)
        }

    }
}