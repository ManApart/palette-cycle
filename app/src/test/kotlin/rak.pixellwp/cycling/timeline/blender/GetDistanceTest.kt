package rak.pixellwp.cycling.timeline.blender

import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import rak.pixellwp.cycling.models.getHourFromSeconds
import rak.pixellwp.cycling.models.getSecondsFromHour


@RunWith(Parameterized::class)
class GetDistanceTest(private val previous: Int, private val next: Int, private val expectedDistance: Int) {

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

    @Test
    fun getDistance() {
        val blender = createTimelineBlender()
        val distance = blender.getDist(getSecondsFromHour(previous), getSecondsFromHour(next))
        Assert.assertEquals(expectedDistance, getHourFromSeconds(distance))
    }
}