package rak.pixellwp.cycling.wallpaperService

import android.content.SharedPreferences
import android.graphics.Rect
import android.util.Log
import rak.pixellwp.cycling.*
import rak.pixellwp.cycling.models.getTimeWithinDay
import rak.pixellwp.cycling.models.maxMilliseconds

fun CyclingWallpaperService.CyclingWallpaperEngine.preferenceListener(): SharedPreferences.OnSharedPreferenceChangeListener {
    return SharedPreferences.OnSharedPreferenceChangeListener { preference: SharedPreferences, newValue: Any ->
        if (newValue == OVERRIDE_TIME
            || newValue == LAST_HOUR_CHECKED
            || (newValue == OVERRIDE_TIME_PERCENT && dayPercent == preference.getInt(OVERRIDE_TIME_PERCENT, 50))
        ) {
            return@OnSharedPreferenceChangeListener
        }

        dayPercent = preference.getInt(OVERRIDE_TIME_PERCENT, 50)
        overrideTime = getTimeWithinDay(maxMilliseconds * dayPercent / 100)
        prefs.edit().putLong(OVERRIDE_TIME, overrideTime).apply()

        if (isPreview) {
            val prevImageCollection = imageCollection
            val prevSingleImage = singleImage
            val prevTimeline = timelineImage
            imageCollection = preference.getString(IMAGE_COLLECTION, imageCollection) ?: imageCollection
            singleImage = preference.getString(SINGLE_IMAGE, singleImage) ?: singleImage
            timelineImage = preference.getString(TIMELINE_IMAGE, timelineImage) ?: timelineImage
            parallax = preference.getBoolean(PARALLAX, parallax)
            adjustMode = preference.getBoolean(ADJUST_MODE, false)
            val prefOverrideTimeline = preference.getBoolean(OVERRIDE_TIMELINE, overrideTimeline)

            currentImageType = preference.getString(IMAGE_TYPE, "")?.toImageType() ?: ImageType.TIMELINE
            weather = preference.getString(WEATHER_TYPE, "")?.toWeatherType() ?: WeatherType.ANY

            imageSrc = Rect(
                preference.getInt(LEFT, imageSrc.left),
                preference.getInt(TOP, imageSrc.top),
                preference.getInt(RIGHT, imageSrc.right),
                preference.getInt(BOTTOM, imageSrc.bottom)
            )

            when {
                currentImageType == ImageType.TIMELINE && prevTimeline != timelineImage -> {
                    Log.d(cyclingWallpaperLogTag, "Timeline image: $timelineImage for engine $this")
                    changeTimeline()
                }
                currentImageType == ImageType.COLLECTION && prevImageCollection != imageCollection -> {
                    Log.d(cyclingWallpaperLogTag, "Image collection: $imageCollection for engine $this")
                    changeCollection()
                }
                prevSingleImage != singleImage -> {
                    Log.d(cyclingWallpaperLogTag, "Single image: $singleImage for engine $this")
                    changeImage()
                }
            }

            updateTimelineOverride(prefOverrideTimeline, overrideTime)

        } else {
            reloadPrefs()
        }
    }
}

internal fun CyclingWallpaperService.CyclingWallpaperEngine.reloadPrefs() {
    imageCollection = prefs.getString(IMAGE_COLLECTION, "") ?: imageCollection
    singleImage = prefs.getString(SINGLE_IMAGE, "") ?: singleImage
    timelineImage = prefs.getString(TIMELINE_IMAGE, "") ?: timelineImage
    val prefOverrideTimeline = prefs.getBoolean(OVERRIDE_TIMELINE, overrideTimeline)
    val newOverrideTime = prefs.getLong(OVERRIDE_TIME, 5000L)
    currentImageType = prefs.getString(IMAGE_TYPE, "")?.toImageType() ?: ImageType.TIMELINE
    weather = prefs.getString(WEATHER_TYPE, "")?.toWeatherType() ?: WeatherType.ANY

    parallax = prefs.getBoolean(PARALLAX, parallax)
    adjustMode = prefs.getBoolean(ADJUST_MODE, false)

    imageSrc = Rect(
        prefs.getInt(LEFT, imageSrc.left),
        prefs.getInt(TOP, imageSrc.top),
        prefs.getInt(RIGHT, imageSrc.right),
        prefs.getInt(BOTTOM, imageSrc.bottom)
    )

    updateTimelineOverride(prefOverrideTimeline, newOverrideTime)

    when {
        currentImageType == ImageType.TIMELINE && timelineImage != "" -> changeTimeline()
        currentImageType == ImageType.COLLECTION && imageCollection != "" -> changeCollection()
        currentImageType == ImageType.SINGLE && singleImage != "" -> changeImage()
    }
}