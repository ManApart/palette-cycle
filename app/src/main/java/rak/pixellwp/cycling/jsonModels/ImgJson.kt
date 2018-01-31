package rak.pixellwp.cycling.jsonModels

import rak.pixellwp.cycling.Cycle

data class ImgJson(val width: Int, val height: Int, val colors: List<ColorJson>, val cycles: List<Cycle>, val pixels: List<Int>){

    fun getParsedColors() : List<Int> {
        val result = colors.map { c ->  c.rgb}.toList()
        return result
    }


}
