package rak.pixellwp.cycling

class Palette(val colors: List<Int>, val cycles: List<Cycle>) {
//    val baseColors = colors
//    val colors = baseColors.toMutableList()

    fun cycle(timePassed: Long) {
        cycles
                .filter { it.rate != 0 }
                .forEach { cycle ->
//                    cycle.reverseColorsIfNecessary(colors)
//                    shiftColors(cycle, cycle.getCycleAmount(timePassed))
//                    cycle.reverseColorsIfNecessary(colors)
                }
    }

//    private fun shiftColors(cycle: Cycle, amount: Int) {
//        for (i in 0..amount) {
//            val temp = colors[cycle.high]
//            for (j in cycle.high - 1..cycle.low) {
//                colors[j + 1] = colors[j]
//            }
//            colors[cycle.low] = temp
//        }
//
//    }


}