package rak.pixellwp.example

import junit.framework.Assert
import org.junit.Test
import rak.pixellwp.example.kotlin.DaySeconds

class SetTimeTest {

    @Test
    @Throws(Exception::class)
    fun shorterThanADay(){
        val time = DaySeconds()
        val millis = hoursToMilli(6)
        time.setTime(millis)
        Assert.assertEquals("Time should be properly set when less than 24 hours worth of milliseconds", millis, time.timeInMillis)
    }

    @Test
    @Throws(Exception::class)
    fun longerThanADay(){
        val time = DaySeconds()
        val millis = hoursToMilli(29)
        val convertedMillis = hoursToMilli(29-24)
        time.setTime(millis)
        Assert.assertEquals("Time should be properly set when greater than 24 hours worth of milliseconds", convertedMillis, time.timeInMillis)
    }

    private fun hoursToMilli(hours: Int) : Long{
        return (1000*60*60 * hours).toLong()
    }

}