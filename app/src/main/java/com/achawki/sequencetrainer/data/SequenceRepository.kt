package com.achawki.sequencetrainer.data

import com.achawki.sequencetrainer.common.SequenceStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SequenceRepository @Inject constructor(
    private val sequenceDao: SequenceDao
) {
    suspend fun insertSequence(sequence: Sequence): Long = sequenceDao.insert(sequence)
    suspend fun updateSequence(updatedSequence: Sequence) = sequenceDao.update(updatedSequence)
    suspend fun getActiveSequence(difficulty: Int): Sequence? =
        sequenceDao.getActive(difficulty, SequenceStatus.ONGOING.name)
    suspend fun getStatistics(): List<Statistic> = sequenceDao.getStatistics()
    suspend fun getSequenceByIdentifier(identifier: String): Sequence? = sequenceDao.getSequenceByIdentifier(identifier)
}