package com.achawki.sequencetrainer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.achawki.sequencetrainer.common.SequenceStatus
import com.achawki.sequencetrainer.generator.GeneratorResult

@Entity(tableName = "sequence", indices = [Index(value = ["identifier"], unique = true)])
data class Sequence(
    val solution: Int,
    val question: String,
    val difficulty: Int,
    var status: SequenceStatus = SequenceStatus.ONGOING,
    @ColumnInfo(name = "failed_attempts") var failedAttempts: Int = 0,
    val identifier: String?,
    @ColumnInfo(name = "solution_paths") val solutionPaths: List<String>?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun formatSolutionPaths() : String {
        if (this.solutionPaths == null){
            return ""
        }
        val sequenceNumbers = this.question.split("\\s+".toRegex()).toMutableList()
        var result = "${sequenceNumbers[0]}\n"
        sequenceNumbers[sequenceNumbers.size - 1] = this.solution.toString()
        sequenceNumbers.drop(1).forEachIndexed { index, sequenceElement ->
            result += "${this.solutionPaths[index]} = $sequenceElement\n"
        }
        return result
    }
}

fun toSequence(generatorResult: GeneratorResult, difficulty: Int): Sequence {
    val sequenceList = generatorResult.sequence
    val question = sequenceList.joinToString(
        separator = "  ",
        limit = sequenceList.size - 1,
        truncated = "?"
    )
    return Sequence(
        solution = sequenceList.last(),
        question = question,
        difficulty = difficulty,
        SequenceStatus.ONGOING,
        0,
        generatorResult.retrieveIdentifier(),
        generatorResult.solutionPath
    )
}