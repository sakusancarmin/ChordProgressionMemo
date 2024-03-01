package com.example.chordprogressionmemo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ChordInfoDao
{
    @Insert
    fun insert(chordInfo: ChordInfo)

    @Query(
        "SELECT * from ChordInfo ORDER BY orderIndex ASC"
    )
    fun getAllOrderedByIndex() :List<ChordInfo>

    @Query(
        "SELECT orderIndex FROM ChordInfo ORDER BY orderIndex DESC LIMIT 1"
    )
    fun getLastOrderIndex() :Long?


    @Transaction
    fun insertLast(chordInfo: ChordInfo) {
        var lastOrderIndex = getLastOrderIndex() ?: -1
        val newChordInfo = chordInfo.copy(orderIndex = lastOrderIndex + 1)
        insert(newChordInfo)
    }
}