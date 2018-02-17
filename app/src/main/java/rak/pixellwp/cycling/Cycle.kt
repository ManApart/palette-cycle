package rak.pixellwp.cycling

const val precision: Double = 100.0
const val precisionInt: Int = 100

data class Cycle(val rate: Int, private val reverse: Int, val low: Int, val high: Int) {
    private val cycleSpeed: Double = 280.0

    private val size = high - low + 1
    private val adjustedRate: Float = (rate / cycleSpeed).toFloat()

    private fun dFloatMod(a: Float, b: Int) : Double {
        return (Math.floor((a*precision)) % Math.floor((b*precision))) / precision
    }

    fun reverseColorsIfNecessary(colors: MutableList<Int>){
        if (reverse == 2){
            for (i in 0 until size/2){
                val lowValue = colors[low+i]
                val highValue = colors[high-i]
                colors[low+i] = highValue
                colors[high-i] = lowValue
            }
        }
    }

    fun getCycleAmount(timePassed: Int) : Double{
        var cycleAmount = 0.0
        if (reverse < 3){
            //standard cycle
            cycleAmount = dFloatMod(timePassed / (1000/adjustedRate), size)
        } else if (reverse == 3){
            //ping pong
            cycleAmount = dFloatMod(timePassed / (1000/adjustedRate), (size* 2))
            if (cycleAmount >= size){
                cycleAmount = size*2 - cycleAmount
            }
        } else if (reverse < 6){
            //sine wave
            cycleAmount = dFloatMod(timePassed / (1000/adjustedRate), size)
            cycleAmount = (Math.sin(cycleAmount * Math.PI * 2/size) + 1)
            if (reverse == 4){
                cycleAmount *= size/4
            } else if (reverse == 5){
                cycleAmount *= size/2
            }
        }

        return cycleAmount
    }
}