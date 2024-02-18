package com.example.chordprogressionmemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chordprogressionmemo.ui.theme.ChordProgressionMemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChordProgressionMemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChordListView()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChordListView()
{
    var selectedText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
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
            // リストクリック時の処理
            selectedText = name
        }
    }

    if (selectedText == "") {
        return
    }

    // TODO: ボタンクリック時に別画面に遷移させる
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            TextButton(onClick = { selectedText = "" }) {
                Text("OK")
            }
        },
        text = { Text("$selectedText is clicked.") }
    )
}


@Composable
fun ChordList(topBarPadding: PaddingValues, onClickItem: (String)->Unit = {}) {
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
        }
    }
}

