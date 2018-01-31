package rak.pixellwp.cycling.jsonModels

import rak.pixellwp.cycling.Cycle

//data class ImgJson(val width: Int, val height: Int, val cycles: List<Cycle>, val pixels: List<Int>){
data class ImgJson(val width: Int, val height: Int, val colors: List<ColorJson>, val cycles: List<Cycle>, val pixels: List<Int>){

//    val parsedColors = colors.map { c ->  c.getRGB()}.toList()
        val parsedColors = listOf(1)
//    val pixels = listOf<Int>()
//    val cycles = listOf<Cycle>()

}
