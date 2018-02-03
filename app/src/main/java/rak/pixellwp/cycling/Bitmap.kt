package rak.pixellwp.cycling

import android.graphics.Bitmap
import android.graphics.Color
import rak.pixellwp.cycling.jsonModels.ImgJson
import java.util.stream.Collectors

class Bitmap(img: ImgJson) {
    val width = img.width
    val height = img.height
    private val palette = Palette(img.getParsedColors(), img.cycles)
    private val pixels = img.pixels
    private val bitmap: Bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)

    override fun toString(): String {
        val color = palette.colors[pixels[0]]
        return "image with wth dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels. Sample color: r${Color.red(color)}, g${Color.green(color)}, b${Color.blue(color)}"
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
//
//    fun render() : Bitmap{
//        val pixelColors: IntArray = pixels.stream().map { p -> palette.colors[p] }.collect(Collectors.toList()).toIntArray()
//
////        val pixelColors = IntArray(width*height*4)
////        var j = 0
////        var i = 0
////
////        for (y in 0 until height){
////            for (x in 0 until width) {
////                val color = palette.colors[pixels[j]]
////                pixelColors[i] = Color.red(color)
////                pixelColors[i+1] = Color.green(color)
////                pixelColors[i+2] = Color.blue(color)
////                pixelColors[i+3] = 255
////
////                i += 4
////                j ++
////            }
////        }
//
//        return Bitmap.createBitmap(pixelColors, width, height, Bitmap.Config.ARGB_8888)
//    }
}