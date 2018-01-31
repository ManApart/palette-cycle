package rak.pixellwp.cycling.jsonModels

import android.graphics.Color
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue

class ColorJson(private val rgb: List<Int>) : Converter<ColorJson> {
    override fun fromJson(jv: JsonValue): ColorJson {
        val r: Int = jv.array!![0] as Int
        val g: Int = jv.array!![0] as Int
        val b: Int = jv.array!![0] as Int
        return ColorJson(listOf(r,g,b))
    }

    override fun toJson(value: ColorJson): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getRGB() : Int {
        return Color.rgb(rgb[0], rgb[1], rgb[2])
    }
}