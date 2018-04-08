package rak.pixellwp.cycling.models

class Timeline(entries: Map<String, String>, palettes: List<Palette>) {
    val timeToPalette: Map<Int, Palette> = parseEntries(entries, palettes)

    private fun parseEntries(entries: Map<String, String>, palettes: List<Palette>): Map<Int, Palette> {
        val map = HashMap<Int, Palette>()
        entries.forEach{ entry -> map[Integer.parseInt(entry.key)] = palettes.first { it.id == entry.value } }
        return map
    }

    fun getPreviousPalette(currentTime: Int): kotlin.collections.Map.Entry<Int, Palette> {
        return timeToPalette.filter { it.key < currentTime}
                .entries.sortedByDescending { it.key }.firstOrNull()
                ?: timeToPalette.entries.sortedByDescending { it.key }.last()

    }

    fun getNextPalette(currentTime: Int): kotlin.collections.Map.Entry<Int, Palette>  {
        return timeToPalette.filter { it.key > currentTime}
                .entries.sortedByDescending { it.key }.lastOrNull()
                ?: timeToPalette.entries.sortedByDescending { it.key }.first()
    }

}