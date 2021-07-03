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

    @Query("select difficulty, ROUND(100.0 * (select count(*) from sequence se where se.status='SOLVED' and se.difficulty = s.difficulty)/count(*),2) AS successRate,count(*) as numberOfSequences,ROUND(CAST(sum(s.failed_attempts) AS float)/count(*),2) as failedAttemptsPerSequence from sequence s where s.status != 'ONGOING' group by s.difficulty")
    suspend fun getStatistics(): List<Statistic>
}