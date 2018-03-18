package rak.pixellwp.cycling

import android.content.*
import android.graphics.Rect
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.support.v4.view.GestureDetectorCompat
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import rak.pixellwp.cycling.jsonLoading.ImageLoadedListener
import rak.pixellwp.cycling.jsonLoading.ImageLoader
import rak.pixellwp.cycling.jsonModels.ImageInfo
import java.util.*


class CyclingWallpaperService : WallpaperService() {
    private val logTag = "CyclingWallpaperService"
    private lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        imageLoader = ImageLoader(this@CyclingWallpaperService)
        super.onCreate()
    }

    override fun onCreateEngine(): Engine {
        return CyclingWallpaperEngine()
    }

    inner class CyclingWallpaperEngine : Engine(), ImageLoadedListener {
        init {
            imageLoader.addLoadListener(this)
        }

        private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@CyclingWallpaperService)

        private var imageCollection = prefs.getString(IMAGE_COLLECTION, "")
        private var singleImage = prefs.getString(SINGLE_IMAGE, "")
        private val defaultImage = ImageInfo("DefaultImage", "DefaultImage", 0)
        private var currentImage = defaultImage

        private var drawRunner = PaletteDrawer(this, imageLoader.loadImage(defaultImage))

        private var imageSrc = Rect(prefs.getInt(LEFT, 0), prefs.getInt(TOP, 0), prefs.getInt(RIGHT, drawRunner.image.width), prefs.getInt(BOTTOM, drawRunner.image.height))
        var screenDimensions = Rect(imageSrc)
        private var screenOffset: Float = 0f
        private var parallax = prefs.getBoolean(PARALLAX, true)
        private var scaleFactor = prefs.getFloat(SCALE_FACTOR, 5.3f)
        private var minScaleFactor = 0.1f

        private var lastHourChecked = prefs.getInt(LAST_HOUR_CHECKED, 0)

        private val scaleDetector = ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                incrementScaleFactor(detector?.scaleFactor ?: 1f)
                return true
            }
        })

        private val panDetector = GestureDetectorCompat(applicationContext, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                adjustImageSrc(distanceX, distanceY)
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })

        private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener({ preference: SharedPreferences, newValue: Any ->
            if (isPreview) {
                val prefCollectionVal = preference.getString(IMAGE_COLLECTION, imageCollection)
                val prefImageVal = preference.getString(SINGLE_IMAGE, singleImage)
                parallax = preference.getBoolean(PARALLAX, parallax)

                imageSrc = Rect(preference.getInt(LEFT, imageSrc.left),
                        preference.getInt(TOP, imageSrc.top),
                        preference.getInt(RIGHT, imageSrc.right),
                        preference.getInt(BOTTOM, imageSrc.bottom))


                if (imageCollection != prefCollectionVal && prefCollectionVal != "") {
                    Log.d(logTag, "Image collection: $imageCollection > $prefCollectionVal for engine $this")
                    imageCollection = prefCollectionVal
                    singleImage = ""
                    preference.edit().putString(SINGLE_IMAGE, "").apply()
                    changeCollection(imageCollection)

                } else if (singleImage != prefImageVal && prefImageVal != "") {
                    Log.d(logTag, "Single image: $singleImage > $prefImageVal for engine $this")
                    singleImage = prefImageVal
                    imageCollection = ""
                    preference.edit().putString(IMAGE_COLLECTION, "").apply()
                    val image = imageLoader.getImageInfoForImage(singleImage)
                    changeImage(image)
                }
            } else {
                reloadPrefs()
            }
        })

        private val timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                if (lastHourChecked != hour) {
                    Log.d(logTag, "Hour passed ($lastHourChecked > $hour). Assessing possible image change")
                    lastHourChecked = hour
                    prefs.edit().putInt(LAST_HOUR_CHECKED, lastHourChecked).apply()
                    if (imageCollection != "") {
                        changeCollection(imageCollection)
                    }
                }
            }
        }

        init {
            changeImage(loadInitialImage())
            downloadFirstTimeImage()
            drawRunner.startDrawing()
        }

        private fun loadInitialImage(): ImageInfo {
            return when {
                imageCollection != "" -> imageLoader.getImageInfoForCollection(imageCollection)
                singleImage != "" -> imageLoader.getImageInfoForImage(singleImage)
                else -> defaultImage
            }
        }

        private fun downloadFirstTimeImage() {
            if (imageCollection == "" && singleImage == "") {
                changeCollection("Waterfall")
            }
        }

        private fun changeCollection(collectionName: String) {
            val image = imageLoader.getImageInfoForCollection(collectionName)
            changeImage(image)
        }

        private fun changeImage(image: ImageInfo) {
            if (image != currentImage) {
                Log.d(logTag, "Changing from ${currentImage.name} to ${image.name}.")
                if (imageLoader.imageIsReady(image)) {
                    currentImage = image
                    drawRunner.image = imageLoader.loadImage(image)
                    determineMinScaleFactor()

                } else {
                    imageLoader.downloadImage(image)
                }
            }
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
            if (isPreview) {
                scaleDetector.onTouchEvent(event)
                panDetector.onTouchEvent(event)
                super.onTouchEvent(event)
                drawRunner.drawNow()
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            if (visible){
                reloadPrefs()
            }
            drawRunner.setVisible(visible)
        }

        private fun reloadPrefs() {
            Log.v(logTag, "Reload prefs: img= $singleImage, drawer: ${drawRunner.id}")
            val prefCollectionVal = prefs.getString(IMAGE_COLLECTION, "")
            val prefImageVal = prefs.getString(SINGLE_IMAGE, "")
            parallax = prefs.getBoolean(PARALLAX, parallax)

            imageSrc = Rect(prefs.getInt(LEFT, imageSrc.left),
                    prefs.getInt(TOP, imageSrc.top),
                    prefs.getInt(RIGHT, imageSrc.right),
                    prefs.getInt(BOTTOM, imageSrc.bottom))

            imageCollection = prefCollectionVal
            singleImage = prefImageVal

            if (prefCollectionVal != "") {
                changeCollection(imageCollection)
            } else if (prefImageVal != "") {
                val image = imageLoader.getImageInfoForImage(singleImage)
                changeImage(image)
            }
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

        fun getOffsetImage(): Rect {
            if (parallax && !isPreview) {
                val totalPossibleOffset = drawRunner.image.width - imageSrc.width()
                val offsetPixels = totalPossibleOffset * screenOffset
                val left = offsetPixels.toInt()
                return Rect(left, imageSrc.top, left + imageSrc.width(), imageSrc.bottom)
            }
            return imageSrc
        }

        private fun adjustImageSrc(distanceX: Float, distanceY: Float) {
            val overlapLeft: Float = drawRunner.image.width - screenDimensions.width() / scaleFactor
            val overLapTop: Float = drawRunner.image.height - screenDimensions.height() / scaleFactor

            val left = clamp(imageSrc.left + distanceX / scaleFactor, 0f, overlapLeft)
            val top = clamp(imageSrc.top + distanceY / scaleFactor, 0f, overLapTop)

            val right = left + screenDimensions.width() / scaleFactor
            val bottom = top + screenDimensions.height() / scaleFactor

            imageSrc = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            prefs.edit()
                    .putInt(LEFT, imageSrc.left)
                    .putInt(TOP, imageSrc.top)
                    .putInt(RIGHT, imageSrc.right)
                    .putInt(BOTTOM, imageSrc.bottom).apply()
        }

        private fun incrementScaleFactor(incrementFactor: Float) {
            scaleFactor *= incrementFactor
            scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, 10f))
            prefs.edit().putFloat(SCALE_FACTOR, scaleFactor).apply()
        }

        private fun determineMinScaleFactor() {
            //Find the smallest scale factor that leaves no border on one side
            val w: Float = screenDimensions.width() / drawRunner.image.width.toFloat()
            val h: Float = screenDimensions.height() / drawRunner.image.height.toFloat()
            minScaleFactor = Math.max(w, h)
        }

        private fun clamp(value: Float, min: Float, max: Float): Float {
            return Math.min(Math.max(value, min), max)
        }

        private fun orientationHasChanged(width: Int, height: Int) =
                (imageSrc.width() > imageSrc.height()) != (width > height)
    }
}