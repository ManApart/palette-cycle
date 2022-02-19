package rak.pixellwp.cycling.timeline

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import rak.pixellwp.cycling.models.*

class DaySecondsTest {

    @Test
    fun getTimeStringTest(){
        val hour = 12
        val expectedString = "12:00"
        val seconds = getSecondsFromHour(hour)
        val actual = getTimeString(seconds)
        println("Seconds: $seconds, actual: $actual")
        assertEquals(expectedString, actual)
    }

    @Test
    fun getTimeStringTest2(){
        val hour = 15
        val expectedString = "15:00"
        val seconds = getSecondsFromHour(hour)
        val actual = getTimeString(seconds)
        println("Seconds: $seconds, actual: $actual")
        assertEquals(expectedString, actual)
    }

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

    @Test
    fun hoursFromSeconds(){
        assertEquals(24, getHourFromSeconds(86400))
        assertEquals(12, getHourFromSeconds(43200))
    }
}