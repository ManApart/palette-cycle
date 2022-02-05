package rak.pixellwp.cycling

import android.content.*
import android.graphics.Rect
import android.preference.PreferenceManager
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceHolder
import androidx.core.view.GestureDetectorCompat
import rak.pixellwp.cycling.jsonLoading.ImageLoadedListener
import rak.pixellwp.cycling.jsonLoading.ImageLoader
import rak.pixellwp.cycling.jsonModels.ImageInfo
import rak.pixellwp.cycling.models.TimelineImage
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

        private var imageCollection: String = prefs.getString(IMAGE_COLLECTION, "") ?: ""
        private var singleImage: String = prefs.getString(SINGLE_IMAGE, "") ?: ""
        private var timelineImage: String = prefs.getString(TIMELINE_IMAGE, "") ?: ""
        private val defaultImage = ImageInfo("DefaultImage", "DefaultImage", 0)
        private var currentImage = defaultImage

        private var drawRunner = PaletteDrawer(this, imageLoader.loadImage(defaultImage))

        private var imageSrc = Rect(prefs.getInt(LEFT, 0), prefs.getInt(TOP, 0), prefs.getInt(RIGHT, drawRunner.image.getImageWidth()), prefs.getInt(BOTTOM, drawRunner.image.getImageHeight()))
        var screenDimensions = Rect(imageSrc)
        private var screenOffset: Float = 0f
        private var parallax = prefs.getBoolean(PARALLAX, true)
        private var overrideTimeline = prefs.getBoolean(OVERRIDE_TIMELINE, false)
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

        private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { preference: SharedPreferences, newValue: Any ->
            if (isPreview) {
                val prefCollectionVal = preference.getString(IMAGE_COLLECTION, imageCollection) ?: ""
                val prefImageVal = preference.getString(SINGLE_IMAGE, singleImage) ?: ""
                val prefTimelineVal = prefs.getString(TIMELINE_IMAGE, timelineImage) ?: ""
                parallax = preference.getBoolean(PARALLAX, parallax)
                val prefOverrideTimeline =
                    preference.getBoolean(OVERRIDE_TIMELINE, overrideTimeline)
                val prefOverrideTime = preference.getLong(OVERRIDE_TIME, System.currentTimeMillis())

                imageSrc = Rect(
                    preference.getInt(LEFT, imageSrc.left),
                    preference.getInt(TOP, imageSrc.top),
                    preference.getInt(RIGHT, imageSrc.right),
                    preference.getInt(BOTTOM, imageSrc.bottom)
                )


                if (imageCollection != prefCollectionVal && prefCollectionVal != "") {
                    Log.d(
                        logTag,
                        "Image collection: $imageCollection > $prefCollectionVal for engine $this"
                    )
                    imageCollection = prefCollectionVal
                    singleImage = ""
                    timelineImage = ""
                    preference.edit().putString(SINGLE_IMAGE, "").apply()
                    preference.edit().putString(TIMELINE_IMAGE, "").apply()
                    changeCollection(imageCollection)

                } else if (timelineImage != prefTimelineVal && prefTimelineVal != "") {
                    Log.d(
                        logTag,
                        "Timeline image: $timelineImage > $prefTimelineVal for engine $this"
                    )
                    timelineImage = prefTimelineVal
                    imageCollection = ""
                    singleImage = ""
                    preference.edit().putString(SINGLE_IMAGE, "").apply()
                    preference.edit().putString(IMAGE_COLLECTION, "").apply()
                    changeTimeline(timelineImage)
                } else if (singleImage != prefImageVal && prefImageVal != "") {
                    Log.d(logTag, "Single image: $singleImage > $prefImageVal for engine $this")
                    singleImage = prefImageVal
                    imageCollection = ""
                    timelineImage = ""
                    preference.edit().putString(TIMELINE_IMAGE, "").apply()
                    preference.edit().putString(IMAGE_COLLECTION, "").apply()
                    changeImage(singleImage)
                }

                updateTimelineOverride(prefOverrideTimeline, prefOverrideTime)

            } else {
                reloadPrefs()
            }
        }

        private val timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                if (lastHourChecked != hour) {
                    Log.d(logTag, "Hour passed ($lastHourChecked > $hour). Assessing possible image change")
                    lastHourChecked = hour
                    prefs.edit().putInt(LAST_HOUR_CHECKED, lastHourChecked).apply()
                    if (imageCollection != "") {
                        changeCollection(imageCollection!!)
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
            Log.v(logTag, "Load initial image img= $singleImage, collection= $imageCollection, timeline= $timelineImage, drawer= ${drawRunner.id}")
            return when {
                imageCollection != "" -> imageLoader.getImageInfoForCollection(imageCollection)
                timelineImage != "" -> imageLoader.getImageInfoForTimeline(timelineImage)
                singleImage != "" -> imageLoader.getImageInfoForImage(singleImage)
                else -> defaultImage
            }
        }

        private fun downloadFirstTimeImage() {
            if (imageCollection == "" && singleImage == "" && timelineImage == "") {
                changeCollection("Waterfall")
            }
        }

        private fun changeCollection(collectionName: String) {
            val image = imageLoader.getImageInfoForCollection(collectionName)
            changeImage(image)
        }

        private fun changeImage(imageName: String) {
            val image = imageLoader.getImageInfoForImage(singleImage) //TODO - should this be image name?
            changeImage(image)
        }

        private fun changeTimeline(timelineName: String) {
            val image = imageLoader.getImageInfoForTimeline(timelineImage)
            changeImage(image)
        }

        private fun changeImage(image: ImageInfo) {
            if (image != currentImage) {
                Log.d(logTag, "Changing from ${currentImage.name} to ${image.name}.")
                if (imageLoader.imageIsReady(image)) {
                    currentImage = image
                    if (image.isTimeline){
                        drawRunner.image = imageLoader.loadTimelineImage(image)
                    } else {
                        drawRunner.image = imageLoader.loadImage(image)
                    }
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
            val prefCollectionVal = prefs.getString(IMAGE_COLLECTION, "") ?: ""
            val prefImageVal = prefs.getString(SINGLE_IMAGE, "") ?: ""
            val prefTimelineVal = prefs.getString(TIMELINE_IMAGE, "") ?: ""
            val prefOverrideTimeline = prefs.getBoolean(OVERRIDE_TIMELINE, overrideTimeline)
            val prefOverrideTime = prefs.getLong(OVERRIDE_TIME, System.currentTimeMillis())

            if (singleImage != prefImageVal || imageCollection != prefCollectionVal || timelineImage != prefTimelineVal || prefOverrideTimeline != overrideTimeline) {
                Log.v(logTag, "Reload prefs: img= $singleImage: $prefImageVal, collection= $imageCollection: $prefCollectionVal, timeline= $timelineImage: $prefTimelineVal, timeline override: ${if (prefOverrideTimeline) "$prefOverrideTime" else "off"} drawer= ${drawRunner.id}")
            }

            parallax = prefs.getBoolean(PARALLAX, parallax)

            imageSrc = Rect(prefs.getInt(LEFT, imageSrc.left),
                    prefs.getInt(TOP, imageSrc.top),
                    prefs.getInt(RIGHT, imageSrc.right),
                    prefs.getInt(BOTTOM, imageSrc.bottom))

            imageCollection = prefCollectionVal
            singleImage = prefImageVal
            timelineImage = prefTimelineVal

            updateTimelineOverride(prefOverrideTimeline, prefOverrideTime)

            when {
                prefCollectionVal != "" -> changeCollection(imageCollection)
                prefImageVal != "" -> changeImage(singleImage)
                prefTimelineVal != "" -> changeTimeline(timelineImage)
            }
        }

        private fun updateTimelineOverride(prefOverrideTimeline: Boolean, prefOverrideTime: Long) {
            if (timelineImage != "" && drawRunner.image is TimelineImage) {
                val image: TimelineImage = drawRunner.image as TimelineImage
                if (prefOverrideTimeline != overrideTimeline || prefOverrideTime != image.getOverrideTime()){
                    if (prefOverrideTimeline) {
                        image.setTimeOverride(prefOverrideTime)
                    } else {
                        image.stopTimeOverride()
                    }
                    overrideTimeline = prefOverrideTimeline
                }
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
                val totalPossibleOffset = drawRunner.image.getImageWidth() - imageSrc.width()
                val offsetPixels = totalPossibleOffset * screenOffset
                val left = offsetPixels.toInt()
                return Rect(left, imageSrc.top, left + imageSrc.width(), imageSrc.bottom)
            }
            return imageSrc
        }

        private fun adjustImageSrc(distanceX: Float, distanceY: Float) {
            val overlapLeft: Float = drawRunner.image.getImageWidth() - screenDimensions.width() / scaleFactor
            val overLapTop: Float = drawRunner.image.getImageHeight() - screenDimensions.height() / scaleFactor

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
            val w: Float = screenDimensions.width() / drawRunner.image.getImageWidth().toFloat()
            val h: Float = screenDimensions.height() / drawRunner.image.getImageHeight().toFloat()
            minScaleFactor = Math.max(w, h)
        }

        private fun clamp(value: Float, min: Float, max: Float): Float {
            return Math.min(Math.max(value, min), max)
        }

        private fun orientationHasChanged(width: Int, height: Int) =
                (imageSrc.width() > imageSrc.height()) != (width > height)
    }
}