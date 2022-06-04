package rak.pixellwp.cycling.models

import android.graphics.Bitmap
import rak.pixellwp.cycling.jsonModels.ImageJson

class ColorCyclingImage(img: ImageJson) : PaletteImage {
    private val width = img.width
    private val height = img.height
    private var palette = Palette(colors = img.getParsedColors(), cycles = img.cycles)
    private val pixels = img.pixels.toList()
    private val rawPixels = IntArray(width*height)
    private var optimizedPixels = optimizePixels(pixels)
    private var drawUnOptimized = true
    private val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    override fun toString(): String {
        return "image with dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    override fun advance(timePassed: Int){
        palette.cycle(timePassed)
        draw()
    }

    override fun getBitmap() : Bitmap{
        bitmap.setPixels(rawPixels,0,width,0,0,width,height)
        return bitmap
    }

    override fun getImageWidth(): Int {
        return width
    }

    override fun getImageHeight(): Int {
        return height
    }

    fun setPalette(palette: Palette){
        this.palette = palette
        drawUnOptimized = true
    }

    fun getPalette(): Palette{
        return palette
    }

    private fun optimizePixels(pixels: List<Int>) : List<Triple<Int,Int,Int>> {
        val optPixels = mutableListOf<Triple<Int,Int,Int>>()
        val optColors = BooleanArray(pixels.size) { false }.toMutableList()

        palette.cycles
                .filter { it.rate != 0 }
                .flatMap { it.low..it.high }
                .forEach { optColors[it] = true }

        var j = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                //If this pixel references an animated color
                if (optColors[pixels[j]]){
                    optPixels.add(Triple(x, y, j))
                }
                j++
            }
        }
        return optPixels
    }

    private fun draw() {
        if (drawUnOptimized){
            drawImage()
            drawUnOptimized = false
        } else {
            drawOptimizedImage()
        }
    }

    private fun drawImage() {
        for (j in 0 until height*width){
            rawPixels[j] = palette.colors[pixels[j]]
        }
    }

    private fun drawOptimizedImage(){
        for ((x, y, j) in optimizedPixels){
            rawPixels[(y*width)+x]=palette.colors[pixels[j]]
        }
    }
}