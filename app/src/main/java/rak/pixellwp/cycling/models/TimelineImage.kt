package rak.pixellwp.cycling.models

import android.graphics.Bitmap
import android.util.Log
import rak.pixellwp.cycling.jsonModels.PaletteJson
import rak.pixellwp.cycling.jsonModels.TimelineImageJson

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
        if (!useTimeOverride) {
            overrideTime.setTime(System.currentTimeMillis())
        }
        base.palette = timeline.getCurrentPalette(base.palette, overrideTime.getTotalSeconds())
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
//        Log.d(logTag, "Set time override to $time, ${getTimeString(time)}, ${overrideTime.get24HourFormattedString()}")
    }

    fun stopTimeOverride() {
        useTimeOverride = false
//        Log.d(logTag, "Removed time override")
    }

    fun getOverrideTime() : Long {
        return overrideTime.getMilliseconds()
    }

}
