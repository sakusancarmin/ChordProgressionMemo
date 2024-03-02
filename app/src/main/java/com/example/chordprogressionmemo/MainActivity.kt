package com.example.chordprogressionmemo


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chordprogressionmemo.data.AppDatabase
import com.example.chordprogressionmemo.ui.theme.ChordProgressionMemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chordInfoDao = AppDatabase.getDatabase(this).chordInfoDao()
        val progInfoDao = AppDatabase.getDatabase(this).chordProgressionInfoDao()

        setContent {
            ChordProgressionMemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val TOP_SCREEN = "top_screen"
                    val CHORD_SCREEN = "chord_screen"
                    val INPUT_SCREEN = "input_screen"

                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = TOP_SCREEN) {
                        composable(route = TOP_SCREEN) {
                            TopScreen(progInfoDao) { progInfo ->
                                // ChordListのitemをクリックすると呼ばれる
                                navController.navigate("$CHORD_SCREEN/${progInfo.id}/${progInfo.name}")
                            }
                        }

                        composable(
                            route = "$CHORD_SCREEN/{id}/{name}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.LongType },
                                navArgument("name") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("id") ?: -1
                            val name = backStackEntry.arguments?.getString("name") ?: ""
                            assert(id >= 0)
                            assert(name != "")

                            ChordProgressionScreen(id, name, chordInfoDao) { progInfoId, mode ->
                                when (mode) {
                                    ButtonMode.BACK -> {
                                        navController.navigateUp()
                                    }

                                    ButtonMode.ADD -> {
                                        navController.navigate("$INPUT_SCREEN/${progInfoId}")
                                    }
                                }
                            }
                        }

                        composable(
                            route = "$INPUT_SCREEN/{progInfoId}",
                            arguments = listOf(
                                navArgument("progInfoId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val progInfoId = backStackEntry.arguments?.getLong("progInfoId") ?: -1
                            assert(progInfoId >= 0)

                            ChordInputScreen(progInfoId, chordInfoDao) {
                                // ボタン押すと呼ばれる
                                navController.navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }
}

