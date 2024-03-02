package com.example.chordprogressionmemo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ChordInfoDao
{
    @Insert
    fun insert(chordInfo: ChordInfo)

    @Delete
    fun delete(chordInfo: ChordInfo)

    @Query(
        "SELECT * from ChordInfo ORDER BY orderIndex ASC"
    )
    fun getAllOrderedByIndex() : Flow<List<ChordInfo>>

    @Query(
        "SELECT orderIndex FROM ChordInfo ORDER BY orderIndex DESC LIMIT 1"
    )
    fun getLastOrderIndex() :Long?

    @Query(
        "UPDATE ChordInfo SET orderIndex = orderIndex - 1 WHERE orderIndex >= :startIndex"
    )
    fun updateOrderIndex(startIndex :Long)

    @Transaction
    fun insertLast(chordInfo: ChordInfo) {
        val lastOrderIndex = getLastOrderIndex() ?: -1
        val newChordInfo = chordInfo.copy(orderIndex = lastOrderIndex + 1)
        insert(newChordInfo)
    }

    @Transaction
    fun deleteWithOrderIndexUpdated(chordInfo: ChordInfo)
    {
        val orderIndex = chordInfo.orderIndex
        delete(chordInfo)
        updateOrderIndex(orderIndex + 1)
    }
}