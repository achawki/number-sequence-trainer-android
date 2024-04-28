package com.achawki.sequencetrainer.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.achawki.sequencetrainer.converter.Converter

@Database(entities = [Sequence::class], version = 2, autoMigrations = [AutoMigration(from = 1, to = 2)])
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sequenceDao(): SequenceDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                createDatabase(context).also { instance = it }
            }
        }

        private fun createDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context, AppDatabase::class.java, "sequence-db"
            ).build()
        }
    }
}