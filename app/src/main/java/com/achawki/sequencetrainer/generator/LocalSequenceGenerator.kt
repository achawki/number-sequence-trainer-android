package com.achawki.sequencetrainer.generator

import com.achawki.sequencetrainer.common.Difficulty
import com.achawki.sequencetrainer.math.*
import kotlin.random.Random

class LocalSequenceGenerator : SequenceGenerator {

    override fun generateSequence(difficulty: Difficulty): Result<List<Int>> {
        for (run in 1..10) {
            val sequenceNumbers = mutableListOf<Int>()
            SequenceConstraint.generateSequenceConfig(difficulty).onSuccess { config: SequenceConfig ->
                sequenceNumbers.add(config.startingNumber)
                for (i in 1 until GeneratorConstants.SEQUENCE_LENGTH) {
                    val operatorsOfIteration: List<Operator<*>> = if (config.numberOfOperators == 1) {
                        listOf(config.operators[0])
                    } else if (config.isAlternating) {
                        if (i % 2 != 0) listOf(config.operators[0]) else listOf(config.operators[1])
                    } else {
                        config.operators
                    }
                    sequenceNumbers.add(
                        applyOperatorIteration(
                            operatorsOfIteration,
                            sequenceNumbers[i - 1],
                            config.factor,
                            sequenceNumbers
                        )
                    )

                }
                if (SequenceConstraint.verifyForPlausibility(sequenceNumbers)) {
                    return Result.success(sequenceNumbers)
                }
            }
        }
        return Result.failure(GeneratorException(GeneratorConstants.FAILED_TO_GENERATE_SEQUENCE_ERROR))
    }

}

private fun applyOperatorIteration(
    operators: List<Operator<*>>,
    lastNumber: Int,
    factor: Int,
    sequence: List<Int>
): Int {
    var result = 0
    operators.forEachIndexed { i, operator ->
        val currentLastNumber: Int = if (i == 0) lastNumber else result
        when (operator) {
            is BinaryOperator -> {
                result = operator.apply(Pair(currentLastNumber, factor))
            }
            is UnaryOperator -> {
                result = operator.apply(currentLastNumber)
            }
            is ListOperator -> {
                result = operator.apply(sequence)
            }
        }
    }
    return result
}

internal object SequenceConstraint {
    fun generateSequenceConfig(difficulty: Difficulty): Result<SequenceConfig> {
        for (i in 1..20) {
            val startingNumber = Random.nextInt(1, getMaxStartingNumber(difficulty) + 1)
            val factor = Random.nextInt(2, getMaxSequenceFactor(difficulty) + 1)
            val numberOfOperators: Int =
                if (Random.nextDouble() <= getProbabilityOfTwoOperators(difficulty)) 2 else 1
            val isAlternating = Random.nextDouble() <= getProbabilityOfAlternatingOperators(difficulty)
            val operators =
                getRandomOperatorsFromWeightedOperators(getAvailableOperators(difficulty), numberOfOperators)
            val sequenceConfig = SequenceConfig(startingNumber, factor, numberOfOperators, isAlternating, operators)
            if (verifyAdditionalConstraints(sequenceConfig, difficulty)) return Result.success(sequenceConfig)
        }
        return Result.failure(GeneratorException(GeneratorConstants.FAILED_TO_GENERATE_SEQUENCE_CONFIG_ERROR))
    }

    fun verifyForPlausibility(sequence: List<Int>): Boolean {
        if (sequence.isEmpty()) return false
        // validate any since square might be > int max
        if (sequence.any { it > 500 || it < -500 }) return false
        // filter out too many occurrences of a single digit and too many 0
        if (sequence.groupBy { it }
                .filterValues { it.size >= GeneratorConstants.SEQUENCE_LENGTH - 1 || (it.size >= 3 && it[0] == 0) }
                .isNotEmpty()) return false

        // filter out too many consecutive occurrences
        val consecutiveCount = mutableMapOf<Int, Int>()
        var last: Int? = null
        sequence.forEach {
            if (last == it) {
                val count = consecutiveCount[it]!! + 1
                if (count == GeneratorConstants.SEQUENCE_LENGTH - 2) {
                    return false
                }
                consecutiveCount[it] = count
            } else {
                consecutiveCount[it] = 1
            }
            last = it
        }

        return true
    }

    private fun getMaxStartingNumber(difficulty: Difficulty): Int {
        return when (difficulty) {
            Difficulty.EASY -> 5
            Difficulty.MEDIUM -> 7
            Difficulty.HARD -> 15
        }
    }

    private fun getMaxSequenceFactor(difficulty: Difficulty): Int {
        return when (difficulty) {
            Difficulty.EASY -> 4
            Difficulty.MEDIUM -> 6
            Difficulty.HARD -> 7
        }
    }

    private fun getProbabilityOfTwoOperators(difficulty: Difficulty): Double {
        return when (difficulty) {
            Difficulty.EASY -> 0.0
            Difficulty.MEDIUM -> 0.20
            Difficulty.HARD -> 0.5
        }
    }

    private fun getProbabilityOfAlternatingOperators(difficulty: Difficulty): Double {
        return when (difficulty) {
            Difficulty.EASY -> 0.0
            Difficulty.MEDIUM -> 0.25
            Difficulty.HARD -> 0.4
        }
    }

    private fun getAvailableOperators(difficulty: Difficulty): List<WeightedOperator<*>> {
        val plusOperator = WeightedOperator(BinaryOperator.PLUS, 10)
        val minusOperator = WeightedOperator(BinaryOperator.MINUS, 10)
        val timesOperator = WeightedOperator(BinaryOperator.TIMES, 10)
        val squareOperator = WeightedOperator(UnaryOperator.SQUARE, 2)
        return when (difficulty) {
            Difficulty.EASY -> listOf<WeightedOperator<*>>(
                plusOperator,
                minusOperator,
                timesOperator,
                squareOperator
            )
            Difficulty.MEDIUM -> listOf<WeightedOperator<*>>(
                plusOperator,
                minusOperator,
                timesOperator,
                squareOperator,
                WeightedOperator(UnaryOperator.NEXT_PRIME, 2),
                WeightedOperator(ListOperator.SUM, 2)
            )
            Difficulty.HARD -> listOf<WeightedOperator<*>>(
                WeightedOperator(BinaryOperator.PLUS, 8),
                WeightedOperator(BinaryOperator.MINUS, 8),
                WeightedOperator(BinaryOperator.TIMES, 8),
                WeightedOperator(UnaryOperator.SQUARE, 3),
                WeightedOperator(UnaryOperator.NEXT_PRIME, 3),
                WeightedOperator(BinaryOperator.REMAINDER, 3),
                WeightedOperator(ListOperator.SUM, 3),
                WeightedOperator(UnaryOperator.DIGIT_SUM, 3)
            )
        }
    }

    private fun verifyAdditionalConstraints(sequenceConfig: SequenceConfig, difficulty: Difficulty): Boolean {
        if (difficulty == Difficulty.HARD && sequenceConfig.operators.size == 1 && sequenceConfig.operators.any { it == BinaryOperator.PLUS || it == BinaryOperator.MINUS || it == BinaryOperator.TIMES || it == UnaryOperator.SQUARE }) {
            return false
        }

        if (difficulty == Difficulty.MEDIUM && sequenceConfig.operators.size == 1 && sequenceConfig.operators.any { it == BinaryOperator.PLUS || it == BinaryOperator.MINUS }) {
            return false
        }

        if (sequenceConfig.numberOfOperators == 2 && sequenceConfig.operators.containsAll(
                listOf(
                    BinaryOperator.PLUS,
                    BinaryOperator.MINUS
                )
            )
        ) {
            return false
        }

        if (!sequenceConfig.isAlternating && sequenceConfig.operators.size == 2 && sequenceConfig.operators.any { it !is BinaryOperator }
        ) {
            return false
        }

        return true
    }
}

internal data class SequenceConfig(
    val startingNumber: Int,
    val factor: Int,
    val numberOfOperators: Int,
    val isAlternating: Boolean,
    val operators: List<Operator<*>>
)