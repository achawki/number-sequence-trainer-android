package com.achawki.sequencetrainer.converter

import com.achawki.sequencetrainer.common.SequenceStatus
import org.junit.Assert
import org.junit.Test

class ConverterTest {

    private val converter = Converter()

    @Test
    fun toSequenceStatus() {
        Assert.assertEquals(SequenceStatus.ONGOING, converter.toSequenceStatus("ONGOING"))
    }

    @Test
    fun fromSequenceStatus() {
        Assert.assertEquals("ONGOING", converter.fromSequenceStatus(SequenceStatus.ONGOING))
    }

    @Test
    fun fromString() {
        Assert.assertEquals(listOf("element"), converter.fromString("[\"element\"]"))
        Assert.assertEquals(null, converter.fromString(null))
    }

    @Test
    fun fromString_nullAsInput() {
        Assert.assertEquals(null, converter.fromString(null))
    }

    @Test
    fun fromList() {
        Assert.assertEquals("[\"element\"]", converter.fromList(listOf("element")))
    }

    @Test
    fun fromList_nullAsInput() {
        Assert.assertEquals(null, converter.fromList(null))
    }
}