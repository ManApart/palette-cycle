package rak.pixellwp.cycling.models

import android.util.Log
import rak.pixellwp.cycling.jsonModels.PaletteJson
import rak.pixellwp.cycling.jsonModels.TimelineImageJson
import java.util.*

class TimelineImage(json: TimelineImageJson) {
    val base: ColorCyclingImage = ColorCyclingImage(json.base)
    val palettes: List<Palette> = parsePalettes(json.palettes)
    val timeline: Timeline = Timeline(json.timeline)
    private var oldTimePassedInSeconds = -1
    private var currentPalette = palettes.first()
    private val logTag = "Timeline Image"


    private fun parsePalettes(jsonPalettes: Map<String, PaletteJson>): List<Palette> {
       return jsonPalettes.map { entry -> Palette(Integer.parseInt(entry.key), entry.value) }.toList()
    }

    fun getCurrentPalette() : Palette {
        val currentTime = getTimePassedInSeconds()
        if (oldTimePassedInSeconds == currentTime){
            return currentPalette
        }
        oldTimePassedInSeconds = currentTime

        val previous = getPreviousPalette(currentTime)
        val next = getNextPalette(currentTime)
        Log.d(logTag, "Blending palettes for ${previous.startSecond} and ${next.startSecond} with current time $currentTime")

        currentPalette = previous.blendPalette(next, currentTime)
        return currentPalette
    }

    private fun getPreviousPalette(currentTime: Int): Palette {
        return palettes.filter { it.startSecond < currentTime}
                .sortedByDescending { it.startSecond }
                .firstOrNull()
                ?: palettes.sortedByDescending { it.startSecond }.last()
    }

    private fun getNextPalette(currentTime: Int): Palette {
        return palettes.filter { it.startSecond > currentTime}
                .sortedByDescending { it.startSecond }
                .lastOrNull()
                ?: palettes.sortedByDescending { it.startSecond }.first()
    }

    private fun getTimePassedInSeconds() : Int {
        return Calendar.getInstance().get(Calendar.SECOND)
    }
}