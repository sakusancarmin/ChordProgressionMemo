package com.example.chordprogressionmemo

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chordprogressionmemo.data.ChordProgressionInfo
import com.example.chordprogressionmemo.data.ChordProgressionInfoDao

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopScreen(progInfoDao: ChordProgressionInfoDao, onClick: (ChordProgressionInfo) -> Unit = {}) {
    val application = LocalContext.current.applicationContext as Application
    val progViewModel = ChordProgressionManagerViewModel(application, progInfoDao)


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
        },

        floatingActionButton = {
            Row() {
                FloatingActionButton(onClick = {
                    progViewModel.toggleInputDialog(true)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "追加")
                }
            }
        }
    ) {
        val progInfoList by progViewModel.progListState.collectAsState()
        ChordList(it, progInfoList,
            onClickItem = { progInfo ->
                onClick(progInfo)
            },
            onLongClickItem = { progInfo ->
                progViewModel.openDeleteDialog(progInfo)
            },
        )


        val showInputDialog by progViewModel.showInputDialog.collectAsState()
        if (showInputDialog) {
            val inputText by progViewModel.inputText.collectAsState()
            AlertDialog(
                onDismissRequest = { progViewModel.toggleInputDialog(false) },
                title = { Text("コード進行登録") },
                text = {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { progViewModel.updateInputText(it) },
                        label = { Text("コード進行の名前を入力") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            progViewModel.toggleInputDialog(false)
                            progViewModel.addItem()
                            progViewModel.updateInputText("")
                        }
                    ) {
                        Text("追加")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            progViewModel.toggleInputDialog(false)
                            progViewModel.updateInputText("")
                        }
                    ) {
                        Text("キャンセル")
                    }
                }
            )
        }


        val showDeleteDialog by progViewModel.showDeleteDialog.collectAsState()
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { progViewModel.closeDeleteDialog() },
                title = { Text("コード進行削除確認") },
                text = { Text("本当に削除しますか？")},
                confirmButton = {
                    Button(
                        onClick = {
                            progViewModel.deleteItemAndCloseDialog()
                        }
                    ) {
                        Text("削除")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            progViewModel.closeDeleteDialog()
                        }
                    ) {
                        Text("キャンセル")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChordList(
    topBarPadding: PaddingValues, progInfoList: List<ChordProgressionInfo>,
    onClickItem: (ChordProgressionInfo) -> Unit = {},
    onLongClickItem: (ChordProgressionInfo) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.padding(topBarPadding)
    ) {
        items(progInfoList) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        onClick = { onClickItem(it) },
                        onLongClick = { onLongClickItem(it) }
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = it.name,
                    fontSize = 30.sp
                )
            }

            HorizontalDivider(thickness = Dp.Hairline)
        }
    }
}