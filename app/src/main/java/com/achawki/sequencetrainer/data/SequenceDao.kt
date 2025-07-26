package com.achawki.sequencetrainer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SequenceDao {

    @Insert
    suspend fun insert(sequence: Sequence): Long

    @Update
    suspend fun update(sequence: Sequence)

    @Query("SELECT * from sequence WHERE status = :ongoingLabel AND difficulty=:difficulty LIMIT 1")
    suspend fun getActive(difficulty: Int, ongoingLabel: String): Sequence?

    @Query("""
        SELECT 
            difficulty,
            CASE 
                WHEN COUNT(*) = 0 THEN 0.0 
                ELSE ROUND(100.0 * COUNT(CASE WHEN status = :solvedStatus THEN 1 END) / COUNT(*), 2) 
            END AS successRate,
            COUNT(*) AS numberOfSequences,
            CASE 
                WHEN COUNT(*) = 0 THEN 0.0 
                ELSE ROUND(CAST(SUM(failed_attempts) AS FLOAT) / COUNT(*), 2) 
            END AS failedAttemptsPerSequence
        FROM sequence 
        WHERE status != :ongoingStatus 
        GROUP BY difficulty
        ORDER BY difficulty ASC
    """)
    suspend fun getStatistics(
        solvedStatus: String,
        ongoingStatus: String
    ): List<Statistic>

    @Query("SELECT * from sequence WHERE identifier = :identifier")
    suspend fun getSequenceByIdentifier(identifier: String): Sequence?

    @Query("DELETE FROM sequence")
    suspend fun deleteAllSequences()
}