package rak.pixellwp.cycling.timeline

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import rak.pixellwp.cycling.models.*

class DaySecondsTest {

    @Test
    fun getTimeWithinDayLoops(){
        assertEquals(1000, getTimeWithinDay(1000))
        assertEquals(2000, getTimeWithinDay(maxMilliseconds + 2000))
    }

    @Test
    fun getDayPercentIsCorrect(){
        assertEquals(0, getDayPercent(0L))
        assertEquals(50, getDayPercent(maxMilliseconds/2))
        assertEquals(100, getDayPercent(maxMilliseconds))
    }
}