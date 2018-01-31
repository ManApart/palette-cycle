package rak.pixellwp.cycling.jsonModels

import android.graphics.Color
import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue

class ColorJson(val rgb: Int)

    val ColorJsonConverter = object : Converter<ColorJson> {
    override fun fromJson(jv: JsonValue): ColorJson {
        val r: Int = jv.array!![0] as Int
        val g: Int = jv.array!![0] as Int
        val b: Int = jv.array!![0] as Int
        return ColorJson(Color.rgb(r, g, b))
    }

    override fun toJson(value: ColorJson): String? {
        val r = Color.red(value.rgb)
        val g = Color.green(value.rgb)
        val b = Color.blue(value.rgb)
        return "[$r,$g,$b]"
    }

}