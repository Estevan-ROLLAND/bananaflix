package dev.mobile.tpsae.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.mobile.tpsae.data.MyApplication
import dev.mobile.tpsae.viewmodel.ViewModelFavoritesPage

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as MyApplication
    val viewModel: ViewModelFavoritesPage = viewModel(
        factory = ViewModelFavoritesPage.factory(app.appDatabase.getFavoritesDAO())
    )
    val favoriteState = viewModel.favoriteMoviesState.collectAsStateWithLifecycle().value

    // Load favorite movies from the database when the composable is first launched
    LaunchedEffect(Unit) {
        viewModel.favoriteMoviesFromDB()
    }

    // Add a lifecycle observer to refresh the favorite movies list when the screen is resumed
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) { // Use DisposableEffect to manage the lifecycle observer
        // Create a LifecycleEventObserver that listens for the ON_RESUME event, which indicates that the screen has become active again
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.favoriteMoviesFromDB()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        // Remove the observer when the composable is disposed to prevent memory leaks
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp, 10.dp),
    ) {
        Text(
            text = "Favoris",
            fontSize = 40.sp,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (favoriteState.isEmpty()) {
                    item {
                        Text(
                            text = "Aucun film en favoris",
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                items(favoriteState) { movie ->
                    MovieCardItem(movie = movie)
                }
            }
        }
    }
}