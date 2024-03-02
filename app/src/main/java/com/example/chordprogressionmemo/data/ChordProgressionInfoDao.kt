package com.example.chordprogressionmemo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChordProgressionInfoDao {
    @Insert
    fun insert(chordProgressionInfo: ChordProgressionInfo)

    @Update
    fun update(chordProgressionInfo: ChordProgressionInfo)

    @Query(
        "SELECT * FROM ChordProgressionInfo ORDER BY Id DESC"
    )
    fun getAllInDescendingOrder() : Flow<List<ChordProgressionInfo>>

    @Delete
    fun delete(chordProgressionInfo: ChordProgressionInfo)

}