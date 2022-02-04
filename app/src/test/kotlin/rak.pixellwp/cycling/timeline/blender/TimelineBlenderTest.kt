package rak.pixellwp.cycling.timeline.blender

import rak.pixellwp.cycling.TimelineBlender
import rak.pixellwp.cycling.models.Palette
import rak.pixellwp.cycling.models.getSecondsFromHour

fun createTimelineBlender() : TimelineBlender {
    val map = HashMap<Int, Palette>()
    map[0] = Palette("", listOf(), listOf())
    return TimelineBlender(map.entries.first())
}

fun createEntry(hour: Int) : Map.Entry<Int, Palette>{
    val map = HashMap<Int, Palette>()
    map[getSecondsFromHour(hour)] = Palette("", listOf(), listOf())
    return map.entries.first()
}