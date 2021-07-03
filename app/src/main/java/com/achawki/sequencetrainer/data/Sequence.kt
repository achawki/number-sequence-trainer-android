package com.achawki.sequencetrainer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.achawki.sequencetrainer.common.SequenceStatus

@Entity(tableName = "sequence")
data class Sequence(
    val solution: Int,
    val question: String,
    val difficulty: Int,
    var status: SequenceStatus = SequenceStatus.ONGOING,
    @ColumnInfo(name = "failed_attempts") var failedAttempts: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

fun toSequence(sequenceList: List<Int>, difficulty: Int): Sequence {
    val question = sequenceList.joinToString(
        separator = "  ",
        limit = sequenceList.size - 1,
        truncated = "?"
    )
    return Sequence(solution = sequenceList.last(), question = question, difficulty = difficulty)
}