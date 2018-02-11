package rak.pixellwp.cycling

class Palette(colors: List<Int>, val cycles: List<Cycle>) {
    private val baseColors = colors
    var colors = baseColors.toMutableList()

    fun cycle(timePassed: Int) {
        //it's important we copy the base values as each time we cycle it 'starts from 0'; it's not additive
        colors = baseColors.toMutableList()
        cycles
                .filter { it.rate != 0 }
                .forEach { cycle ->
                    cycle.reverseColorsIfNecessary(colors)
                    val amount = cycle.getCycleAmount(timePassed)
                    shiftColors(colors, cycle, amount)
                    cycle.reverseColorsIfNecessary(colors)
                }
    }

    fun shiftColors(colors: MutableList<Int>, cycle: Cycle, amount: Int) {
        for (i in 0 until amount) {
            val temp = colors[cycle.high]
            for (j in (cycle.high - 1) downTo cycle.low) {
                colors[j + 1] = colors[j]
            }
            colors[cycle.low] = temp
        }

    }


}