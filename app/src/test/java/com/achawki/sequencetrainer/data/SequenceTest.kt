package com.achawki.sequencetrainer.data

import com.achawki.sequencetrainer.common.SequenceStatus
import com.achawki.sequencetrainer.generator.GeneratorResult
import org.junit.Assert.*
import org.junit.Test

class SequenceTest {

    @Test
    fun toSequence() {
        val generatorResult = GeneratorResult(listOf(1, 2, 3), listOf("1 + 1", "2 + 1"))
        val sequence = toSequence(generatorResult, 1)

        assertEquals("1  2  ?", sequence.question)
        assertEquals(3, sequence.solution)
    }

    @Test
    fun formatSolutionPaths() {
        val generatorResult = GeneratorResult(listOf(1, 2, 3), listOf("1 + 1", "2 + 1"))
        val sequence = toSequence(generatorResult, 1)

        assertEquals("1\n1 + 1 = 2\n2 + 1 = 3\n", sequence.formatSolutionPaths())
    }

    @Test
    fun formatSolutionPaths_sequencePreviousDataModel() {
        val sequence = Sequence(4, "1 2 ?", 1, SequenceStatus.ONGOING, 0, null, null)

        assertEquals("", sequence.formatSolutionPaths())
    }
}