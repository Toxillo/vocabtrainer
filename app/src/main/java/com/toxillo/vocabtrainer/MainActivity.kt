package com.toxillo.vocabtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.toxillo.vocabtrainer.navigation.SetupNavGraph
import com.toxillo.vocabtrainer.ui.components.BottomBar
import com.toxillo.vocabtrainer.ui.components.TopBar
import com.toxillo.vocabtrainer.ui.theme.VocabTrainerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VocabTrainerApp()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabTrainerApp() {
    VocabTrainerTheme {
        val navController = rememberNavController()
        Scaffold(
            topBar = {
                TopBar()
            },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            bottomBar = { BottomBar(navController = navController) },
            content = { padding ->
                SetupNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(padding)
                )
            }
        )
    }
}

