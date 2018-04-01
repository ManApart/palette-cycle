package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimelineImage(val base: ImageJson, val palettes: List<PaletteJson>, val timeline: List<Timeline>)
