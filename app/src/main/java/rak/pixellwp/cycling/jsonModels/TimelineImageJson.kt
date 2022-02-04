package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import rak.pixellwp.cycling.models.Timeline

@JsonIgnoreProperties(ignoreUnknown = true)
data class TimelineImageJson(val base: ImageJson, val palettes: Map<String, PaletteJson>, val timeline: Map<String, String>)
