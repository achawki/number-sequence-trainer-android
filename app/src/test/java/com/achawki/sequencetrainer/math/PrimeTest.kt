package com.achawki.sequencetrainer.math

import org.junit.Assert.assertEquals
import org.junit.Test

class PrimeTest {

    @Test
    fun nextPrime() {
        assertEquals(7, nextPrime(5))
        assertEquals(11, nextPrime(10))
        assertEquals(2, nextPrime(-5))
        assertEquals(2, nextPrime(0))
        assertEquals(101, nextPrime(100))
        assertEquals(211, nextPrime(200))
    }
}