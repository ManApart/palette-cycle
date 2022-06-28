package rak.pixellwp.cycling

import org.junit.Assert.assertTrue
import org.junit.Test
import rak.pixellwp.cycling.models.Cycle
import rak.pixellwp.cycling.models.Palette

class PaletteTest {

    @Test
    @Throws(Exception::class)
    fun shiftColorTest(){
        val cycle = Cycle(0, 0, 1, 10)
        val colors = listOf(0,1,2,3,4,5,6,7,8,9,10,11)
        val shiftedColors = listOf(0,10,1,2,3,4,5,6,7,8,9,11)
        val palette = Palette("", colors, listOf(cycle))
        val amount = 1f

        palette.shiftColors(palette.colors, cycle, amount)
//        Log.info("palette.colors= ${palette.colors}")
        assertTrue(palette.colors contentEquals shiftedColors.toIntArray())
    }
}