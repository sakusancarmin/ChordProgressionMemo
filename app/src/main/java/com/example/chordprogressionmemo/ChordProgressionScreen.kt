package com.example.chordprogressionmemo

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayDisabled
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chordprogressionmemo.data.ChordInfo
import com.example.chordprogressionmemo.data.ChordInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


enum class ButtonMode {
    BACK, ADD
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordProgressionScreen(
    progInfoId: Long,
    itemName: String = "",
    chordInfoDao: ChordInfoDao,
    onClick: (Long, ButtonMode) -> Unit = { _, _ -> }
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel = ChordProgressionViewModel(application, chordInfoDao, progInfoId)
    //val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            Text(text = itemName)
        }, navigationIcon = {
            IconButton(onClick = {
                onClick(progInfoId, ButtonMode.BACK)
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = ""
                )
            }
        })
    },

        floatingActionButton = {
            Row () {
                val playbackState by viewModel.playbackState.collectAsState()
                when (playbackState) {
                    PlaybackState.PLAYABLE -> {
                        FloatingActionButton(
                            onClick = {
                                viewModel.playChordProgression()
                            }
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "再生")
                        }
                    }
                    PlaybackState.PLAYING -> {
                        FloatingActionButton(
                            onClick = {
                                viewModel.stopChordProgression()
                            }
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = "一時停止")
                        }
                    }
                    else -> {
                        FloatingActionButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Default.PlayDisabled, contentDescription = "再生できません")
                        }
                    }
                }
                Spacer(Modifier.size(10.dp))
                FloatingActionButton(onClick = {
                    onClick(progInfoId, ButtonMode.ADD)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "追加")
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
    val chordProgressionList by viewModel.chordListState.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(15.dp),
    ) {
        // リストの変更があった場合、アイテムの再利用を防ぐためにkeyを指定する
        // 例えばリストの途中の要素をスワイプで削除した場合に、スワイプ済状態となっているアイテムを
        // 引き継がないようにする
        var index = 0
        items(chordProgressionList, key = { it.id }) { chordInfo ->
            ChordProgressionItem(chordInfo, index, viewModel)
            HorizontalDivider(thickness = Dp.Hairline)
            index++
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordProgressionItem(chordInfo: ChordInfo, index: Int, viewModel: ChordProgressionViewModel) {

    val textStyle = TextStyle(fontSize = 30.sp)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isDismissed by remember { mutableStateOf(false) }

    //var displayConfirmDialog by remember { mutableStateOf(false)
    CompositionLocalProvider(LocalTextStyle provides textStyle) {
        val dismissState = rememberSwipeToDismissBoxState(
            positionalThreshold = {
                // スワイプ完了とみなす閾値を、画面の横幅に対する割合により設定する
                val THRESHOLD_RATE = 0.4f
                val displayMetrics = context.resources.displayMetrics
                displayMetrics.widthPixels * THRESHOLD_RATE
            },
            confirmValueChange = {
                // TODO 削除キャンセル機能をつけたい
                /*
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    displayConfirmDialog = true
                    true
                } else {
                    false
                }
                */
                // positionalThresholdに基づきスワイプ完了と判断された場合に
                // コードの削除処理を実施する
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            viewModel.deleteChordInfo(chordInfo)
                            // データベースからの削除後、リスト再構成が必要であることを通知する
                            isDismissed = true
                        }
                    }
                }
                false
            }
        )

        val enableDelete = viewModel.enableDelete.collectAsState().value
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = enableDelete,
            backgroundContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // enableDisMissFromEndToStartがfalseの場合においても、少しスワイプできてしまう。
                    // backgroudContentを表示させたくないため、条件判定を入れた
                    if (enableDelete) {
                        Spacer(modifier = Modifier.weight(3f))
                        Box(
                            modifier = Modifier
                                .background(color = Color.Red)
                                .padding(8.dp)
                                .weight(2f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "削除",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        ) {
            val focusIndex = viewModel.currentPosition.collectAsState().value
            val color = if (index == focusIndex)
                MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.background
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .background(color = color)
                    .clickable {
                        val setIndex = when {
                            index != focusIndex -> index
                            else -> -1
                        }
                        viewModel.setPosition(setIndex)
                    }
            ) {
                val showBassNote = (chordInfo.rootNote != chordInfo.bassNote)
                val bassNote = if (showBassNote) "/" + chordInfo.bassNote else ""

                val annotatedChordName = buildAnnotatedString {
                    append(chordInfo.rootNote)

                    withStyle(style = SpanStyle(fontSize = textStyle.fontSize.div(1.5))) {
                        append(chordInfo.quality)
                    }

                    append(bassNote)
                }

                Text(modifier = Modifier.weight(1f), text = annotatedChordName)
                Text(
                    modifier = Modifier.weight(1f),
                    text = "音程" + chordInfo.octave.toString(),
                    fontSize = 20.sp
                    )
            }
        }

        // TODO 削除キャンセル機能をつけたい
        /*
        if (displayConfirmDialog) {
            AlertDialog(
                onDismissRequest = {
                    displayConfirmDialog = false
                },
                title = { Text("削除の確認") },
                text = { Text("この項目を削除してもよろしいですか？") },
                confirmButton = {
                    Button(onClick = {
                        displayConfirmDialog = false
                    }) {
                        Text("削除")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        displayConfirmDialog = false
                        scope.launch {
                            // スワイプ前の状態に戻す
                            dismissState.reset()
                            dismissState.dismiss(SwipeToDismissBoxValue.Settled)
                            Log.d("ChordProgression", "reset :" + dismissState.targetValue.toString())
                        }
                    }) {
                        Text("キャンセル")
                    }
                }
            )
        }
         */
    }
}