package rak.pixellwp.example.kotlin

import rak.pixellwp.cycling.models.Palette
import rak.pixellwp.cycling.models.Pixel

class ColorCyclingImage(img: ImageJson) {
    private val width = img.width
    private val height = img.height
    private var palette = Palette(colors = listOf(), cycles = img.cycles)
    val pixels = img.pixels.toList()
    private var optimizedPixels = optimizePixels(pixels)


    fun optimizePixels(pixels: List<Int>): List<Pixel> {
        throw NotImplementedError()
    }

}