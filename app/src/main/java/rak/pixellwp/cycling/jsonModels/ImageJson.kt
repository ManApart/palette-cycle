package rak.pixellwp.cycling.jsonModels

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import rak.pixellwp.cycling.models.Cycle

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageJson(val width: Int, val height: Int, val colors: List<ColorJson>, val cycles: List<Cycle>, val pixels: List<Int>){

    fun getParsedColors() : List<Int> {
        return colors.map { c ->  c.rgb}.toList()
    }


}
