package com.achawki.sequencetrainer.generator

import com.achawki.sequencetrainer.common.Difficulty
import org.junit.Assert.*
import org.junit.Test

class LocalSequenceGeneratorTest {

    private val generator = LocalSequenceGenerator()

    @Test
    fun generateSequence() {
        val result = generator.generateSequence(Difficulty.EASY)
        assertTrue(result.isSuccess)
        result.onSuccess {
            assertEquals(GeneratorConstants.SEQUENCE_LENGTH, it.size)
        }
    }

    @Test
    fun generateSequenceConfig() {
        val result = SequenceConstraint.generateSequenceConfig(Difficulty.HARD)
        assertTrue(result.isSuccess)
    }

    @Test
    fun verifyForPlausibility() {
        assertTrue(SequenceConstraint.verifyForPlausibility(listOf(1, 2, 3, 4, 5)))
    }

    @Test
    fun verifyForPlausibility_tooLargeNumbers() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(1, 2, 3, 4, 501)))
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(1, 2, 3, 4, -501)))
    }

    @Test
    fun verifyForPlausibility_tooManyOccurrences() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(2, 2, 3, 2, 2)))
    }

    @Test
    fun verifyForPlausibility_tooManyZeros() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(2, 2, 0, 0, 0)))
    }

    @Test
    fun verifyForPlausibility_tooManyConsecutiveOccurrences() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(2, 2, 2, 3, 3)))
    }
}