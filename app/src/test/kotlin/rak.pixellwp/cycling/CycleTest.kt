package rak.pixellwp.cycling

import org.junit.Assert.assertEquals
import org.junit.Test
import rak.pixellwp.cycling.models.Cycle

class CycleTest {

    @Test
    @Throws(Exception::class)
    fun reverseColors(){
        val colors = getColors()
        val expectedColors = getColors()
        expectedColors[192] = 199
        expectedColors[193] = 198
        expectedColors[194] = 197
        expectedColors[195] = 196
        expectedColors[196] = 195
        expectedColors[197] = 194
        expectedColors[198] = 193
        expectedColors[199] = 192

        val cycle = Cycle(4914, 2, 192, 199)
        cycle.reverseColorsIfNecessary(colors)

        assertEquals(expectedColors.toList(), colors.toList())
    }


    private fun getColors(): IntArray {
        return IntArray(256) { i -> i }
    }
}