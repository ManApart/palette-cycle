package rak.pixellwp.cycling

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImgJson
import java.util.*

class Bitmap(img: ImgJson) {
    val width = img.width
    val height = img.height
    private val startTime = Date().time
    private val palette = Palette(img.getParsedColors(), img.cycles)
    private val pixels = img.pixels
    private val bitmap: Bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)

    var usedColors = listOf<Int>().toMutableList()

    init {
        createBitmap()
    }

    override fun toString(): String {
        return "image with wth dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    private fun createBitmap() {
        var j = 0
        for (y in 0 until height){
            for (x in 0 until width) {
                val color = palette.colors[pixels[j]]
                bitmap.setPixel(x, y, color)
                usedColors.add(color)
                j++
            }
        }
    }

    fun advance(){
        val timePassed = Date().time - startTime
        Log.d("cycling", "cycling with time passed: $timePassed")
//        palette.cycle(timePassed)

        //eventually optimize and only draw active pixels over old bitmap
        Log.d("create bitmap", "drawing $width x $height pixels")
        var j = 0
        for (y in 0 until height){
            for (x in 0 until width) {
                val color = usedColors[j]
//                bitmap.setPixel(x, y, color)
                j++
            }
        }
        Log.d("create bitmap", "drawing done")
        bitmap.setPixel(300,300, Color.BLUE)
    }

    fun render() : Bitmap{
        return bitmap
    }

}