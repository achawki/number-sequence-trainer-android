package com.achawki.sequencetrainer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achawki.sequencetrainer.data.Sequence
import com.achawki.sequencetrainer.data.SequenceRepository
import com.achawki.sequencetrainer.data.Statistic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SequenceTrainerViewModel @Inject constructor(private val sequenceRepository: SequenceRepository) :
    ViewModel() {

    fun insertSequence(sequence: Sequence) {
        viewModelScope.launch {
            sequence.id = sequenceRepository.insertSequence(sequence)
        }
    }

    fun updateSequence(updatedSequence: Sequence) {
        viewModelScope.launch {
            sequenceRepository.updateSequence(updatedSequence)
        }
    }

    suspend fun getActiveSequence(difficulty: Int): Sequence? {
        return sequenceRepository.getActiveSequence(difficulty)
    }

    suspend fun getStatistics(): List<Statistic> {
        return sequenceRepository.getStatistics()
    }
}