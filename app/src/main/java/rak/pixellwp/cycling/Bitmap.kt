package rak.pixellwp.cycling

import android.graphics.Bitmap
import rak.pixellwp.cycling.jsonModels.ImgJson

class Bitmap(img: ImgJson) {
    val width = img.width
    val height = img.height
    private val palette = Palette(img.getParsedColors(), img.cycles)
    private val pixels = img.pixels

    override fun toString(): String {
        return "image with wth dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    fun render() : Bitmap{
        //TODO - get the array of current pixels
        val colors: List<Int> = palette.getRawTransformedColors()
        val pixelColors: IntArray = intArrayOf(1)
        //TODO - what config to use?
        return Bitmap.createBitmap(pixelColors, width, height, null)
    }
}