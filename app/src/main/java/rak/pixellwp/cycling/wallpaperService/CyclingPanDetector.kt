package rak.pixellwp.cycling.wallpaperService

import android.content.Context
import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.content.edit
import androidx.core.math.MathUtils.clamp
import androidx.core.view.GestureDetectorCompat
import rak.pixellwp.cycling.*
import rak.pixellwp.cycling.models.getDayPercent
import rak.pixellwp.cycling.models.getTimeWithinDay
import kotlin.math.abs

fun CyclingWallpaperService.CyclingWallpaperEngine.panDetector(applicationContext: Context): GestureDetectorCompat {
    return GestureDetectorCompat(applicationContext, object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (adjustMode || currentImageType != ImageType.TIMELINE) {
                adjustImageSrc(distanceX, distanceY)
            } else {
                val distance = if (abs(distanceX) > abs(distanceY)) -distanceX else distanceY
                adjustTimeOverride(distance)
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    })
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.adjustImageSrc(distanceX: Float, distanceY: Float) {
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

private fun CyclingWallpaperService.CyclingWallpaperEngine.adjustTimeOverride(distanceX: Float) {
    val prefOverrideTimeline = prefs.getBoolean(OVERRIDE_TIMELINE, overrideTimeline)
    val newOverrideTime = getTimeWithinDay(overrideTime + distanceX.toLong() * 9000)
    dayPercent = getDayPercent(newOverrideTime)
    updateTimelineOverride(prefOverrideTimeline, newOverrideTime)
    prefs.edit {
        putLong(OVERRIDE_TIME, overrideTime)
        putInt(OVERRIDE_TIME_PERCENT, dayPercent)
    }
}