package rak.pixellwp.cycling.models

import android.util.Log
import android.widget.TimePicker
import java.util.*

/*
    Class stores time for a single day as milliseconds
 */
class DaySeconds {
    private val maxMilliseconds = getMilliFromSeconds(getSecondsFromHour(24))
    private var timeInMillis: Long = 0

    override fun toString(): String {
        return get24HourFormattedString() + ", milli: $timeInMillis"
    }

    fun getHours(): Int {
        return getHourFromSeconds(getSecondsFromMilli(timeInMillis))
    }

    fun getMinutes(): Int {
        return getMinutesFromSeconds(getSecondsFromMilli(timeInMillis)) % 60
    }

    fun getSeconds(): Int {
        return getSecondsFromMilli(timeInMillis) % 60
    }

    fun getMilliseconds(): Long {
        return timeInMillis
    }

    fun getTotalSeconds(): Int {
        return getSecondsFromMilli(timeInMillis)
    }

    fun setTime(time: Long) {
        val adjustedTime = if (time > maxMilliseconds){
            time % maxMilliseconds
        } else {
            time
        }
        timeInMillis = adjustedTime
        Log.d("dayseconds", "set time from $time to ${get24HourFormattedString()}")
    }

    fun setTimeToNow(){
        val now = GregorianCalendar()
        setTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
    }


    fun setTime(picker: TimePicker) {
        setTime(picker.currentHour, picker.currentMinute)
    }

    fun setTime(hours: Int, minutes: Int){
        timeInMillis = getMilliFromSeconds(getSecondsFromHour(hours) + getSecondsFromMinute(minutes))
    }

    fun get24HourFormattedString() : String {
        return String.format("%02d:%02d", getHours(), getMinutes())
    }
    fun get12HourFormattedString() : String {
        val hours = getHours()
        val adjust = (hours > 12)
        val adjustedHours = if (adjust) hours-12 else hours
        val amPm = if (adjust) "pm" else "am"
        return String.format("%02d:%02d", adjustedHours, getMinutes()) + amPm
    }

}

fun getTimeString(time: Int): String {
    return getTimeString(getMilliFromSeconds(time))
}

fun getTimeString(time: Long): String {
    val cal = DaySeconds()
    cal.setTime(time)
    return cal.get24HourFormattedString()
}

private fun getSecondsFromMilli(milli: Long) : Int{
    return (milli / 1000).toInt()
}

fun getMilliFromSeconds(seconds: Int) : Long{
    return (seconds * 1000).toLong()
}

fun getSecondsFromMinute(minute: Int): Int {
    return minute * 60
}

fun getMinutesFromSeconds(seconds: Int): Int {
    return seconds / 60
}

fun getSecondsFromHour(hour: Int): Int {
    return hour * 60 * 60
}

fun getHourFromSeconds(seconds: Int): Int {
    return seconds / 60 / 60
}
