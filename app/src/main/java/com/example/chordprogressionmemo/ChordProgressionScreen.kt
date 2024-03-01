package com.example.chordprogressionmemo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao

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
            FloatingActionButton(onClick = {
                onClick(ButtonMode.ADD)
            }) {
                Icon(Icons.Default.Add, contentDescription = "追加")
            }
        }

    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            ChordProgressionList(chordInfoDao)
        }
    }
}

@Composable
fun ChordProgressionList(chordInfoDao: ChordInfoDao) {
    val chordProgressionList =
        chordInfoDao.getAllOrderedByIndex().collectAsState(initial = emptyList()).value

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


