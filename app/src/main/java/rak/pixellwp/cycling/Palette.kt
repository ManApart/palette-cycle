package rak.pixellwp.cycling

import android.util.Log
import java.util.logging.Logger

class Palette(colors: List<Int>, val cycles: List<Cycle>) {
    val baseColors = colors
    val colors = baseColors.toMutableList()
    val LOG = Logger.getLogger(Palette::class.java.name)

    fun cycle(timePassed: Long) {
        cycles
                .filter { it.rate != 0 }
                .forEach { cycle ->
                    cycle.reverseColorsIfNecessary(colors)
                    shiftColors(cycle, cycle.getCycleAmount(timePassed))
                    cycle.reverseColorsIfNecessary(colors)
                }
    }

    fun shiftColors(cycle: Cycle, amount: Int) {
        if (cycle.low == 32){
            Log.d("Palette", "Shifting cycle with ${cycle.low}-${cycle.high} by $amount")
            Log.d("Palette", "before: $colors")
        }
        for (i in 0 until amount) {
            val temp = colors[cycle.high]
            for (j in (cycle.high - 1) downTo cycle.low) {
                colors[j + 1] = colors[j]
            }
            colors[cycle.low] = temp
        }
        if (cycle.low == 32){
            Log.d("Palette", "after: $colors")
        }

    }


}