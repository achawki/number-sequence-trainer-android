package com.achawki.sequencetrainer.generator

import com.achawki.sequencetrainer.common.Difficulty
import com.achawki.sequencetrainer.math.BinaryOperator
import com.achawki.sequencetrainer.math.ListOperator
import com.achawki.sequencetrainer.math.Operator
import com.achawki.sequencetrainer.math.UnaryOperator
import com.achawki.sequencetrainer.math.WeightedOperator
import com.achawki.sequencetrainer.math.getRandomOperatorsFromWeightedOperators
import kotlin.random.Random

class LocalSequenceGenerator : SequenceGenerator {

    override fun generateSequence(difficulty: Difficulty): Result<GeneratorResult> {
        for (run in 1..45) {
            val sequenceNumbers = mutableListOf<Int>()
            val solutionPaths = mutableListOf<String>()
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
                    val (result, solutionPath) = applyOperatorIteration(
                        operatorsOfIteration,
                        sequenceNumbers[i - 1],
                        config.factor,
                        sequenceNumbers
                    )
                    sequenceNumbers.add(result)
                    solutionPaths.add(solutionPath)

                }
                if (SequenceConstraint.verifyForPlausibility(sequenceNumbers)) {
                    return Result.success(GeneratorResult(sequenceNumbers, solutionPaths))
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
): Pair<Int, String> {
    var result = 0
    var solutionPath = ""
    operators.forEachIndexed { i, operator ->
        val currentLastNumber: Int = if (i == 0) lastNumber else result
        if (i > 0) solutionPath = "$solutionPath ${GeneratorConstants.SOLUTION_PATH_DELIMITER} "
        when (operator) {
            is BinaryOperator -> {
                result = operator.apply(Pair(currentLastNumber, factor))
                solutionPath += operator.print(Pair(currentLastNumber, factor))
            }

            is UnaryOperator -> {
                result = operator.apply(currentLastNumber)
                solutionPath += operator.print(currentLastNumber)
            }

            is ListOperator -> {
                result = operator.apply(sequence)
                solutionPath += operator.print(sequence)
            }
        }
    }

    return Pair(result, solutionPath)
}

internal object SequenceConstraint {
    fun generateSequenceConfig(difficulty: Difficulty): Result<SequenceConfig> {
        for (i in 1..50) {
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
        if (sequence.any { it > 950 || it < -950 }) return false
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
                if (count == 3) {
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
            Difficulty.EASY -> 20
            Difficulty.MEDIUM -> 35
            Difficulty.HARD -> 50
        }
    }

    private fun getMaxSequenceFactor(difficulty: Difficulty): Int {
        return when (difficulty) {
            Difficulty.EASY -> 7
            Difficulty.MEDIUM -> 9
            Difficulty.HARD -> 15
        }
    }

    private fun getProbabilityOfTwoOperators(difficulty: Difficulty): Double {
        return when (difficulty) {
            Difficulty.EASY -> 0.0
            Difficulty.MEDIUM -> 0.15
            Difficulty.HARD -> 0.3
        }
    }

    private fun getProbabilityOfAlternatingOperators(difficulty: Difficulty): Double {
        return when (difficulty) {
            Difficulty.EASY -> 0.0
            Difficulty.MEDIUM -> 0.15
            Difficulty.HARD -> 0.3
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
                WeightedOperator(ListOperator.SUM, 2)
            )

            Difficulty.HARD -> listOf<WeightedOperator<*>>(
                WeightedOperator(BinaryOperator.PLUS, 8),
                WeightedOperator(BinaryOperator.MINUS, 8),
                WeightedOperator(BinaryOperator.TIMES, 8),
                WeightedOperator(UnaryOperator.SQUARE, 4),
                WeightedOperator(BinaryOperator.REMAINDER, 4),
                WeightedOperator(ListOperator.SUM, 4),
                WeightedOperator(UnaryOperator.DIGIT_SUM, 4)
            )
        }
    }

    private fun verifyAdditionalConstraints(sequenceConfig: SequenceConfig, difficulty: Difficulty): Boolean {
        if (difficulty == Difficulty.HARD && sequenceConfig.operators.size == 1 && sequenceConfig.operators.any { it == BinaryOperator.PLUS || it == BinaryOperator.MINUS || it == UnaryOperator.SQUARE }) {
            return false
        }

        // prevent too often occurrences of single sum or times operator (>40%)
        if ((difficulty == Difficulty.HARD || difficulty == Difficulty.MEDIUM) && sequenceConfig.operators.size == 1 && sequenceConfig.operators.any { it == ListOperator.SUM || it == BinaryOperator.TIMES }) {
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