package com.example.chordprogressionmemo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonMode {
    BACK, ADD
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordProgressionScreen(itemName: String = "", onClick: (ButtonMode) -> Unit = {}) {
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
            //ChordProgressionList()
        }
    }
}

@Composable
fun ChordProgressionList() {
    LazyColumn() {
        val chordProgressionList = listOf("")

        items(chordProgressionList) {
            ChordProgressionItem()
            HorizontalDivider(thickness = Dp.Hairline)
        }
    }
}


@Composable
fun ChordProgressionItem() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {}
}


