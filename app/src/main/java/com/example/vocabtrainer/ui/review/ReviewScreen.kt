package com.example.vocabtrainer.ui.review

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private const val CONTENT_ANIMATION_DURATION = 500


//@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
//@Preview(showSystemUi = true)
//@Composable
//fun RevPreview() {
//    val modifier = Modifier
//    var input by remember { mutableStateOf("") }
//    var count by remember { mutableStateOf(0) }
//    Scaffold(
//        topBar = {
//            ReviewTopAppBar(
//                vocabIndex = -1,
//                totalVocabCount = 4
//            )
//        },
//
//        content = {
//            AnimatedContent(
//                targetState = count,
//                transitionSpec = {
//                    val animationSpec: TweenSpec<IntOffset> = tween(CONTENT_ANIMATION_DURATION)
//                    val direction = AnimatedContentScope.SlideDirection.Left
//                    slideIntoContainer(
//                        towards = direction,
//                        animationSpec = animationSpec
//                    ) with
//                            slideOutOfContainer(
//                                towards = direction,
//                                animationSpec = animationSpec
//                            )
//                }
//            ) { targetState ->
//                ReviewVocab(
//                    word = "des Olives",
//                    input = input,
//                    text = targetState.toString(),
//                    onValueChange = { input = it },
//                    onGo = {
//                        input = ""
//                        count++
//                    },
//                    modifier = modifier
//                )
//            }
//        }
//    )
//}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel = viewModel(),
) {
    val modifier = Modifier
    var input by remember { mutableStateOf("") }
    val uiState by mutableStateOf(viewModel.uiState)

    Crossfade(targetState = uiState.currentState) { state ->
        when (state) {
            State.LOADING -> Text(text = "Not fetched yet, please hold")
            State.LEARNING -> {
                ReviewTopAppBar(
                    vocabIndex = uiState.vocabIndex - 1,
                    totalVocabCount = uiState.vocabs.size
                )

                AnimatedContent(
                    targetState = uiState.vocabIndex,
                    transitionSpec = {
                        val animationSpec: TweenSpec<IntOffset> = tween(CONTENT_ANIMATION_DURATION)
                        val direction = AnimatedContentScope.SlideDirection.Left
                        slideIntoContainer(
                            towards = direction,
                            animationSpec = animationSpec
                        ) with
                                slideOutOfContainer(
                                    towards = direction,
                                    animationSpec = animationSpec
                                )
                    }
                ) { targetState ->
                    val color: Color by animateColorAsState(if (uiState.wrongAnswer) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant)
                    ReviewVocab(
                        word = uiState.vocabs[targetState].domesticWord,
                        input = input,
                        color = color,
                        text = targetState.toString(),
                        onValueChange = {
                            input = it
                            uiState.wrongAnswer = false
                        },
                        onGo = {
//                        if (viewModel.checkInput(input)) {
                            input = ""
                            viewModel.incrementIndex()
//                        } else {
//                            viewModel.isWrong = true
//                            Log.d("Check if correct", "NOT CORRECT, TRY AGAIN")
//                        }
                        },
                        modifier = modifier
                    )
                }
            }
            State.FINISHED -> DefaultText(
                text = "Another review done and dusted",
                modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewVocab(
    word: String,
    input: String,
    text: String,
    color: Color,
    onValueChange: (String) -> Unit,
    onGo: () -> Unit,
    modifier: Modifier
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReviewCard(
            word = word,
            modifier = modifier
        )
        TextField(  //Input
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = input,
            onValueChange = { onValueChange(it) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
                onGo = { onGo() }
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = color
            )
        )
        DefaultText(
            //Displays Count
            text = text,
            modifier = modifier,
        )
    }
}

@Composable
fun ReviewCard(
    word: String,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3F),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            DefaultText(
                text = word,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ReviewTopAppBar(
    vocabIndex: Int,
    totalVocabCount: Int,
//    onBackPressed: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = (vocabIndex + 1) / totalVocabCount.toFloat(),
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        )
    }

}

@Composable
fun DefaultText(
    text: String,
    modifier: Modifier
) {
    Text(
        modifier = modifier
            .fillMaxWidth(),
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineLarge,
    )
}