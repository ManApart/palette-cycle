package rak.pixellwp.cycling

import junit.framework.Assert
import org.junit.Test
import java.util.logging.Logger

class CycleTest {
    val Log = Logger.getLogger(CycleTest::class.java.name)

    @Test
    @Throws(Exception::class)
    fun helloWorld(){
        val spock = 1
        Assert.assertEquals(spock, 1)
    }

}