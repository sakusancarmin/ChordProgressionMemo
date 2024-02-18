package com.example.chordprogressionmemo

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopScreen(onClick: (String) -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("コード進行メモ")
                }
            )
        }
    ) {
        ChordList(it) { name ->
            onClick(name)
        }
    }
}

@Composable
fun ChordList(topBarPadding: PaddingValues, onClickItem: (String) -> Unit = {}) {
    // TODO: データベースからリストを生成する
    val testList = listOf("a", "b", "c")

    LazyColumn(
        modifier = Modifier.padding(topBarPadding)
    ) {
        items(testList) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClickItem(it) }
                    .padding(20.dp)
            ) {
                Text(
                    text = it,
                    fontSize = 30.sp
                )

                Text(
                    text = "hogehoge",
                    fontSize = 25.sp
                )
            }

            HorizontalDivider(thickness = Dp.Hairline)
        }
    }
}