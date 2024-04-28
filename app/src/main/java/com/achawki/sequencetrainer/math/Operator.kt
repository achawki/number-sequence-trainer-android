package com.achawki.sequencetrainer.math

import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

interface Operator<T> {
    fun apply(t: T): Int
    fun print(t: T): String
}

enum class BinaryOperator : Operator<Pair<Int, Int>> {
    PLUS {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first + t.second
        }

        override fun print(t: Pair<Int, Int>): String {
            return "${t.first} + ${t.second}"
        }
    },
    MINUS {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first - t.second
        }
        override fun print(t: Pair<Int, Int>): String {
            return "${t.first} - ${t.second}"
        }
    },
    TIMES {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first * t.second
        }
            override fun print(t: Pair<Int, Int>): String {
                return "${t.first} * ${t.second}"
            }
    },
    REMAINDER {
        override fun apply(t: Pair<Int, Int>): Int {
            return t.first % t.second
        }
        override fun print(t: Pair<Int, Int>): String {
            return "${t.first} % ${t.second}"
        }
    }
}

enum class UnaryOperator : Operator<Int> {
    SQUARE {
        override fun apply(t: Int): Int {
            return t * t
        }
        override fun print(t: Int): String {
            return "${t}^2"
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
        override fun print(t: Int): String {
            val result = abs(t).toString().map { it.toString() }.joinToString(separator = " + ")
            return if (t < 0) {
                "-($result)"
            } else {
                result
            }
        }
    }
}

enum class ListOperator : Operator<List<Int>> {
    SUM {
        override fun apply(t: List<Int>): Int {
            return t.sum()
        }
        override fun print(t: List<Int>): String {
            return t.mapIndexed { i, number ->
                when {
                    i == 0 -> "$number"
                    number < 0 -> "- ${number * -1}"
                    else -> "+ $number"
                }
            }.joinToString(" ")
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
