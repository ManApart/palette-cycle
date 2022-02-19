package rak.pixellwp.cycling

import android.content.Context
import android.view.ScaleGestureDetector
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

