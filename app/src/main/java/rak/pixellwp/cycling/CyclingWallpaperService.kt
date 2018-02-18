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
import rak.pixellwp.cycling.jsonLoading.ImageLoader
import rak.pixellwp.cycling.jsonLoading.JsonDownloadListener
import rak.pixellwp.cycling.jsonModels.ImageInfo
import java.util.*


class CyclingWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return CyclingWallpaperEngine()
    }

    inner class CyclingWallpaperEngine : Engine(), JsonDownloadListener {
        private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@CyclingWallpaperService)

        private val imageLoader: ImageLoader = ImageLoader(this@CyclingWallpaperService, this@CyclingWallpaperEngine)
        private var imageCollection = prefs.getString(IMAGE_COLLECTION, "")
        private var singleImage = prefs.getString(SINGLE_IMAGE, "")
        private var currentImage = loadInitialImage()

        private var drawRunner = PaletteDrawer(this, imageLoader.loadImage(currentImage))

        var imageSrc = Rect(prefs.getInt(LEFT, 0), prefs.getInt(TOP, 0), prefs.getInt(RIGHT, drawRunner.image.width), prefs.getInt(BOTTOM, drawRunner.image.height))
        var screenDimensions = Rect(imageSrc)
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

        private val imageCollectionListener = SharedPreferences.OnSharedPreferenceChangeListener({ preference: SharedPreferences, newValue: Any ->
            val prefCollectionVal = preference.getString(IMAGE_COLLECTION, imageCollection)
            val prefImageVal = preference.getString(SINGLE_IMAGE, singleImage)

            if (imageCollection != prefCollectionVal && prefCollectionVal != "") {
                Log.d("RAK", "Image collection: $imageCollection > $prefCollectionVal")
                imageCollection = prefCollectionVal
                singleImage = ""
                preference.edit().putString(SINGLE_IMAGE, "").apply()
                changeCollection(imageCollection)

            } else if (singleImage != prefImageVal && prefImageVal != "") {
                Log.d("RAK", "Single image: $singleImage > $prefImageVal")
                singleImage = prefImageVal
                imageCollection = ""
                preference.edit().putString(IMAGE_COLLECTION, "").apply()
                val image = imageLoader.getImageInfoForImage(singleImage)
                changeImage(image)
            }
        })

        private val timeReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                Log.d("wallpaper service", "time check")
                if (lastHourChecked != hour){
                    Log.d("Time Checker", "Hour passed ($lastHourChecked > $hour). Assessing possible image change")
                    lastHourChecked = hour
                    prefs.edit().putInt(LAST_HOUR_CHECKED, lastHourChecked).apply()
                    if (imageCollection != ""){
                        changeCollection(imageCollection)
                    }
                }
            }

        }

        init {
            drawRunner.startDrawing()
        }

        override fun downloadComplete(image: ImageInfo) {
            changeImage(image, true)
        }

        private fun loadInitialImage() : ImageInfo{
            if (imageCollection != ""){
                return imageLoader.getImageInfoForCollection(imageCollection)
            } else if (singleImage != "") {
                return imageLoader.getImageInfoForImage(singleImage)
            }
            return imageLoader.getImageInfoForCollection("Seascape")
        }

        private fun changeCollection(collectionName: String) {
            val image = imageLoader.getImageInfoForCollection(collectionName)
            changeImage(image)
        }

        private fun changeImage(image: ImageInfo, force: Boolean = false) {
            if (image != currentImage || force) {
                Log.d("Engine", "Changing from ${currentImage.name} to ${image.fileName}")
                currentImage = image
                drawRunner.stop()
                drawRunner = PaletteDrawer(this, imageLoader.loadImage(image))
                determineMinScaleFactor()
                drawRunner.startDrawing()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            PreferenceManager.getDefaultSharedPreferences(applicationContext).registerOnSharedPreferenceChangeListener(imageCollectionListener)
            registerReceiver(timeReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
            super.onCreate(surfaceHolder)
        }

        override fun onDestroy() {
            super.onDestroy()
            unregisterReceiver(timeReceiver)
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

        private fun clamp(value: Float, min: Float, max: Float) : Float{
            return Math.min(Math.max(value, min), max)
        }

        private fun orientationHasChanged(width: Int, height: Int) =
                (imageSrc.width() > imageSrc.height()) != (width > height)
    }
}