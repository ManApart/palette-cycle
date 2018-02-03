package rak.pixellwp.cycling

import android.graphics.Bitmap
import rak.pixellwp.cycling.jsonModels.ImgJson

class Bitmap(img: ImgJson) {
    val width = img.width
    val height = img.height
    private val palette = Palette(img.getParsedColors(), img.cycles)
    private val pixels = img.pixels
    private val bitmap: Bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)

    override fun toString(): String {
        return "image with wth dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    fun render() : Bitmap{
        var j = 0
        for (y in 0 until height){
            for (x in 0 until width) {
                val color = palette.colors[pixels[j]]
                bitmap.setPixel(x, y, color)
                j++
            }
        }


        return bitmap
    }
}