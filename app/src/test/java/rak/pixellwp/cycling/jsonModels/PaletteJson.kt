package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import rak.pixellwp.cycling.models.Cycle

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaletteJson (val colors: List<ColorJson>, val cycles: List<Cycle>){
    val parsedColors = colors.map { color -> color.rgb }
}

