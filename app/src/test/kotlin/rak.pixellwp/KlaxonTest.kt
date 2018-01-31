package rak.pixellwp

import android.graphics.Color
import android.util.Log
import com.beust.klaxon.Klaxon
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Test
import rak.pixellwp.cycling.jsonModels.ColorJson
import rak.pixellwp.cycling.jsonModels.ColorJsonConverter

class KlaxonTest {

    val json = "[[1, 1, 1],[2, 2, 2]]"

    @Test
    @Throws(Exception::class)
    fun helloWorld(){
        val spock = 1
        assertEquals(spock, 1)
    }

    @Test
    @Throws(Exception::class)
    fun parseColorJsonList(){
        //TODO - mock out Color jdk methods
        val parsed: List<ColorJson>? = Klaxon().converter(ColorJsonConverter).parseArray(json)
        assertNotNull(parsed)
        if (parsed != null) {
            Log.d("RAK", "parsed: ${parsed[0]}")
            System.out.print("parsed: ${parsed[0]}")
            assertEquals(2, parsed.size)
            assertEquals(1, red(parsed[0].rgb))
            assertEquals(2, red(parsed[1].rgb))
        }
        val reSerialized = Klaxon().toJsonString(parsed!!)
        assertEquals(json, reSerialized)

    }

    fun red(color: Int): Int {
        return color shr 16 and 0xFF
    }


}