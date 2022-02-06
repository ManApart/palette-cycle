package rak.pixellwp.cycling.timeline

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import rak.pixellwp.cycling.models.getSecondsFromHour
import rak.pixellwp.cycling.models.getTimeString

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
}