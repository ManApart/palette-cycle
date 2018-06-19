package rak.pixellwp.example

import junit.framework.Assert
import org.junit.Test

class CollectionsTest {
    @Test
    @Throws(Exception::class)
    fun helloWorld(){
        val spock = 1
        Assert.assertEquals(spock, 1)
    }

    @Test
    @Throws(Exception::class)
    fun first(){
        val strings = listOf("first", "second")
        val first = strings.first()
        Assert.assertEquals("first", first)
    }

    @Test
    @Throws(Exception::class)
    fun firstPredicate(){
        val strings = listOf("first", "second")
        val first = strings.first{it == "second"}
        Assert.assertEquals("second", first)
    }

    @Test(expected = NoSuchElementException::class)
    fun firstPredicateEmpty(){
        val strings = listOf<String>()
        val first = strings.first{it == "second"}
        System.err.print("Should not have made it this far")
    }


}