package rak.pixellwp.example.kotlin

import android.util.Log
import rak.pixellwp.cycling.models.Palette
import rak.pixellwp.cycling.models.getTimeString

class Timeline(entries: Map<String, String>, palettes: List<Palette>) {
    private val logTag = "Timeline"
    private val timeToPalette: Map<Int, Palette> = parseEntries(entries, palettes)

    init {
        Log.d(logTag, "Initing timeline image with palettes at " + timeToPalette.keys.sorted().map { time -> "$time (${getTimeString(time)})" }.toList())
    }

    private fun parseEntries(entries: Map<String, String>, palettes: List<Palette>): Map<Int, Palette> {
        throw NotImplementedError()
    }

}