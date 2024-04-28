package com.achawki.sequencetrainer.generator

import com.achawki.sequencetrainer.common.Difficulty

interface SequenceGenerator {
    fun generateSequence(difficulty: Difficulty): Result<GeneratorResult>
}

data class GeneratorResult(
    val sequence: List<Int>, val solutionPath: List<String>, val identifier: String? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeneratorResult

        return sequence == other.sequence
    }

    override fun hashCode(): Int {
        return sequence.hashCode()
    }

     fun retrieveIdentifier(): String {
         return identifier ?: hashCode().toString()
    }
}

class GeneratorException(message: String) : Exception(message)

object GeneratorConstants {
    const val SEQUENCE_LENGTH = 6
    const val FAILED_TO_GENERATE_SEQUENCE_ERROR = "Couldn't generate sequence"
    const val FAILED_TO_GENERATE_SEQUENCE_CONFIG_ERROR = "Could not generate valid sequence config"
    const val SOLUTION_PATH_DELIMITER = "=>"
}
