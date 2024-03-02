package com.example.chordprogressionmemo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChordProgressionInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = ""
)
