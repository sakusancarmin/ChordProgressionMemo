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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

                    val TOP_SCREEN = "top_screen"
                    val CHORD_SCREEN = "chord_screen"

                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = TOP_SCREEN) {
                        composable(route = TOP_SCREEN) {
                            TopScreen() { name ->
                                navController.navigate("$CHORD_SCREEN/$name")
                            }
                        }

                        composable(
                            route = "$CHORD_SCREEN/{item}",
                            arguments = listOf(
                                navArgument("item") { type = NavType.StringType },
                            )
                        ) { backStackEntry ->
                            val item = backStackEntry.arguments?.getString("item") ?: ""
                            assert(item != "")

                            ChordProgressionScreen(item) {
                                navController.navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopScreen(onClick: (String) -> Unit = {}) {
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChordProgressionScreen(itemName: String = "", onClick: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = itemName)
                },
                navigationIcon = {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) {

    }
}
