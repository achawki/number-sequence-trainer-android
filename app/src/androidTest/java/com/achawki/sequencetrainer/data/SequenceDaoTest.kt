package com.achawki.sequencetrainer.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.achawki.sequencetrainer.common.SequenceStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SequenceDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var sequenceDao: SequenceDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        sequenceDao = database.sequenceDao()

        runBlocking {
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 1, SequenceStatus.ONGOING, 0))
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 1, SequenceStatus.SOLVED, 0))
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 1, SequenceStatus.SOLVED, 0))
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 1, SequenceStatus.SOLVED, 5))
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 1, SequenceStatus.GIVEN_UP, 3))
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 3, SequenceStatus.SOLVED, 0))
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 3, SequenceStatus.GIVEN_UP, 0))
            sequenceDao.insert(Sequence(1, "1 2 3 4 ?", 3, SequenceStatus.SOLVED, 1))
        }
    }

    @Test
    fun testStatistics() = runBlocking {
        val statistics = sequenceDao.getStatistics()
        assertEquals(2, statistics.size)
        val easyStatistics = statistics[0]
        val hardStatistics = statistics[1]
        assertEquals(1, easyStatistics.difficulty)
        assertEquals(4, easyStatistics.numberOfSequences)
        assertEquals(2.0F, easyStatistics.failedAttemptsPerSequence)
        assertEquals(75.0F, easyStatistics.successRate)
        assertEquals(3, hardStatistics.difficulty)
        assertEquals(3, hardStatistics.numberOfSequences)
        assertEquals(0.33F, hardStatistics.failedAttemptsPerSequence)
        assertEquals(66.67F, hardStatistics.successRate)
    }

    @After
    fun tearDown() {
        database.close()
    }

}