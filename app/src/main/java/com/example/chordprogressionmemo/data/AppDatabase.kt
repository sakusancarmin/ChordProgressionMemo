package com.example.chordprogressionmemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [ChordProgressionInfo::class, ChordInfo::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chordProgressionInfoDao(): ChordProgressionInfoDao
    abstract fun chordInfoDao(): ChordInfoDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chord_database"
                ).build()
                this.instance = instance
                // return instance
                instance
            }
        }
    }
}