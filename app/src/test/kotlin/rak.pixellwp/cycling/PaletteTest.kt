package rak.pixellwp.cycling

import junit.framework.Assert
import org.junit.Test
import rak.pixellwp.cycling.models.Cycle
import rak.pixellwp.cycling.models.Palette
import java.util.logging.Logger

class PaletteTest {
    val Log = Logger.getLogger(PaletteTest::class.java.name)

    @Test
    @Throws(Exception::class)
    fun helloWorld(){
        val spock = 1
        Assert.assertEquals(spock, 1)
    }

//    @Test
//    @Throws(Exception::class)
//    fun shiftColorTest(){
//        val cycle = Cycle(0, 0, 1, 10)
//        val colors = listOf(0,1,2,3,4,5,6,7,8,9,10,11)
//        val shiftedColors = listOf(0,10,1,2,3,4,5,6,7,8,9,11)
//        val palette = Palette(colors, listOf(cycle))
//        val amount: Double = 1.0
//
//        palette.shiftColors(palette.colors, cycle, amount)
////        Log.info("palette.colors= ${palette.colors}")
//        Assert.assertTrue(palette.colors.toIntArray() contentEquals shiftedColors.toIntArray())
//    }
}