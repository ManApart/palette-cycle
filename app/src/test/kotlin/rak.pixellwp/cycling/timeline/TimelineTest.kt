package rak.pixellwp.cycling.timeline

import rak.pixellwp.cycling.models.Palette
import rak.pixellwp.cycling.models.Timeline
import rak.pixellwp.cycling.models.getSecondsFromHour

class TimelineTest

fun createTimeline(times: List<Int>) : Timeline {
    val entries = HashMap<String, String>()
    val palettes = mutableListOf<Palette>()

    for (time in times){
        val seconds = getSecondsFromHour(time)
        val id = "P$seconds"
        entries["$seconds"] = id
        palettes.add(Palette(id, listOf(), listOf()))
    }

    return Timeline(entries, palettes)
}