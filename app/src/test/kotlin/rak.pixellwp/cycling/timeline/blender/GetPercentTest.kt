package rak.pixellwp.cycling.timeline.blender

import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import rak.pixellwp.cycling.models.getHourFromSeconds

@RunWith(Parameterized::class)
class GetPercentTest(private val currentTime: Int, private val previous: Int, private val expectedPercent: Int) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Int>> {
            return listOf(
                    arrayOf(1, 24, 23),
                    arrayOf(1, 12, 11),
                    arrayOf(24, 1, 1),
                    arrayOf(24, 12, 12)
            )
        }
    }

//    @Test
//    fun getPercent() {
//        val blender = createTimelineBlender()
//        val percent = blender.getPercent(currentTime, previous, next)
//        Assert.assertEquals(expectedPercent, getHourFromSeconds(percent))
//    }
}