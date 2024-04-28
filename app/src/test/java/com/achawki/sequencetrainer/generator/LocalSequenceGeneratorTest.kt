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
        result.onSuccess { generatorResult ->
            assertEquals(GeneratorConstants.SEQUENCE_LENGTH, generatorResult.sequence.size)
            assertEquals(GeneratorConstants.SEQUENCE_LENGTH - 1, generatorResult.solutionPath.size)
        }
    }

    @Test
    fun generateSequenceConfig() {
        val result = SequenceConstraint.generateSequenceConfig(Difficulty.HARD)
        assertTrue(result.isSuccess)
    }

    @Test
    fun verifyForPlausibility() {
        assertTrue(SequenceConstraint.verifyForPlausibility(listOf(1, 2, 3, 4, 5, 6)))
    }

    @Test
    fun verifyForPlausibility_tooLargeNumbers() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(1, 2, 3, 4, 5, 951)))
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(1, 2, 3, 4, 5, -951)))
    }

    @Test
    fun verifyForPlausibility_tooManyOccurrences() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(2, 2, 3, 2, 2, 2)))
    }

    @Test
    fun verifyForPlausibility_tooManyZeros() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(2, 2, 0, 0, 0, 0)))
    }

    @Test
    fun verifyForPlausibility_tooManyConsecutiveOccurrences() {
        assertFalse(SequenceConstraint.verifyForPlausibility(listOf(2, 2, 2, 3, 3, 3)))
    }
}