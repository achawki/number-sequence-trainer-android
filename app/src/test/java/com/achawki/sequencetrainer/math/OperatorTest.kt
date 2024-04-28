package com.achawki.sequencetrainer.math

import org.junit.Assert.assertEquals
import org.junit.Test

class OperatorTest {

    @Test
    fun apply_plus() {
        assertEquals(5, BinaryOperator.PLUS.apply(Pair(2, 3)))
    }

    @Test
    fun print_plus(){
        assertEquals("2 + 3", BinaryOperator.PLUS.print(Pair(2, 3)))
    }

    @Test
    fun apply_minus() {
        assertEquals(3, BinaryOperator.MINUS.apply(Pair(5, 2)))
    }

    @Test
    fun print_minus(){
        assertEquals("2 - 3", BinaryOperator.MINUS.print(Pair(2, 3)))
    }

    @Test
    fun apply_times() {
        assertEquals(10, BinaryOperator.TIMES.apply(Pair(5, 2)))
    }

    @Test
    fun print_times(){
        assertEquals("2 * 3", BinaryOperator.TIMES.print(Pair(2, 3)))
    }

    @Test
    fun apply_remainder() {
        assertEquals(1, BinaryOperator.REMAINDER.apply(Pair(5, 2)))
        assertEquals(0, BinaryOperator.REMAINDER.apply(Pair(4, 2)))
    }

    @Test
    fun print_remainder(){
        assertEquals("5 % 2", BinaryOperator.REMAINDER.print(Pair(5, 2)))
    }

    @Test
    fun apply_square() {
        assertEquals(25, UnaryOperator.SQUARE.apply(5))
    }

    @Test
    fun print_square(){
        assertEquals("2^2", UnaryOperator.SQUARE.print(2))
    }

    @Test
    fun apply_digitSum() {
        assertEquals(5, UnaryOperator.DIGIT_SUM.apply(5))
        assertEquals(0, UnaryOperator.DIGIT_SUM.apply(0))
        assertEquals(6, UnaryOperator.DIGIT_SUM.apply(15))
        assertEquals(11, UnaryOperator.DIGIT_SUM.apply(155))
        assertEquals(-11, UnaryOperator.DIGIT_SUM.apply(-155))
    }

    @Test
    fun print_digitSum(){
        assertEquals("5", UnaryOperator.DIGIT_SUM.print(5))
        assertEquals("1 + 5 + 5", UnaryOperator.DIGIT_SUM.print(155))
        assertEquals("-(1 + 5 + 5)", UnaryOperator.DIGIT_SUM.print(-155))
    }

    @Test
    fun apply_sum() {
        assertEquals(2, ListOperator.SUM.apply(listOf(2)))
        assertEquals(15, ListOperator.SUM.apply(listOf(2, 10, 3)))
        assertEquals(13, ListOperator.SUM.apply(listOf(-2, 10, 5)))
    }

    @Test
    fun print_sum(){
        assertEquals("-2 - 10 + 5", ListOperator.SUM.print(listOf(-2, -10, 5)))
        assertEquals("2 + 10 + 5", ListOperator.SUM.print(listOf(2, 10, 5)))
    }

    @Test
    fun getRandomOperatorsFromWeightedOperators() {
        val operators = getRandomOperatorsFromWeightedOperators(
            listOf(
                WeightedOperator(BinaryOperator.PLUS, 10),
                WeightedOperator(BinaryOperator.MINUS, 0),
                WeightedOperator(BinaryOperator.TIMES, 0)
            ), 1
        )
        assertEquals(1, operators.size)
        assertEquals(BinaryOperator.PLUS, operators[0])
    }

    @Test
    fun getRandomOperatorsFromWeightedOperators_onlyNonPositiveWeights() {
        val operators = getRandomOperatorsFromWeightedOperators(
            listOf(
                WeightedOperator(BinaryOperator.PLUS, -1),
                WeightedOperator(BinaryOperator.MINUS, 0),
                WeightedOperator(BinaryOperator.TIMES, 0)
            ), 1
        )
        assertEquals(0, operators.size)
    }

    @Test
    fun getRandomOperatorsFromWeightedOperators_lessPositiveOperatorsThanProvided() {
        val operators = getRandomOperatorsFromWeightedOperators(
            listOf(
                WeightedOperator(BinaryOperator.PLUS, 10),
                WeightedOperator(BinaryOperator.MINUS, 2),
                WeightedOperator(BinaryOperator.TIMES, 0)
            ), 4
        )
        assertEquals(2, operators.size)
    }
}