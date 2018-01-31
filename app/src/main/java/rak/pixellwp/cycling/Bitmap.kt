package rak.pixellwp.cycling

import android.graphics.Bitmap
import rak.pixellwp.cycling.jsonModels.ImgJson

class Bitmap(img: ImgJson) {
    val width = img.width
    val height = img.height
    val palette = Palette(img.parsedColors, img.cycles)
    val pixels = img.pixels

    override fun toString(): String {
        return "image with wth dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    fun render() : Bitmap{
        //TODO - get the array of current pixels
        val pixelColors: IntArray = intArrayOf(1)
        //TODO - what config to use?
        return Bitmap.createBitmap(pixelColors, width, height, null)
    }
}