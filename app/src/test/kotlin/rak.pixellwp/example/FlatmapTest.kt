package rak.pixellwp.example

import junit.framework.Assert
import org.junit.Test
import rak.pixellwp.cycling.models.Cycle
import rak.pixellwp.cycling.models.Pixel

class FlatmapTest {


    @Test
    @Throws(Exception::class)
    fun flatMapBehavior(){
        val cycles = listOf(
                Cycle(1,0,1,10),
                Cycle(1,0,50,55)
        )

        val result = cycles.flatMap { it.low..it.high }.toMutableList()

        val expected = mutableListOf<Int>()
        for (i in 1..10){
            expected.add(i)
        }
        for (i in 50..55){
            expected.add(i)
        }

        Assert.assertEquals(expected, result)
    }
}