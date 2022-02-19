package rak.pixellwp.cycling.models

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import rak.pixellwp.cycling.jsonModels.ImageJson

class ColorCyclingImage(img: ImageJson) : PaletteImage {
    private val width = img.width
    private val height = img.height
    private var palette = Palette(colors = img.getParsedColors(), cycles = img.cycles)
    private val pixels = img.pixels.toList()
    private var optimizedPixels = optimizePixels(pixels)
    private var drawUnOptimized = true

    private val bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private val canvas = Canvas(bitmap)

    init {
        createBitmap()
    }

    override fun toString(): String {
        return "image with dimensions $width x $height = ${width*height}, ${palette.colors.size} colors, ${palette.cycles.size} cycles and ${pixels.size} pixels"
    }

    override fun advance(timePassed: Int){
        palette.cycle(timePassed)
        draw()
    }

    override fun getBitmap() : Bitmap{
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

    private fun optimizePixels(pixels: List<Int>) : List<Pixel> {
        val optPixels = mutableListOf<Pixel>()
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

    private fun draw() {
        if (drawUnOptimized){
            drawImage()
            drawUnOptimized = false
        } else {
            drawOptimizedImage()
        }
    }

    private fun drawImage() {
        val p = Paint()
        var j = 0
        for (y in 0 until height){
            for (x in 0 until width) {
                p.color = palette.colors[pixels[j]]
                canvas.drawPoint(x.toFloat(), y.toFloat(), p)
                j++
            }
        }
    }

    private fun drawOptimizedImage(){
        val p = Paint()
        for (pixel in optimizedPixels){
            p.color = palette.colors[pixels[pixel.index]]
            canvas.drawPoint(pixel.x, pixel.y, p)
        }
    }

}