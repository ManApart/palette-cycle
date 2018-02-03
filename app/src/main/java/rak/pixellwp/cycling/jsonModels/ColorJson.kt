package rak.pixellwp.cycling.jsonModels

import android.graphics.Color
//import com.beust.klaxon.Converter
//import com.beust.klaxon.JsonValue

class ColorJson(rgbArray: IntArray){
    val rgb = parseColor(rgbArray)

    private fun parseColor(rgbArray: IntArray) : Int{
        val r: Int = rgbArray[0] as Int
        val g: Int = rgbArray[1] as Int
        val b: Int = rgbArray[2] as Int
        return Color.rgb(r, g, b)
    }
}
