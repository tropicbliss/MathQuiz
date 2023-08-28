package net.tropicbliss.mathquiz

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import net.tropicbliss.mathquiz.ui.ProgressScreen
import net.tropicbliss.mathquiz.ui.QuizScreen
import net.tropicbliss.mathquiz.ui.QuizViewModel
import net.tropicbliss.mathquiz.ui.StartScreen
import net.tropicbliss.mathquiz.ui.SummaryScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import net.tropicbliss.mathquiz.ui.AppViewModelProvider

enum class MathQuizScreen(@StringRes val title: Int) {
    Start(R.string.app_name),
    Quiz(R.string.quiz),
    Summary(R.string.summary_screen),
    Progress(R.string.progress_screen)
}

private data class NavigationItem(
    @StringRes val title: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector
)

private fun NavigationItem.toMathQuizScreen(): MathQuizScreen {
    return when (this.title) {
        R.string.progress_screen -> MathQuizScreen.Progress
        else -> MathQuizScreen.Start
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MathQuizAppBar(
    currentScreen: MathQuizScreen,
    navigateUp: () -> Unit,
    onClickNavigationItem: () -> Unit,
    onClickShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(title = {
        Text(stringResource(currentScreen.title))
    }, navigationIcon = {
        when (currentScreen) {
            MathQuizScreen.Start, MathQuizScreen.Progress -> {
                IconButton(onClick = onClickNavigationItem) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.menu)
                    )
                }
            }

            MathQuizScreen.Summary -> {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(R.string.home)
                    )
                }
            }

            else -> {}
        }
    }, actions = {
        if (currentScreen == MathQuizScreen.Summary) {
            IconButton(onClick = onClickShare) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
        }
    }, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MathQuizApp(
    navController: NavHostController = rememberNavController(),
    viewModel: QuizViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen =
        MathQuizScreen.valueOf(backStackEntry?.destination?.route ?: MathQuizScreen.Start.name)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(
        NavigationItem(
            title = R.string.home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ), NavigationItem(
            title = R.string.progress_screen,
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        )
    )
    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    ModalNavigationDrawer(drawerContent = {
        ModalDrawerSheet {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            items.forEachIndexed { index, item ->
                NavigationDrawerItem(label = {
                    Text(stringResource(item.title))
                }, selected = index == selectedItemIndex, onClick = {
                    selectedItemIndex = index
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(item.toMathQuizScreen().name)
                }, icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) {
                            item.selectedIcon
                        } else {
                            item.unselectedIcon
                        }, contentDescription = stringResource(item.title)
                    )
                }, modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
            }
        }
    }, drawerState = drawerState) {
        Scaffold(topBar = {
            MathQuizAppBar(currentScreen = currentScreen, onClickNavigationItem = {
                scope.launch {
                    drawerState.open()
                }
            }, onClickShare = {
            }, navigateUp = {
                navController.navigateUp()
            })
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MathQuizScreen.Start.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = MathQuizScreen.Start.name) {
                    StartScreen(onNavigate = {
                        viewModel.quizMode = it
                        navController.navigate(MathQuizScreen.Quiz.name)
                    })
                }
                composable(route = MathQuizScreen.Quiz.name) {
                    QuizScreen()
                }
                composable(route = "${MathQuizScreen.Summary.name}/{quizResults}") { backStackEntry ->
                    val jsonQuizResults = backStackEntry.arguments?.getString("quizResults")!!
                    BackHandler {
                        navController.popBackStack(MathQuizScreen.Start.name, inclusive = false)
                    }
                    SummaryScreen()
                }
                composable(route = MathQuizScreen.Progress.name) {
                    ProgressScreen()
                }
            }
        }
    }
}

private fun shareScore(context: Context, subject: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.share_score)
        )
    )
}