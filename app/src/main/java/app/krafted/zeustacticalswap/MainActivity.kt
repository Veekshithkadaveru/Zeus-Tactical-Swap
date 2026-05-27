package app.krafted.zeustacticalswap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.krafted.zeustacticalswap.data.db.AppDatabase
import app.krafted.zeustacticalswap.data.db.BossProgressRepository
import app.krafted.zeustacticalswap.game.BossId
import app.krafted.zeustacticalswap.ui.ArenaCompleteScreen
import app.krafted.zeustacticalswap.ui.BattleScreen
import app.krafted.zeustacticalswap.ui.DefeatScreen
import app.krafted.zeustacticalswap.ui.HomeScreen
import app.krafted.zeustacticalswap.ui.LeaderboardScreen
import app.krafted.zeustacticalswap.ui.PreBattleScreen
import app.krafted.zeustacticalswap.ui.SplashScreen
import app.krafted.zeustacticalswap.ui.VictoryScreen
import app.krafted.zeustacticalswap.ui.theme.ZeusTacticalSwapTheme
import app.krafted.zeustacticalswap.viewmodel.BattleViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = BossProgressRepository(database.bossDao())

        setContent {
            ZeusTacticalSwapTheme {
                val navController = rememberNavController()
                val viewModel: BattleViewModel by viewModels {
                    BattleViewModel.provideFactory(repository)
                }
                val state by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onEnter = { navController.navigate("home") }
                            )
                        }

                        composable("home") {
                            val nextBoss =
                                BossId.values().getOrNull(state.defeatedBosses.size) ?: BossId.HADES
                            val bestTime = state.bestClearTimes[nextBoss]

                            HomeScreen(
                                defeatedBosses = state.defeatedBosses,
                                currentHp = state.player.currentHp,
                                maxHp = state.player.maxHp,
                                bestClearTime = bestTime,
                                onSelectBoss = { index ->
                                    navController.navigate("pre_battle/$index")
                                },
                                onLeaderboardClick = {
                                    navController.navigate("leaderboard")
                                }
                            )
                        }

                        composable(
                            route = "pre_battle/{bossIndex}",
                            arguments = listOf(navArgument("bossIndex") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val bossIndex = backStackEntry.arguments?.getInt("bossIndex") ?: 0
                            val bossId = BossId.values()[bossIndex]
                            PreBattleScreen(
                                boss = bossId,
                                onBegin = {
                                    viewModel.loadBoss(bossIndex)
                                    navController.navigate("battle")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("battle") {
                            BattleScreen(
                                viewModel = viewModel,
                                onQuit = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = false
                                        }
                                    }
                                },
                                onVictory = { navController.navigate("victory") },
                                onDefeat = { navController.navigate("defeat") },
                                onArenaComplete = { navController.navigate("arena_complete") }
                            )
                        }

                        composable("victory") {
                            val currentBoss = BossId.values()[state.currentBossIndex]
                            val isFinalBoss = state.currentBossIndex == 2
                            VictoryScreen(
                                boss = currentBoss,
                                isFinalBoss = isFinalBoss,
                                onContinue = {
                                    if (isFinalBoss) {
                                        navController.navigate("arena_complete")
                                    } else {
                                        viewModel.advanceToNextBoss()
                                        navController.navigate("battle") {
                                            popUpTo("battle") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable("defeat") {
                            val currentBoss = BossId.values()[state.currentBossIndex]
                            DefeatScreen(
                                boss = currentBoss,
                                onRetry = {
                                    viewModel.restartCurrentBoss()
                                    navController.navigate("battle") {
                                        popUpTo("battle") { inclusive = true }
                                    }
                                },
                                onHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }

                        composable("arena_complete") {
                            ArenaCompleteScreen(
                                onHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            )
                        }

                        composable("leaderboard") {
                            LeaderboardScreen(
                                bestTimes = state.bestClearTimes,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}