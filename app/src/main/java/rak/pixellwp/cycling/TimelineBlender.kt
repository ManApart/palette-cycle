package rak.pixellwp.cycling

import android.util.Log
import rak.pixellwp.cycling.models.*

class TimelineBlender(defaultPalette: Map.Entry<Int, Palette>) {
    private val logTag = "Timeline Blender"
    private var oldTimePassedInSeconds = -1
    private var currentPalette = defaultPalette.value
    private var lastPercent = 0

    fun getCurrentPalette(current: Palette, currentTime: Int, previous: Map.Entry<Int, Palette>, next: Map.Entry<Int, Palette>): Palette {
        if(returnImmediatly(currentTime, previous, next)){
            return currentPalette
        }

        val totalDist = getDist(previous.key, next.key)
        if (totalDist == 0) {
            currentPalette = next.value
            return currentPalette
        }

        val percent: Int = getPercent(currentTime, previous.key, totalDist)
        currentPalette = getBlendedPalette(current, currentTime, percent, previous, next)
        return currentPalette
    }

    private fun returnImmediatly(currentTime: Int, previous: Map.Entry<Int, Palette>, next: Map.Entry<Int, Palette>) : Boolean {
        if (oldTimePassedInSeconds == currentTime) {
            return true
        }
        oldTimePassedInSeconds = currentTime

        if (currentTime == previous.key) {
            currentPalette = previous.value
            return true
        }
        if (currentTime == next.key) {
            currentPalette = next.value
            return true
        }
        return false
    }

    fun getDist(previous: Int, next: Int) : Int {
        val adjustedNext = if (next < previous) next + getSecondsFromHour(24) else next
        return (adjustedNext - previous)
    }

    fun getPercent(currentTime: Int, previous: Int, totalDist: Int): Int {
        val adjustedCurrent = if (currentTime < previous) currentTime + getSecondsFromHour(24) else currentTime
        val progress = adjustedCurrent - previous
        return ((progress / totalDist.toDouble()) * precisionInt).toInt()
    }

    private fun getBlendedPalette(current: Palette, currentTime: Int, percent: Int, previous: Map.Entry<Int, Palette>, next: Map.Entry<Int, Palette>) : Palette {
        if (percent == lastPercent) {
            return currentPalette
        }
        lastPercent = percent

        Log.d(logTag, "Blending palettes for ${previous.key} (${getTimeString(previous.key)}) and ${next.key} (${getTimeString(next.key)}) with current time $currentTime (${getTimeString(currentTime)}) and percent blend $percent")
        return current.blendPalette(previous.value, next.value, percent)
    }


}