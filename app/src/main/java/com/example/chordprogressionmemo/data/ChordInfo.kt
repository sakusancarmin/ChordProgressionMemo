package com.example.chordprogressionmemo.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// コードの種類をまとめたもの
// pitchListには、rootNoteを0としたときの相対的な音の高さが入る
enum class ChordQuality(val displayName: String, val pitchList: List<Int>) {
    MAJOR("", listOf(0, 4, 7)),
    MINOR("m", listOf(0, 3, 7)),
    MAJORSEVENTH("M7", listOf(0, 4, 11)),
    MINORSEVENTH("m7", listOf(0, 3, 10)),
    MINORMAJORSEVENTH("Mm7", listOf(0, 3, 11)),
    SEVENTH("7", listOf(0, 4, 10)),
    NINTH("9", listOf(0, 4, 10, 14)),
    FLATNINTH("-9", listOf(0, 4, 10, 13)),
    SUSFOURTH("sus4", listOf(0, 5, 7)),
    SUSFOURTHSEVENTH("7sus4", listOf(0, 5, 10)),
    DIMINISH("dim", listOf(0, 3, 6)),
    DIMINISHSEVENTH("dim7", listOf(0, 3, 6, 9)),
    HALFDIMINISH("m7-5", listOf(0, 3, 6, 10)),
    AUGMENT("aug", listOf(0, 4, 8));

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


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ChordProgressionInfo::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("chordProgressId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChordInfo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chordProgressId: Long = 0,
    val rootNote: String = "",
    val quality: String = "",
    val bassNote: String = "",
    val octave: Int = 0,
    val orderIndex: Long = 0
)