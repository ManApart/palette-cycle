package rak.pixellwp.cycling.wallpaperService

import android.content.Context
import android.view.ScaleGestureDetector
import rak.pixellwp.cycling.SCALE_FACTOR
import kotlin.math.max
import kotlin.math.min

fun CyclingWallpaperService.CyclingWallpaperEngine.scaleDetector(applicationContext: Context): ScaleGestureDetector{
    return ScaleGestureDetector(applicationContext, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            incrementScaleFactor(detector?.scaleFactor ?: 1f)
            return true
        }
    })
}

private fun CyclingWallpaperService.CyclingWallpaperEngine.incrementScaleFactor(incrementFactor: Float) {
    scaleFactor *= incrementFactor
    scaleFactor = max(minScaleFactor, min(scaleFactor, 10f))
    prefs.edit().putFloat(SCALE_FACTOR, scaleFactor).apply()
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.determineMinScaleFactor() {
    //Find the smallest scale factor that leaves no border on one side
    val w: Float = screenDimensions.width() / drawRunner.image.getImageWidth().toFloat()
    val h: Float = screenDimensions.height() / drawRunner.image.getImageHeight().toFloat()
    minScaleFactor = max(w, h)
}