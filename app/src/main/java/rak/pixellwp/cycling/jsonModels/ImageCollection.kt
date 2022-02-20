package rak.pixellwp.cycling.jsonModels

import android.util.Log
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import rak.pixellwp.cycling.models.getHourFromSeconds
import rak.pixellwp.cycling.models.getSecondsFromHour
import rak.pixellwp.cycling.models.getSecondsFromMilli
import rak.pixellwp.cycling.wallpaperService.WeatherType
import rak.pixellwp.cycling.wallpaperService.cyclingWallpaperLogTag
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
class ImageCollection(val name: String, val images: List<ImageInfo>) {
    @JsonCreator
    constructor(name: String, id: String = "", month: String = "", script: String = "", weather: WeatherType = WeatherType.CLEAR, images: List<ImageInfo>?) : this(name, images ?: listOf(ImageInfo(name, id, month = month, script = script, weather = weather)))

    override fun toString(): String {
        return "Collection: $name" + images.map { it.toString() }
    }

    fun getImageInfoForCollection(time: Long, weather: WeatherType): ImageInfo {
        val hour = getHourFromSeconds(getSecondsFromMilli(time))

        Log.v(cyclingWallpaperLogTag, "grabbing image info for collection $name at hour $hour with weather $weather")

        val weatherMatches = getWeatherMatches(weather)

        val info = weatherMatches
            .filter { it.startHour < hour }.maxByOrNull { it.startHour }
            ?: weatherMatches.minByOrNull { it.startHour }!!

        Log.d(cyclingWallpaperLogTag, "grabbed ${info.name} with hour ${info.startHour}")
        return info
    }

    private fun getWeatherMatches(weather: WeatherType): List<ImageInfo> {
        if (weather == WeatherType.ANY) return images

        return images.filter { it.weather == weather }.ifEmpty { images }
    }

}