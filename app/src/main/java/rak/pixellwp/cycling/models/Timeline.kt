package rak.pixellwp.cycling.models

class Timeline(entries: Map<String, String>) {
    val entries = parseEntries(entries)

    private fun parseEntries(entries: Map<String, String>): Map<Int, String> {
        val map = HashMap<Int, String>()
        entries.forEach{ entry -> map[Integer.parseInt(entry.key)] = entry.value }
        return map
    }
}