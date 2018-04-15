package rak.pixellwp.cycling.models

import android.content.Context
import android.text.format.DateFormat
import android.widget.TimePicker
import java.util.*


class DaySeconds {
    private val calendar = Calendar.getInstance()

    override fun toString(): String {
        return getHourMinuteString() + ", milli: ${calendar.timeInMillis}"
    }

    fun getHour(): Int {
        return calendar[Calendar.HOUR_OF_DAY]
    }

    fun getMinute(): Int {
        return calendar[Calendar.MINUTE]
    }

    fun setTime(time: Long) {
        calendar.timeInMillis = time
        System.out.print("set: $time, ${calendar.timeInMillis}")
    }

    fun setTime(picker: TimePicker) {
        calendar.set(Calendar.HOUR_OF_DAY, picker.currentHour)
        calendar.set(Calendar.MINUTE, picker.currentMinute)
    }

    fun getMilliseconds(): Long {
        return calendar.timeInMillis
    }

    fun getSecondsInDay(): Int {
        return calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.HOUR) * 60 * 60
    }

    fun getFormattedTime(context: Context): String {
        return DateFormat.getTimeFormat(context).format(Date(calendar.timeInMillis))
    }

    fun getHourMinuteString() : String {
        return "${getHour()}:${getMinute()}"
    }

}

fun getTimeString(time: Int): String {
    return getTimeString(time.toLong())
}

fun getTimeString(time: Long): String {
    val cal = DaySeconds()
    cal.setTime(time)
    return cal.getHourMinuteString()
}

fun getSecondsFromHour(hour: Int): Int {
    return hour * 60 * 60
}

fun getHourFromSeconds(seconds: Int): Int {
    return seconds / 60 / 60
}