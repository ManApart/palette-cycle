package rak.pixellwp.cycling.models

import android.util.Log

class Timeline(entries: Map<String, String>, palettes: List<Palette>) {
    private val timeToPalette: Map<Int, Palette> = parseEntries(entries, palettes)

    init {
        Log.d("Timeline", "Initing timeline image with palettes at " + timeToPalette.keys.sorted())
    }

    private fun parseEntries(entries: Map<String, String>, palettes: List<Palette>): Map<Int, Palette> {
        val map = HashMap<Int, Palette>()
        entries.forEach{ entry -> map[Integer.parseInt(entry.key)] = palettes.first { it.id == entry.value } }
        return map
    }

    fun getPreviousPalette(currentTime: Int): kotlin.collections.Map.Entry<Int, Palette> {
        return timeToPalette.filter { it.key < currentTime}
                .entries.sortedBy { it.key }.lastOrNull()
                ?: timeToPalette.entries.sortedBy { it.key }.last()
    }

    fun getNextPalette(currentTime: Int): kotlin.collections.Map.Entry<Int, Palette>  {
        return timeToPalette.filter { it.key > currentTime}
                .entries.sortedBy { it.key }.firstOrNull()
                ?: timeToPalette.entries.sortedBy { it.key }.first()
    }

}