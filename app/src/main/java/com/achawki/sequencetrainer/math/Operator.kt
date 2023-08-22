package com.achawki.sequencetrainer.math

import kotlin.math.min
import kotlin.random.Random

interface Operator<T> {
    fun apply(t: T): Int
}

enum class BinaryOperator : Operator<Pair<Int, Int>> {
    PLUS {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first + t.second
        }
    },
    MINUS {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first - t.second
        }
    },
    TIMES {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first * t.second
        }
    },
    REMAINDER {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first % t.second
        }
    }
}

enum class UnaryOperator : Operator<Int> {
    SQUARE {
        override fun apply(t: Int): Int {
            return t * t
        }
    },
    DIGIT_SUM {
        override fun apply(t: Int): Int {
            if (t == 0) return 0
            val sign = if (t < 0) -1 else 1
            var digitSum = 0
            var n = t * sign
            while (n > 0) {
                digitSum += n % 10
                n /= 10
            }
            return digitSum * sign
        }
    }
}

enum class ListOperator : Operator<List<Int>> {
    SUM {
        override fun apply(t: List<Int>): Int {
            return t.sum()
        }
    }
}

class WeightedOperator<T>(val operator: Operator<T>, val weight: Int)

fun getRandomOperatorsFromWeightedOperators(
    availableOperators: List<WeightedOperator<*>>,
    operatorsToSelect: Int
): List<Operator<*>> {
    val operators = mutableListOf<Operator<*>>()
    val overallWeight = availableOperators.sumOf { it.weight }
    val filteredOperators = availableOperators.filter { weightedOperator -> weightedOperator.weight > 0 }
    while (operators.size < min(operatorsToSelect, filteredOperators.size)) {
        var currentWeight = 0
        val targetWeight = Random.nextDouble() * overallWeight
        for (operator in filteredOperators) {
            currentWeight += operator.weight
            if (currentWeight >= targetWeight) {
                if (!operators.contains(operator.operator)) {
                    operators.add(operator.operator)
                }
                break

            }
        }
    }
    return operators
}
