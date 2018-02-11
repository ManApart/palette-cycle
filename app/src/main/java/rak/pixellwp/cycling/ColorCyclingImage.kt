package rak.pixellwp.cycling

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import rak.pixellwp.cycling.jsonModels.ImgJson

class ColorCyclingImage(img: ImgJson) {
    val width = img.width
    val height = img.height
    private val palette = Palette(img.getParsedColors(), img.cycles)
    private val pixels = img.pixels.toList()
    private val optimizedPixels = optimizePixels(pixels)

    private val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    init {
        createBitmap()
    }

    override fun toString(): String {
        return "image with dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    fun advance(timePassed: Int){
        palette.cycle(timePassed)
        drawOptimizedImage()
    }

    fun getBitmap() : Bitmap{
        return bitmap
    }

    private fun optimizePixels(pixels: List<Int>) : List<Pixel> {
        val optPixels = mutableListOf<Pixel>()
        val optColors = BooleanArray(pixels.size, { _ -> false}).toMutableList()

        palette.cycles
                .filter { it.rate != 0 }
                .flatMap { it.low..it.high }
                .forEach { optColors[it] = true }

        var j = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                //If this pixel references an animated color
                if (optColors[pixels[j]]){
                    optPixels.add(Pixel(x.toFloat(), y.toFloat(), j))
                }
                j++
            }
        }
        return optPixels
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

    private fun drawOptimizedImage(){
        val canvas = Canvas(bitmap)
        val p = Paint()
        for (pixel in optimizedPixels){
            p.color = palette.colors[pixels[pixel.index]]
            canvas.drawPoint(pixel.x, pixel.y, p)
        }
    }

}