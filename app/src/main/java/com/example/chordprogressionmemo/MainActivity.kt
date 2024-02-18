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
                                // ChordListのitemをクリックすると呼ばれる
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
                                // 戻るボタン押すと呼ばれる
                                navController.navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }
}

