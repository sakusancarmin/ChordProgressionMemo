package com.example.chordprogressionmemo

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao
import kotlinx.coroutines.launch

enum class ButtonMode {
    BACK, ADD
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordProgressionScreen(
    itemName: String = "",
    chordInfoDao: ChordInfoDao,
    onClick: (ButtonMode) -> Unit = {}
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel = ChordProgressionViewModel(application, chordInfoDao)
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            Text(text = itemName)
        }, navigationIcon = {
            IconButton(onClick = {
                onClick(ButtonMode.BACK)
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = ""
                )
            }
        })
    },

        floatingActionButton = {
            Row() {
                FloatingActionButton(onClick = {
                    onClick(ButtonMode.ADD)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "追加")
                }

                FloatingActionButton(onClick = {
                    scope.launch {
                        // コード進行の一番最後のコードまで連続再生する
                        // playNextChord()内で1コードずつの制御が実施されているため、
                        // ここのループの本体処理では何もしない。
                        while (viewModel.playNextChord()) {
                        }
                        viewModel.resetPlayback()
                    }
                }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "再生")
                }
            }
        }

    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            ChordProgressionList(viewModel)
        }
    }
}

@Composable
fun ChordProgressionList(viewModel: ChordProgressionViewModel) {
    val chordProgressionList = viewModel.chordListState.collectAsState().value

    LazyColumn(
        modifier = Modifier
            .padding(20.dp)
    ) {
        items(chordProgressionList) { chordInfo ->
            ChordProgressionItem(chordInfo)
            HorizontalDivider(thickness = Dp.Hairline)
        }
    }
}


@Composable
fun ChordProgressionItem(chordInfo: ChordInfo) {

    val textStyle = TextStyle(fontSize = 30.sp)

    CompositionLocalProvider(LocalTextStyle provides textStyle) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val showBassNote = (chordInfo.rootNote != chordInfo.bassNote)
            val bassNote = if (showBassNote) "on" + chordInfo.bassNote else ""
            val chordName = chordInfo.rootNote + chordInfo.quality + bassNote

            Text(modifier = Modifier.weight(1f), text = chordName)
            Text(modifier = Modifier.weight(1f), text = "音程" + chordInfo.octave.toString())

        }
    }
}