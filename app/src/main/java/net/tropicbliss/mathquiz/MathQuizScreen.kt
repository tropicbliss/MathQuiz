package net.tropicbliss.mathquiz

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.tropicbliss.mathquiz.data.QuizMode
import net.tropicbliss.mathquiz.ui.AppViewModelProvider
import net.tropicbliss.mathquiz.ui.ProgressScreen
import net.tropicbliss.mathquiz.ui.QuizScreen
import net.tropicbliss.mathquiz.ui.QuizViewModel
import net.tropicbliss.mathquiz.ui.Results
import net.tropicbliss.mathquiz.ui.StartScreen
import net.tropicbliss.mathquiz.ui.SummaryScreen

enum class MathQuizScreen(@StringRes val title: Int) {
    Start(R.string.app_name), Quiz(R.string.app_name), Summary(R.string.summary_screen), Progress(R.string.progress_screen)
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
    quizMode: QuizMode,
    onTimerComplete: () -> Unit,
    onInfoOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (currentScreen) {
        MathQuizScreen.Quiz -> stringResource(quizMode.getStringResource())
        else -> stringResource(currentScreen.title)
    }

    TopAppBar(title = {
        Text(title)
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

            MathQuizScreen.Quiz -> {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        }
    }, actions = {
        when (currentScreen) {
            MathQuizScreen.Summary -> {
                IconButton(onClick = onInfoOpen) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.info)
                    )
                }
                IconButton(onClick = onClickShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share)
                    )
                }
            }

            MathQuizScreen.Quiz -> {
                Timer(startTime = quizMode.getTotalTimeInMinutes(), onComplete = onTimerComplete)
            }

            else -> {}
        }
    }, modifier = modifier)
}

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
    var quizResults by rememberSaveable {
        mutableStateOf<Results?>(null)
    }
    val context = LocalContext.current
    val quiz = stringResource(R.string.app_name)
    var isDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    ModalNavigationDrawer(
        drawerContent = {
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
                        when (val mathQuizScreen = item.toMathQuizScreen()) {
                            MathQuizScreen.Start -> navController.goBackHome()
                            else -> navController.navigate(mathQuizScreen.name)
                        }
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
        },
        drawerState = drawerState,
        gesturesEnabled = currentScreen == MathQuizScreen.Start || currentScreen == MathQuizScreen.Progress
    ) {
        Scaffold(topBar = {
            MathQuizAppBar(currentScreen = currentScreen, onClickNavigationItem = {
                scope.launch {
                    drawerState.open()
                }
            }, onClickShare = {
                val questionsAnswered = quizResults!!.problems.count()
                val questionsPerMinute = quizResults!!.questionsPerMinute
                val averageAccuracy = quizResults!!.averageAccuracy
                val shareText =
                    "For ${viewModel.quizMode.name.lowercase()}, I answered $questionsAnswered ${if (questionsAnswered == 1) "question" else "questions"} at a rate of $questionsPerMinute ${if (questionsPerMinute == 1) "question" else "questions"} per minute${if (averageAccuracy == null) "" else " with an average accuracy of $averageAccuracy%"}!"
                shareScore(context, quiz, shareText)
            }, navigateUp = {
                navController.goBackHome()
            }, quizMode = viewModel.quizMode, onTimerComplete = {
                scope.launch {
                    quizResults = viewModel.exportResults()
                    navController.navigate(MathQuizScreen.Summary.name)
                }
            }, onInfoOpen = {
                isDialogOpen = true
            })
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MathQuizScreen.Start.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = MathQuizScreen.Start.name) {
                    StartScreen(onNavigate = {
                        viewModel.setQuizMode(it)
                        viewModel.clear()
                        navController.navigate(MathQuizScreen.Quiz.name)
                    })
                }
                composable(route = MathQuizScreen.Quiz.name) {
                    QuizScreen(viewModel)
                }
                composable(route = MathQuizScreen.Summary.name) {
                    BackHandler {
                        navController.goBackHome()
                    }
                    SummaryScreen(
                        results = quizResults!!,
                        isDialogOpen = isDialogOpen,
                        onCloseInfo = {
                            isDialogOpen = false
                        })
                }
                composable(route = MathQuizScreen.Progress.name) {
                    ProgressScreen()
                }
            }
        }
    }
}

@Composable
fun Timer(startTime: Int, onComplete: () -> Unit) {
    val totalTime = startTime * 60
    var currentTime by rememberSaveable {
        mutableIntStateOf(totalTime)
    }
    var progress by rememberSaveable {
        mutableFloatStateOf(1.0f)
    }
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "timerProgress"
    ).value

    LaunchedEffect(key1 = currentTime) {
        if (currentTime > 0) {
            delay(1_000L)
            currentTime--
            progress = currentTime.toFloat() / totalTime
        } else {
            onComplete()
        }
    }
    CircularProgressIndicator(progress = animatedProgress)
}

fun NavHostController.goBackHome() {
    this.popBackStack(MathQuizScreen.Start.name, inclusive = false)
}

private fun shareScore(context: Context, subject: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(
        Intent.createChooser(
            intent, context.getString(R.string.share_score)
        )
    )
}