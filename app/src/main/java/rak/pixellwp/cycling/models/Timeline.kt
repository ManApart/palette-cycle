package rak.pixellwp.cycling.models

import android.util.Log
import rak.pixellwp.cycling.TimelineBlender
import java.util.*

class Timeline(entries: Map<String, String>, palettes: List<Palette>) {
    private val logTag = "Timeline"
    private val timeToPalette: TreeMap<Int, Palette> = parseEntries(entries, palettes)
    private val blender = TimelineBlender(timeToPalette.entries.first())

    init {
        Log.d(logTag, "Initing timeline image with palettes at " + timeToPalette.keys.sorted().map { time -> "$time" }.toList())
    }

    private fun parseEntries(entries: Map<String, String>, palettes: List<Palette>): TreeMap<Int, Palette> {
        val map = TreeMap<Int, Palette>()
        entries.forEach{ entry -> map[Integer.parseInt(entry.key)] = palettes.first { it.id == entry.value } }
        return map
    }

    fun getCurrentPalette(current: Palette, currentTime: Int) : Palette{
        return blender.getCurrentPalette(current, currentTime, getPreviousPalette(currentTime), getNextPalette(currentTime))
    }

    fun getPreviousPalette(currentTime: Int): Map.Entry<Int, Palette> {
        return timeToPalette.lowerEntry(currentTime)?:timeToPalette.lastEntry()!!
    }

    fun getNextPalette(currentTime: Int): Map.Entry<Int, Palette>  {
        return timeToPalette.higherEntry(currentTime)?:timeToPalette.firstEntry()!!
    }

}