package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import rak.pixellwp.cycling.wallpaperService.WeatherType

@JsonIgnoreProperties(ignoreUnknown = true)
class ImageCollection(val name: String, val images: List<ImageInfo>) {
    @JsonCreator
    constructor(name: String, id: String = "", month: String = "", script: String = "", weather: WeatherType = WeatherType.CLEAR, images: List<ImageInfo>?) : this(name, images ?: listOf(ImageInfo(name, id, month = month, script = script, weather = weather)))

    override fun toString(): String {
        return "Collection: $name" + images.map { it.toString() }
    }
}