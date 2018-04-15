package rak.pixellwp.cycling.models

import android.graphics.Bitmap
import android.util.Log
import rak.pixellwp.cycling.jsonModels.PaletteJson
import rak.pixellwp.cycling.jsonModels.TimelineImageJson
import java.util.*

class TimelineImage(json: TimelineImageJson) : PaletteImage {
    private val base: ColorCyclingImage = ColorCyclingImage(json.base)
    private val palettes: List<Palette> = parsePalettes(json.palettes)
    private val timeline: Timeline = Timeline(json.timeline, palettes)
    private val logTag = "Timeline Image"
    private var useTimeOverride = false
    private var overrideTime = DaySeconds()

    private fun parsePalettes(jsonPalettes: Map<String, PaletteJson>): List<Palette> {
        return jsonPalettes.map { entry -> Palette(entry.key, entry.value) }.toList()
    }

    override fun advance(timePassed: Int) {
        val currentTime = getSeconds()
        base.palette = timeline.getCurrentPalette(currentTime)
        base.advance(timePassed)
    }

    override fun getBitmap(): Bitmap {
        return base.getBitmap()
    }

    override fun getImageWidth(): Int {
        return base.getImageWidth()
    }

    override fun getImageHeight(): Int {
        return base.getImageHeight()
    }

    fun setTimeOverride(time: Long) {
        useTimeOverride = true
        overrideTime.setTime(time)
        Log.d(logTag, "Set time override to $time, ${getTimeString(time)}, ${overrideTime.getHourMinuteString()}")
    }

    fun stopTimeOverride() {
        useTimeOverride = false
        Log.d(logTag, "Removed time override")
    }

    private fun getSeconds(): Int {
        if (!useTimeOverride) {
            overrideTime.setTime(System.currentTimeMillis())
        }
        return overrideTime.getSecondsInDay()
    }

}
