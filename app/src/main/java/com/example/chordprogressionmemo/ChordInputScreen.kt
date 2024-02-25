package com.example.chordprogressionmemo

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Stable
class ChordInputState(
    chordInfo: ChordInfo
) {
    var chordInfo = chordInfo
        private set

    fun setRootNote(rootNote: String) {
        // デフォルトはバス音 = 根音とする。
        // 根音を変えた場合も、バス音は根音に追従して変更する（バス音をそのまま引き継ぐシーンはほぼないと想定）
        chordInfo = chordInfo.copy(rootNote = rootNote, bassNote = rootNote)
    }

    fun setQuality(quality: String) {
        chordInfo = chordInfo.copy(quality = quality)
    }

    fun setBassNote(bassNote: String) {
        chordInfo = chordInfo.copy(bassNote = bassNote)
    }

    fun setOctave(octave: Int) {
        chordInfo = chordInfo.copy(octave = octave)
    }

    fun isValidated(): Boolean {
        return (chordInfo.rootNote != "" && chordInfo.bassNote != "")
    }
}

@Composable
fun rememberChordInputState(
    chordInfo: ChordInfo
): ChordInputState {
    return remember(chordInfo) {
        ChordInputState(chordInfo)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordInputScreen(itemName: String = "", onClick: () -> Unit = {}) {
    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            Text(text = itemName)
        }, navigationIcon = {
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = ""
                )
            }
        })
    }) {
        Column(
            modifier = Modifier.padding(it),

            ) {
            InputForm()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputForm() {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // オクターブ音のみ初期値を持つ
        var chordState = rememberChordInputState(ChordInfo(octave = 4))

        Log.v("InputForm", "Recomposable!")

        // TODO: 現状、根音を入力すると、ベース音の初期表示が変わる処理が動かない
        // NOTE: (おそらく)監視対象であるchordStateの指す先が変わらないとrecomposeされない
        //       そのため、関数の引数でchordStateを渡してはいけない
        RootNoteDropdownMenuBox(chordState)
        ChordQualityDropdownMenuBox(chordState)
        BassNoteDropdownMenuBox(chordState)
        OctaveDropdownMenuBox(chordState)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNoteDropdownMenuBox(chordState: ChordInputState) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        val noteOptions = NoteName.entries

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = chordState.chordInfo.rootNote,
            onValueChange = {},
            label = { Text("根音") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            noteOptions.forEach {
                DropdownMenuItem(text = { Text(it.displayName()) }, onClick = {
                    chordState.setRootNote(it.displayName())
                    expanded = false
                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordQualityDropdownMenuBox(chordState: ChordInputState) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        val qualityOptions = ChordQuality.entries

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = chordState.chordInfo.quality,
            onValueChange = {},
            label = { Text("種類") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            qualityOptions.forEach {
                DropdownMenuItem(text = { Text(it.displayName()) }, onClick = {
                    chordState.setQuality(it.displayName())
                    expanded = false
                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BassNoteDropdownMenuBox(chordState: ChordInputState) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        val noteOptions = NoteName.entries

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = chordState.chordInfo.bassNote,
            onValueChange = {},
            label = { Text("ベース音") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            noteOptions.forEach { noteName ->
                val displayName = "on" + noteName.displayName()

                DropdownMenuItem(text = {
                    Text(displayName)
                }, onClick = {
                    chordState.setBassNote(noteName.displayName())
                    expanded = false
                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OctaveDropdownMenuBox(chordState: ChordInputState) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        var octaveOptions = (0..7).toList()

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = chordState.chordInfo.octave.toString(),
            onValueChange = {},
            label = { Text("オクターブ") },
            trailingIcon = { TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            octaveOptions.forEach { octave ->

                val displayName = octave.toString()
                DropdownMenuItem(text = {
                    Text(displayName)
                }, onClick = {
                    chordState.setOctave(octave)
                    expanded = false
                }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }

        }
    }
}