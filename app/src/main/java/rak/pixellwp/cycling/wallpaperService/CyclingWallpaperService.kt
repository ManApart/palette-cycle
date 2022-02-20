package rak.pixellwp.cycling.wallpaperService

import android.content.*
import android.graphics.Rect
import android.service.wallpaper.WallpaperService
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.preference.PreferenceManager
import rak.pixellwp.cycling.jsonLoading.ImageLoadedListener
import rak.pixellwp.cycling.jsonLoading.ImageLoader
import rak.pixellwp.cycling.jsonModels.ImageInfo
import rak.pixellwp.cycling.jsonModels.defaultImageJson
import rak.pixellwp.cycling.models.*
import rak.pixellwp.cycling.*

const val cyclingWallpaperLogTag = "CyclingWallpaperService"

class CyclingWallpaperService : WallpaperService() {
    internal val imageLoader: ImageLoader by lazy { ImageLoader(this) }

    override fun onCreateEngine(): Engine {
        return CyclingWallpaperEngine()
    }

    inner class CyclingWallpaperEngine : Engine(), ImageLoadedListener {
        //create reference so extension function files have access
        internal val imageLoader get() = this@CyclingWallpaperService.imageLoader

        init {
            imageLoader.addLoadListener(this)
        }

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@CyclingWallpaperService)

        internal var imageCollection = prefs.getString(IMAGE_COLLECTION, "") ?: "Seascape"
        internal var singleImage = prefs.getString(SINGLE_IMAGE, "") ?: "CORAL"
        internal var timelineImage = prefs.getString(TIMELINE_IMAGE, "") ?: "V26"
        internal val defaultImage = ImageInfo("DefaultImage", "DefaultImage", 0)
        internal var currentImage = defaultImage
        internal var currentImageType = prefs.getString(IMAGE_TYPE, "")?.toImageType() ?: ImageType.TIMELINE
        internal var weather = prefs.getString(WEATHER_TYPE, "")?.toWeatherType() ?: WeatherType.ANY
        internal var drawRunner = PaletteDrawer(this, ColorCyclingImage(defaultImageJson()))

        internal var imageSrc = Rect(prefs.getInt(LEFT, 0), prefs.getInt(TOP, 0), prefs.getInt(RIGHT, drawRunner.image.getImageWidth()), prefs.getInt(BOTTOM, drawRunner.image.getImageHeight()))
        internal var screenDimensions = Rect(imageSrc)
        internal var screenOffset: Float = 0f
        internal var parallax = prefs.getBoolean(PARALLAX, true)
        internal var adjustMode = prefs.getBoolean(ADJUST_MODE, false)
        internal var overrideTimeline = prefs.getBoolean(OVERRIDE_TIMELINE, false)
        internal var overrideTime = 500L
        internal var dayPercent = 0
        internal var scaleFactor = prefs.getFloat(SCALE_FACTOR, 5.3f)
        internal var minScaleFactor = 0.1f

        internal var lastHourChecked = prefs.getInt(LAST_HOUR_CHECKED, 0)

        private val scaleDetector = scaleDetector(applicationContext)
        private val panDetector = panDetector(applicationContext)
        private val timeReceiver = timeReceiver()
        private val preferenceListener = preferenceListener()

        init {
            changeImage(loadInitialImage())
            downloadFirstTimeImage()
            drawRunner.startDrawing()
        }

        override fun imageLoadComplete(image: ImageInfo) {
            changeImage(image)
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            PreferenceManager.getDefaultSharedPreferences(applicationContext).registerOnSharedPreferenceChangeListener(preferenceListener)
            registerReceiver(timeReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
            super.onCreate(surfaceHolder)
        }

        override fun onDestroy() {
            drawRunner.stop()
            unregisterReceiver(timeReceiver)
            PreferenceManager.getDefaultSharedPreferences(applicationContext).unregisterOnSharedPreferenceChangeListener(preferenceListener)
            super.onDestroy()
        }

        override fun onTouchEvent(event: MotionEvent?) {
            if (isPreview && event != null) {
                scaleDetector.onTouchEvent(event)
                panDetector.onTouchEvent(event)
                super.onTouchEvent(event)
                drawRunner.drawNow()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible) {
                reloadPrefs()
            }
            drawRunner.setVisible(visible)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            drawRunner.stop()
            super.onSurfaceDestroyed(holder)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            drawRunner.startDrawing()
            super.onSurfaceCreated(holder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            screenDimensions = Rect(0, 0, width, height)
            determineMinScaleFactor()
            if (orientationHasChanged(width, height)) {
                adjustImageSrc(0f, 0f)
            }
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
            screenOffset = xOffset
            drawRunner.drawNow()
        }

        private fun orientationHasChanged(width: Int, height: Int) =
            (imageSrc.width() > imageSrc.height()) != (width > height)
    }
}