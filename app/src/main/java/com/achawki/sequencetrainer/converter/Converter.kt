package com.achawki.sequencetrainer.converter

import androidx.room.TypeConverter
import com.achawki.sequencetrainer.common.SequenceStatus

class Converter {
    @TypeConverter
    fun toSequenceStatus(value: String) = enumValueOf<SequenceStatus>(value)

    @TypeConverter
    fun fromSequenceStatus(value: SequenceStatus) = value.name
}