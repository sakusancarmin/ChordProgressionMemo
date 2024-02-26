package com.example.chordprogressionmemo.data


// 変化記号をまとめたもの
// valueには、BasicNoteNameに対する音の変化量を設定している(半音を1とする)
enum class AccidentalMark(val value: Int, val displayName: String)
{
    SHARP(1,"♯"),
    FLAT(-1,"♭"),
    NONE(0,"")
}

// 変化記号なしの音名をまとめたもの
// valueには、C = 0としたときの相対値を定義(半音を1とする)
enum class BasicNoteName(val value: Int, val displayName: String) {
    C(0, "C"),
    D(2, "D"),
    E(4, "E"),
    F(5, "F"),
    G(7, "G"),
    A(9, "A"),
    B(11, "B");
}

// 変化記号含む音名をまとめたもの
enum class NoteName(val noteName: BasicNoteName, val accidental: AccidentalMark = AccidentalMark.NONE)
{
    C(BasicNoteName.C),
    C_SHARP(BasicNoteName.C, AccidentalMark.SHARP),
    D_FLAT(BasicNoteName.D, AccidentalMark.FLAT),
    D(BasicNoteName.D),
    D_SHARP(BasicNoteName.D, AccidentalMark.SHARP),
    E_FLAT(BasicNoteName.E, AccidentalMark.FLAT),
    E(BasicNoteName.E),
    F(BasicNoteName.F),
    F_SHARP(BasicNoteName.F, AccidentalMark.SHARP),
    G_FLAT(BasicNoteName.G, AccidentalMark.FLAT),
    G(BasicNoteName.G),
    G_SHARP(BasicNoteName.G, AccidentalMark.SHARP),
    A_FLAT(BasicNoteName.A, AccidentalMark.FLAT),
    A(BasicNoteName.A),
    A_SHARP(BasicNoteName.A, AccidentalMark.SHARP),
    B_FLAT(BasicNoteName.B, AccidentalMark.FLAT),
    B(BasicNoteName.B);

    fun value() :Int
    {
        val value = noteName.value + accidental.value
        assert(value >= 0 && value < 12)
        return value
    }

    fun displayName() :String
    {
        return noteName.displayName + accidental.displayName
    }


    companion object {
        fun lookup(displayName: String): NoteName {
            return entries.find { it.displayName() == displayName } ?: throw IllegalArgumentException()
        }
    }
}
