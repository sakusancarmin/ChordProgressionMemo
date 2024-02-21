package com.example.chordprogressionmemo

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

// コードの情報を格納する
data class ChordInfo(
    val rootNote :NoteName,
    val quality :ChordQuality,
    val bassNote :NoteName,
    val octave :Int)
{
}