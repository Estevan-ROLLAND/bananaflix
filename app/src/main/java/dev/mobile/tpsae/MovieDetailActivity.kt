package dev.mobile.tpsae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.mobile.tpsae.ui.screen.LoadingScreen
import dev.mobile.tpsae.viewmodel.ScreenState
import dev.mobile.tpsae.viewmodel.ViewModelMovieDetail
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.dp
import dev.mobile.tpsae.ui.screen.MovieDetailScreen
import dev.mobile.tpsae.ui.theme.TpSaeTheme

class MovieDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val movieId = intent.getIntExtra("movieId", -1)
            val isDarkTheme = isSystemInDarkTheme()
            TpSaeTheme(
                isDarkTheme
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 46.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        finish() },
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                ) { innerPadding ->
                    MovieDetail(modifier = Modifier.padding(innerPadding), movieId = movieId)
                }
            }
        }
    }
}

@Composable
fun MovieDetail(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMovieDetail = viewModel(),
    movieId: Int
) {
    val uiState by viewModel.actionMovieState.collectAsStateWithLifecycle()
    viewModel.fetchMovieDetail(movieId)
    when (val state = uiState) {
        is ScreenState.Idle -> Text("Liste Film")
        is ScreenState.Loading -> LoadingScreen(modifier)
        is ScreenState.SuccessMovieDetail -> MovieDetailScreen(state.theMovie)
        is ScreenState.Error -> Text(
            "Erreur de chargement : ${state.message}",
            color = Color.Red
        )
        else -> Text("Liste Film")
    }
}
