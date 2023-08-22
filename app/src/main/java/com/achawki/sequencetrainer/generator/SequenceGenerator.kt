package com.achawki.sequencetrainer.generator

import com.achawki.sequencetrainer.common.Difficulty

interface SequenceGenerator {
    fun generateSequence(difficulty: Difficulty): Result<List<Int>>
}

class GeneratorException(message:String): Exception(message)

object GeneratorConstants{
    const val SEQUENCE_LENGTH = 6
    const val FAILED_TO_GENERATE_SEQUENCE_ERROR = "Couldn't generate sequence"
    const val FAILED_TO_GENERATE_SEQUENCE_CONFIG_ERROR = "Could not generate valid sequence config"
}
