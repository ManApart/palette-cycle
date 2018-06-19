package rak.pixellwp.example.kotlin.answers

/*
    Class stores time for a single day as milliseconds
 */
class DaySeconds {
    private val maxMilliseconds = getMilliFromSeconds(getSecondsFromHour(24))
    var timeInMillis: Long = 0

    fun setTime(time: Long) {
        val adjustedTime = if (time > maxMilliseconds){
            time % maxMilliseconds
        } else {
            time
        }
        this.timeInMillis = adjustedTime
    }
}

private fun getMilliFromSeconds(seconds: Int) : Long{
    return (seconds * 1000).toLong()
}

fun getSecondsFromHour(hour: Int): Int {
    return hour * 60 * 60
}
