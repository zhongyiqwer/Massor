package com.example.massor

import org.junit.Test

import org.junit.Assert.*
import java.nio.ByteBuffer

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testByte2(){
        val CMD_Order : ByteArray = byteArrayOf(0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B)
        val CMD_Order1 : IntArray = intArrayOf(0x81,0x82,0x83,0x84,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B)
        val c:Byte=0x81.toByte()
        val d = 0b10000001
        println(0x81.compareTo(d))
        println(d.compareTo(c))

    }
}
