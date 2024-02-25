package com.example.chordprogressionmemo

import androidx.room.Entity
import androidx.room.PrimaryKey

// コードの種類をまとめたもの
// pitchListには、rootNoteを0としたときの相対的な音の高さが入る
enum class ChordQuality(val displayName: String, val pitchList: List<Int>) {
    MAJOR("", listOf(0, 4, 7)),
    MINOR("m", listOf(0, 3, 7)),
    MAJORSEVENTH("M7", listOf(0, 4, 7, 11)),
    MINORSEVENTH("m7", listOf(0, 3, 7, 10)),
    SEVENTH("7", listOf(0, 4, 7, 10)),
    DIMINISH("dim", listOf(0, 3, 6));

    fun displayName(): String {
        return displayName
    }

    companion object {
        fun lookup(displayName: String): ChordQuality {
            return entries.find { it.displayName == displayName }
                ?: throw IllegalArgumentException()
        }
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