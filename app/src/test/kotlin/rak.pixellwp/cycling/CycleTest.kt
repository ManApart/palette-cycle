package rak.pixellwp.cycling

import junit.framework.Assert
import org.junit.Test
import java.util.logging.Logger

class CycleTest {
    val Log = Logger.getLogger(CycleTest::class.java.name)

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

        val cycle = Cycle(4914,2, 192, 199)
        cycle.reverseColorsIfNecessary(colors)

        Assert.assertEquals(expectedColors, colors)
    }


    private fun getColors(): MutableList<Int> {
        return MutableList(256, { index -> index })
    }
}