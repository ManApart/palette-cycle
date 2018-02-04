package rak.pixellwp.cycling

data class Cycle(val rate: Int, val reverse: Int, val low: Int, val high: Int) {
    private val precision = 100
    private val cycleSpeed = 280

    private val size = high - low + 1
    private val adjustedRate: Float = rate / cycleSpeed.toFloat()

    private fun dFloatMod(a: Float, b: Float) : Double {
        return (Math.floor((a*precision).toDouble()) % Math.floor((b*precision).toDouble())) / precision
    }

    fun reverseColorsIfNecessary(colors: MutableList<Int>){
        if (reverse == 2){
            for (i in 0..size/2){
                val low = colors[low+i]
                val high = colors[high-i]
                colors[low+i] = high
                colors[high-i] = low
            }
        }
    }

    fun getCycleAmount(timePassed: Long) : Int{
        var cycleAmount = 0f
        if (reverse < 3){
            //standard cycle
            cycleAmount = timePassed / (1000/adjustedRate) % size
        } else if (reverse == 3){
            //ping pong
            cycleAmount = timePassed / (1000/adjustedRate) % (size* 2)
            if (cycleAmount >= size){
                cycleAmount = size*2 - cycleAmount
            }
        } else if (reverse < 6){
            //sine wave
            cycleAmount = timePassed / (1000/adjustedRate) % size
            cycleAmount = (Math.sin(cycleAmount * Math.PI * 2/size) + 1).toFloat()
            if (reverse == 4){
                cycleAmount *= size/4
            } else if (reverse == 5){
                cycleAmount *= size/2
            }
        }


        return cycleAmount.toInt()
    }
}