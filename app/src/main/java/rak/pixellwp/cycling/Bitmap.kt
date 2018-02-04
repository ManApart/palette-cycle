package rak.pixellwp.cycling

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImgJson
import java.util.*

class Bitmap(img: ImgJson) {
    val width = img.width
    val height = img.height
    private val startTime = Date().time
    private val palette = Palette(img.getParsedColors(), img.cycles)
    private val pixels = img.pixels.toList()
    private val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    init {
        createBitmap()
    }

    override fun toString(): String {
        return "image with dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    private fun createBitmap() {
        var j = 0
        for (y in 0 until height){
            for (x in 0 until width) {
                val color = palette.colors[pixels[j]]
                bitmap.setPixel(x, y, color)
                j++
            }
        }
    }

    fun advance(){
        val timePassed = Date().time - startTime
        Log.d("cycling", "cycling with time passed: $timePassed")
        palette.cycle(timePassed)

        //eventually optimize and only draw active pixels over old bitmap

        val canvas = Canvas(bitmap)
        val p = Paint()

        var j = 0
        for (y in 0 until height){
            for (x in 0 until width) {
                val color = palette.colors[pixels[j]]
                p.color = color
                canvas.drawPoint(x.toFloat(), y.toFloat(), p)
                j++

//                if (j > 10000) break
            }
        }

        bitmap.setPixel(300,300, Color.RED)
    }

    fun render() : Bitmap{
        return bitmap
    }

}