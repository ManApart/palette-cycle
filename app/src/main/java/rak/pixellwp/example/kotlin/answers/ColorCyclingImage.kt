package rak.pixellwp.example.kotlin.answers

import rak.pixellwp.cycling.models.Palette
import rak.pixellwp.cycling.models.Pixel
import rak.pixellwp.example.kotlin.ImageJson

class ColorCyclingImage(img: ImageJson) {
    private val width = img.width
    private val height = img.height
    private var palette = Palette(colors = listOf(), cycles = img.cycles)
    val pixels = img.pixels.toList()
    private var optimizedPixels = optimizePixels(pixels)


    fun optimizePixels(pixels: List<Int>): List<Pixel> {
        val paletteIndexes = getOptimizedPaletteIndexes(pixels)
        return createOptimizedPixels(paletteIndexes, pixels)
    }

    private fun getOptimizedPaletteIndexes(pixels: List<Int>): MutableList<Boolean> {
        val paletteIndexes = BooleanArray(pixels.size) { false }.toMutableList()

        palette.cycles.filter { it.rate != 0 }
                .flatMap { it.low..it.high }
                .forEach { paletteIndexes[it] = true }
        return paletteIndexes
    }

    private fun createOptimizedPixels(paletteIndexes: MutableList<Boolean>, pixels: List<Int>): MutableList<Pixel> {
        val optPixels = mutableListOf<Pixel>()
        var j = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (paletteIndexes[pixels[j]]) {
                    optPixels.add(Pixel(x.toFloat(), y.toFloat(), j))
                }
                j++
            }
        }
        return optPixels
    }

}