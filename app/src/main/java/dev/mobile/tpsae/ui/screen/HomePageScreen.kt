package dev.mobile.tpsae.ui.screen

import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import dev.mobile.tpsae.Greeting
import dev.mobile.tpsae.ui.theme.TpSaeTheme
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.util.ConstantesAPI.imgURL
import dev.mobile.tpsae.viewmodel.ScreenState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.mobile.tpsae.model.Genre
import dev.mobile.tpsae.MovieDetailActivity
import dev.mobile.tpsae.model.MoviePage
import dev.mobile.tpsae.viewmodel.ViewModelHomePage


@Composable
// @OptIn(ExperimentalMaterialApi::class)
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModelHomePage  = viewModel()
) {
//    var isRefreshing by remember { mutableStateOf(false) }
//    val refreshState = rememberPullRefreshState(
//        refreshing = isRefreshing,
//        onRefresh = { viewModel.fetchMoviePage() }
//    )

    Column(modifier = modifier
        .fillMaxSize()
        .padding(10.dp)
        .verticalScroll(rememberScrollState())
//        .pullRefresh(refreshState)
    ) {
//        PullRefreshIndicator(
//            refreshing = isRefreshing,
//            state = refreshState,
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        )
        // TITLE
        Text(
            text = "BananaFlix",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // FILMS POPULAIRES
        Column {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            Text(
                text = "Films populaires",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            viewModel.fetchMoviePage()
            when (val state = uiState) {
                is ScreenState.Idle -> Text("Liste Film")
                is ScreenState.Loading -> LoadingScreen(modifier)
                is ScreenState.SuccessMoviePage -> ListMovies(state.theMovie)
                is ScreenState.Error -> Text(
                    "Erreur de chargement : ${state.message}",
                    color = Color.Red
                )
                else -> Text("Liste Film")
            }
        }

        // FILMS ACTION
        GenreItem(
            genre = Genre(id = 28, name = "Action"),
            viewModel = viewModel,
            state = viewModel.actionMovieState.collectAsStateWithLifecycle().value
        )

        // FILMS AVENTURE
        GenreItem(
            genre = Genre(id = 12, name = "Aventure"),
            viewModel = viewModel,
            state = viewModel.adventureMovieState.collectAsStateWithLifecycle().value
        )

        // FILMS FANTASTIQUE
        GenreItem(
            genre = Genre(id = 14, name = "Fantastique"),
            viewModel = viewModel,
            state = viewModel.fantasyMovieState.collectAsStateWithLifecycle().value
        )

        // FILMS HORREUR

        GenreItem(
            genre = Genre(id = 27, name = "Horreur"),
            viewModel = viewModel,
            state = viewModel.horrorMovieState.collectAsStateWithLifecycle().value
        )
    }
}

@Composable
fun GenreItem(genre: Genre, viewModel: ViewModelHomePage, state: ScreenState, modifier: Modifier = Modifier){
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = genre.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        viewModel.searchByGenre(genre.id) // Genre ID pour l'action
        when (val state = state) {
            is ScreenState.Idle -> Text("Liste Film")
            is ScreenState.Loading -> LoadingScreen(modifier)
            is ScreenState.SuccessMoviePage -> ListMovies(state.theMovie)
            is ScreenState.Error -> Text(
                "Erreur de chargement : ${state.message}",
                color = Color.Red
            )
            else -> Text("Liste Film")
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ListMovies(movie: MoviePage, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
    ) {
        movie.results.forEach { movie ->
            item {
                MoviePosterItem(movie=movie)
            }
        }
    }
}

@Composable
fun MoviePosterItem(movie: Movie, modifier: Modifier = Modifier, /*onClick: (Movie) -> Unit*/) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = {
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("movieId", movie.id)
                context.startActivity(intent)
            }),
        shape = RoundedCornerShape(10.dp)

    ) {
        Row(
            modifier = modifier
                .width(155.5.dp)
                .height(230.4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val posterPath = movie.poster_path.orEmpty()
            AsyncImage(
                model = ("$imgURL/$posterPath"),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TpSaeTheme {
        HomeScreen()
    }
}