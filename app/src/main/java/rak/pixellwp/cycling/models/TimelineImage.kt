package rak.pixellwp.cycling.models

import rak.pixellwp.cycling.jsonModels.TimelineImageJson

class TimelineImage(json: TimelineImageJson) {
    val base: ColorCyclingImage = ColorCyclingImage(json.base)
    val palettes: List<Palette> = json.palettes
    val timeline: Timeline = Timeline(json.timeline)
}