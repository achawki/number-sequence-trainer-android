package com.achawki.sequencetrainer.data

data class Statistic(
    val difficulty: Int,
    val successRate: Float,
    val numberOfSequences: Int,
    val failedAttemptsPerSequence: Float
)