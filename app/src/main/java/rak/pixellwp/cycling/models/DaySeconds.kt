package rak.pixellwp.cycling.models

import android.widget.TimePicker

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
//        Log.d("dayseconds", "set time from $time to $adjustedTime")
        timeInMillis = adjustedTime
    }

    fun setTime(picker: TimePicker) {
        timeInMillis = getMilliFromSeconds(getSecondsFromHour(picker.currentHour) + getSecondsFromMinute(picker.currentMinute))
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

private fun getMilliFromSeconds(seconds: Int) : Long{
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
