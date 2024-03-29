package com.example.chordprogressionmemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao
import com.example.chordprogressionmemo.data.ChordQuality
import com.example.chordprogressionmemo.data.NoteName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class ChordInputState(
    initialChordInfo: ChordInfo
) {
    private var _chordInfo = mutableStateOf(initialChordInfo)
    var chordInfo: ChordInfo
        get() = _chordInfo.value
        private set(value) {
            _chordInfo.value = value
        }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordInputScreen(prodInfoId :Long, chordInfoDao: ChordInfoDao, itemName: String = "コード登録画面", onClick: () -> Unit = {}) {
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
            Spacer(modifier = Modifier.padding(30.dp))
            InputForm(prodInfoId, chordInfoDao) {
                onClick()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputForm(prodInfoId :Long, chordInfoDao :ChordInfoDao, onClick: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        // オクターブ音のみ初期値を持つ
        val chordState = remember { ChordInputState(ChordInfo(chordProgressId = prodInfoId, octave = 4)) }

        RootNoteDropdownMenuBox(chordState)
        ChordQualityDropdownMenuBox(chordState)
        BassNoteDropdownMenuBox(chordState)
        OctaveDropdownMenuBox(chordState)

        Row(
        ) {
            var isButtonEnabled by remember { mutableStateOf(false) }
            isButtonEnabled = chordState.isValidated()

            Button(
                onClick = {
                    isButtonEnabled = false
                    if (!chordState.isValidated()) {
                        isButtonEnabled = true
                        return@Button
                    }

                    val player = ChordPlayer(context, chordState.chordInfo)
                    scope.launch {
                        player.waitForReady()
                        player.play()
                        player.stop(2_000L)
                        player.release()
                        isButtonEnabled = true
                    }
                },
                enabled = isButtonEnabled
            ) {
                Text("プレビュー再生")
            }
            Spacer(Modifier.size(10.dp))
            Button(
                onClick = {
                    isButtonEnabled = false
                    scope.launch {
                        // 画面遷移は、バックグラウンドでデータベース登録後に
                        // メインスレッドで実行する必要がある
                        withContext(Dispatchers.IO) {
                            chordInfoDao.insertLast(chordState.chordInfo)
                        }
                        onClick()
                    }
                },
                enabled = isButtonEnabled
            ) {
                Text("登録")
            }
        }
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
                DropdownMenuItem(text = {
                    Text(noteName.displayName())
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
        val octaveOptions = (3..4).toList()

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