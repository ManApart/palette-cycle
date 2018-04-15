package rak.pixellwp.cycling.timeline

import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import rak.pixellwp.cycling.models.getSecondsFromHour

@RunWith(Parameterized::class)
class TimelineNextTest(private val hours: List<Int>, private val currentHour: Int, private val expectedHour: Int) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(listOf(0, 24), 12, 24),
                    arrayOf(listOf(0, 12, 15, 24), 15, 24),
                    arrayOf(listOf(12, 15, 24), 24, 12),
                    arrayOf(listOf(12, 15, 24), 25, 12)
            )
        }
    }

    @Test
    fun getNextPalette() {
        val timeline = createTimeline(hours)
        val next = timeline.getNextPalette(getSecondsFromHour(currentHour))
        Assert.assertEquals(getSecondsFromHour(expectedHour), next.key)
    }
}