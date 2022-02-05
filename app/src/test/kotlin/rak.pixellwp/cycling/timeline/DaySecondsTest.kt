package rak.pixellwp.cycling.timeline

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import rak.pixellwp.cycling.models.getSecondsFromHour
import rak.pixellwp.cycling.models.getTimeString

@RunWith(Parameterized::class)
class DaySecondsTest(private val hour: Int, private val expectedString: String) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(12, "12:0"),
                    arrayOf(15, "15:0")
            )
        }
    }

    @Test
    fun getTimeStringTest(){
        val seconds = getSecondsFromHour(hour).toLong()
        val actual = getTimeString(seconds)
        println("Seconds: $seconds, actual: $actual")
        assertEquals(expectedString, actual)
    }
}