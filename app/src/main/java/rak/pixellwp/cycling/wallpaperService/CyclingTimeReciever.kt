package rak.pixellwp.cycling.wallpaperService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import rak.pixellwp.cycling.LAST_HOUR_CHECKED
import rak.pixellwp.cycling.models.TimelineImage
import java.util.*

fun CyclingWallpaperService.CyclingWallpaperEngine.timeReceiver(): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (currentImageType == ImageType.COLLECTION) {
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                if (lastHourChecked != hour) {
                    Log.d(cyclingWallpaperLogTag, "Hour passed ($lastHourChecked > $hour). Assessing possible image change")
                    lastHourChecked = hour
                    prefs.edit().putInt(LAST_HOUR_CHECKED, lastHourChecked).apply()
                    if (imageCollection != "") {
                        changeCollection()
                    }
                }
            }
        }
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.updateTimelineOverride(prefOverrideTimeline: Boolean, newOverrideTime: Long) {
    if (drawRunner.image is TimelineImage) {
        val image: TimelineImage = drawRunner.image as TimelineImage
        if (prefOverrideTimeline != overrideTimeline || newOverrideTime != image.getOverrideTime()) {
            if (prefOverrideTimeline) {
                image.setTimeOverride(newOverrideTime)
            } else {
                image.stopTimeOverride()
            }
            overrideTimeline = prefOverrideTimeline
            overrideTime = image.getOverrideTime()
        }
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.getTime(): Long {
    return if (overrideTimeline) {
        overrideTime
    } else {
        Calendar.getInstance().get(Calendar.MILLISECOND).toLong()
    }
}