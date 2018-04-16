package rak.pixellwp.cycling.models

import android.graphics.Bitmap
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ColorJson
import rak.pixellwp.cycling.jsonModels.ImageInfo
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
        if (!useTimeOverride) {
            overrideTime.setTimeToNow()
        }
        val newPalette = timeline.getCurrentPalette(base.getPalette(), overrideTime.getTotalSeconds())
        if (base.getPalette() != newPalette){
            base.setPalette(newPalette)
        }
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

    fun loadOverrides(imageInfo: ImageInfo){
        if (imageInfo.remap.isNotEmpty()){
            Log.d(logTag, "remapping ${imageInfo.name} with map ${imageInfo.remap.keys}")
            imageInfo.remap.entries.forEach { entry ->
                for (palette in palettes){
                    palette.baseColors[entry.key] = ColorJson(entry.value).rgb
                }
            }
        }
    }


    fun setTimeOverride(time: Long) {
        useTimeOverride = true
        overrideTime.setTime(time)
    }

    fun stopTimeOverride() {
        useTimeOverride = false
    }

    fun getOverrideTime() : Long {
        return overrideTime.getMilliseconds()
    }

}
