package com.achawki.sequencetrainer.common

enum class Difficulty(val label: Int) {
    EASY(1), MEDIUM(2), HARD(3);

    companion object {
        private val labelMapping = entries.associateBy(Difficulty::label)
        fun fromLabel(label: Int): Difficulty = labelMapping.getOrElse(label) { EASY }
    }
}