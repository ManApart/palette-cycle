package rak.pixellwp.example.kotlin.answers

import rak.pixellwp.cycling.models.Palette

class Timeline(entries: Map<String, String>, palettes: List<Palette>) {
    var timeToPalette: Map<Int, Palette> = parseEntries(entries, palettes)

    private fun parseEntries(entries: Map<String, String>, palettes: List<Palette>): Map<Int, Palette> {
        val newMap = HashMap<Int, Palette>()
        entries.forEach {entry ->  newMap[Integer.parseInt(entry.key)] = palettes.first{palette ->  palette.id == entry.value}}
        return newMap
    }

}