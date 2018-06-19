package rak.pixellwp.example.kotlin

/*
    Class stores time for a single day as milliseconds
 */
class DaySeconds {
    private val maxMilliseconds = getMilliFromSeconds(getSecondsFromHour(24))
    var timeInMillis: Long = 0

    fun setTime(time: Long) {
        throw NotImplementedError()
    }
}

private fun getMilliFromSeconds(seconds: Int) : Long{
    return (seconds * 1000).toLong()
}

fun getSecondsFromHour(hour: Int): Int {
    return hour * 60 * 60
}
