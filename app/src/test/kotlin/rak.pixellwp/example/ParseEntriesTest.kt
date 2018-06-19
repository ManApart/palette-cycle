package rak.pixellwp.example

import org.junit.Assert
import org.junit.Test
import rak.pixellwp.cycling.models.Palette
import rak.pixellwp.example.kotlin.Timeline

class ParseEntriesTest {

    @Test
    @Throws(Exception::class)
    fun parseEntries() {
        val entries = HashMap<String, String>()
        val palettes = mutableListOf<Palette>()
        entries["1"] = "123"
        palettes.add(Palette("123", listOf(), listOf()))

        entries["2"] = "abc"
        palettes.add(Palette("abc", listOf(), listOf()))

        entries["3"] = "myPalette"
        palettes.add(Palette("myPalette", listOf(), listOf()))

        val timeline = Timeline(entries, palettes)

        Assert.assertEquals("Map entries and number of palettes should match", palettes.size, timeline.timeToPalette.keys.size)
        entries.entries.forEach { entry ->
            run {
                val key = Integer.parseInt(entry.key)
                Assert.assertTrue("Timeline should contain palette for entry $key", timeline.timeToPalette.containsKey(key))
                Assert.assertEquals("Timeline entry id should match entry value ${entry.value}", entry.value, timeline.timeToPalette[key]?.id)
            }
        }
    }

}