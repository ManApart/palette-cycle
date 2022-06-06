package rak.pixellwp.cycling.models

import kotlin.math.floor
import kotlin.math.sin

const val precision: Float = 100.0F
const val precisionInt: Int = 100
const val PI: Float = 3.141592653589793F

data class Cycle(val rate: Int, private val reverse: Int, val low: Int, val high: Int) {
    private val cycleSpeed: Float = 280.0F

    private val size = high - low + 1
    private val adjustedRate: Float = (rate / cycleSpeed).toFloat()

    private fun dFloatMod(a: Float, b: Int) : Float {
        return (floor((a* precision)) % floor((b* precision))) / precision
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

    fun getCycleAmount(timePassed: Int) : Float{
        var cycleAmount = 0.0F
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
            cycleAmount = (sin(cycleAmount * PI * 2/size) + 1)
            if (reverse == 4){
                cycleAmount *= size/4
            } else if (reverse == 5){
                cycleAmount *= size/2
            }
        }

        return cycleAmount
    }
}