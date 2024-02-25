package com.example.chordprogressionmemo

import androidx.room.Entity
import androidx.room.PrimaryKey

// コードの種類をまとめたもの
enum class ChordQuality(val displayName: String) {
    MAJOR(""),
    MINOR("m"),
    MAJORSEVENTH("M7"),
    MINORSEVENTH("m7"),
    SEVENTH("7"),
    DIMINISH("dim");

    fun displayName(): String {
        return displayName
    }
}


@Entity
data class ChordInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chordId: Long = 0,
    val rootNote: String = "",
    val quality: String = "",
    val bassNote: String = "",
    val octave: Int = 0
)