package rak.pixellwp.cycling.models

import java.util.*

/*
    Class stores time for a single day as milliseconds
 */
val maxMilliseconds = getMilliFromSeconds(getSecondsFromHour(24))
val maxMilliDouble = maxMilliseconds.toDouble()

class DaySeconds {
    private var timeInMillis: Long = 0

    override fun toString(): String {
        return get24HourFormattedString() + ", milli: $timeInMillis"
    }

    private fun getHours(): Int {
        return getHourFromSeconds(getSecondsFromMilli(timeInMillis))
    }

    private fun getMinutes(): Int {
        return getMinutesFromSeconds(getSecondsFromMilli(timeInMillis)) % 60
    }

    private fun getSeconds(): Int {
        return getSecondsFromMilli(timeInMillis) % 60
    }

    fun getMilliseconds(): Long {
        return timeInMillis
    }

    fun getTotalSeconds(): Int {
        return getSecondsFromMilli(timeInMillis)
    }

    fun setTime(time: Long) {
        timeInMillis = getTimeWithinDay(time)
    }

    fun setTimeToNow() {
        val now = GregorianCalendar()
        setTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
    }

    private fun setTime(hours: Int, minutes: Int) {
        timeInMillis = getMilliFromSeconds(getSecondsFromHour(hours) + getSecondsFromMinute(minutes))
    }

    fun get24HourFormattedString(): String {
        return String.format("%02d:%02d", getHours(), getMinutes())
    }

}

private fun getSecondsFromMilli(milli: Long): Int {
    return (milli / 1000).toInt()
}

fun getMilliFromSeconds(seconds: Int): Long {
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

fun getTimeWithinDay(time: Long): Long {
    return  when {
        time > maxMilliseconds  -> time % maxMilliseconds
        time < 0 -> maxMilliseconds - (time % maxMilliseconds)
        else -> time
    }
}

fun getDayPercent(millis: Long): Int {
    return (100 - (maxMilliseconds - millis) / maxMilliDouble * 100).toInt()
}