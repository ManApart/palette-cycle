package rak.pixellwp.cycling.wallpaperService

enum class WeatherType(val stringValue: String) {
    CLEAR("Clear"), CLOUDY("Cloudy"), RAIN("Rain"), ANY("Any")
}

fun String?.toWeatherType(): WeatherType {
    return WeatherType.values().firstOrNull { it.stringValue == this } ?: WeatherType.CLEAR
}