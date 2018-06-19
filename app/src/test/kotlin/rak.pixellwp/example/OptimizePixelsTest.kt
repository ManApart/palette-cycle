package rak.pixellwp.example

import junit.framework.Assert
import org.junit.Test
import rak.pixellwp.cycling.models.Cycle
import rak.pixellwp.example.kotlin.ColorCyclingImage
import rak.pixellwp.example.kotlin.ImageJson

class OptimizePixelsTest {
    @Test
    @Throws(Exception::class)
    fun optPixels() {
        val pixels = List(100) { it }
        val cycles = mutableListOf(Cycle(0, 0, 1, 10))
        cycles.add(Cycle(1, 0, 20, 30))

        val imgJson = ImageJson(10, 10, cycles, pixels)
        val image = ColorCyclingImage(imgJson)
        val results = image.optimizePixels(image.pixels)

        Assert.assertEquals("The active cycle has 11 palette indexes", 11, results.size)
        results.forEach{result ->
            run {
                Assert.assertTrue("The pixel should be greater than or equal to the active cycle's low value", result.index >= 20)
                Assert.assertTrue("The pixel should be less than or equal to the active cycle's high value", result.index <= 30)
            }
        }
    }

}