package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkArenaBg
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkArenaBg
                ) {
                    val navController = rememberNavController()
                    val gameViewModel: GameViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "menu"
                    ) {
                        composable("menu") {
                            MainMenuScreen(
                                viewModel = gameViewModel,
                                onPlayClick = { levelNum ->
                                    gameViewModel.loadLevel(levelNum)
                                    navController.navigate("gameplay/$levelNum")
                                },
                                onLevelSelectClick = {
                                    navController.navigate("level_select")
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        }

                        composable("level_select") {
                            LevelSelectScreen(
                                viewModel = gameViewModel,
                                onLevelSelected = { levelNum ->
                                    gameViewModel.loadLevel(levelNum)
                                    navController.navigate("gameplay/$levelNum") {
                                        popUpTo("menu")
                                    }
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(
                            route = "gameplay/{levelNumber}",
                            arguments = listOf(
                                navArgument("levelNumber") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val levelNumber = backStackEntry.arguments?.getInt("levelNumber") ?: 1
                            GameplayScreen(
                                viewModel = gameViewModel,
                                onBackToMenu = {
                                    navController.popBackStack("menu", inclusive = false)
                                },
                                onLevelSelect = {
                                    navController.navigate("level_select") {
                                        popUpTo("menu")
                                    }
                                }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                viewModel = gameViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
