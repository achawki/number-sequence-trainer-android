package com.achawki.sequencetrainer.converter

import androidx.room.TypeConverter
import com.achawki.sequencetrainer.common.SequenceStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    @TypeConverter
    fun toSequenceStatus(value: String) = enumValueOf<SequenceStatus>(value)

    @TypeConverter
    fun fromSequenceStatus(value: SequenceStatus) = value.name

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        if (value == null){
            return null
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        if (list == null){
            return null
        }
        val gson = Gson()
        return gson.toJson(list)
    }
}