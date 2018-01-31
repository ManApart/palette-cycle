package rak.pixellwp

import android.graphics.Bitmap
import android.graphics.Color
import junit.framework.Assert.assertEquals
import org.junit.Test

class BitmapTest {

    val pixels = intArrayOf(Color.BLUE, Color.GREEN, Color.RED,
            Color.WHITE, Color.MAGENTA, Color.YELLOW,
            Color.CYAN, Color.DKGRAY, Color.BLACK)

    @Test
    @Throws(Exception::class)
    fun helloWorld(){
        val spock = 1
        assertEquals(spock, 1)
    }

//    @Test
//    @Throws(Exception::class)
//    fun createBitmap(){
        //Can't test android.jar methods, lol
//        val bitMap = Bitmap.createBitmap(pixels, 3, 3, null)
//
//    }

}