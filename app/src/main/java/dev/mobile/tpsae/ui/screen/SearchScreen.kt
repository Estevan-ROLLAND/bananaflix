package dev.mobile.tpsae.ui.screen

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.mobile.tpsae.FilterSearchActivity
import dev.mobile.tpsae.MovieDetailActivity
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.model.SearchFilters
import dev.mobile.tpsae.model.SearchFiltersContract
import dev.mobile.tpsae.util.ConstantesAPI.imgURL
import dev.mobile.tpsae.viewmodel.ScreenState
import dev.mobile.tpsae.viewmodel.ViewModelSearchPage
import kotlinx.coroutines.delay

private val genreLabels = mapOf(
    28 to "Action",
    12 to "Aventure",
    16 to "Animation",
    35 to "Comedie",
    80 to "Crime",
    18 to "Drame",
    14 to "Fantastique",
    27 to "Horreur",
    10749 to "Romance",
    878 to "Science-fiction"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModelSearchPage = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchTerm by rememberSaveable { mutableStateOf("") }
    var minVoteAverage by rememberSaveable { mutableDoubleStateOf(0.0) }
    var dates by rememberSaveable { mutableStateOf(1900 to 2026) }
    var genres by rememberSaveable { mutableStateOf(emptySet<Int>()) }
    val appliedFilters = SearchFilters(
        minVoteAverage = minVoteAverage,
        dates = dates,
        genres = genres
    )
    val selectedGenreId = genres.firstOrNull()
    val selectedGenreLabel = selectedGenreId?.let { genreLabels[it] } ?: "Tous"
    val context = LocalContext.current
    val filterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val filters = SearchFiltersContract.getFilters(result.data)
            minVoteAverage = filters.minVoteAverage
            dates = filters.dates
            genres = filters.genres
        }
    }

    // Debounce pour eviter un appel reseau a chaque caractere tape.
    LaunchedEffect(searchTerm, selectedGenreId) {
        delay(300)
        if (selectedGenreId != null) {
            viewModel.searchMoviesByGenre(searchTerm, selectedGenreId)
        } else if (searchTerm.isBlank()) {
            viewModel.fetchMoviePage()
        } else {
            viewModel.searchMovies(searchTerm)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchTerm,
                onValueChange = { searchTerm = it },
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .padding(10.dp),
                leadingIcon = { Icon(Icons.Default.Search, "Recherche") },
                trailingIcon = {
                    if (searchTerm.isNotBlank()) {
                        Icon(
                            Icons.Default.Close,
                            "Effacer",
                            modifier = Modifier
                                .clickable { searchTerm = "" }
                        )
                    }
                },
                singleLine = true,
                label = { Text("Rechercher un film") }
            )
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Filtres",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = {
                        val intent = Intent(context, FilterSearchActivity::class.java)
                        SearchFiltersContract.putFilters(intent, appliedFilters)
                        filterLauncher.launch(intent)
                    })
            )
        }


        Text(
            text = "Résultats pour : ${searchTerm.ifBlank { "" }}",
            modifier = Modifier.padding(10.dp),
            fontSize = 20.sp
        )

        Text(
            text = "Filtres: Genre : $selectedGenreLabel | Date de sortie : ${dates.first}-${dates.second} | note >= ${"%.1f".format(appliedFilters.minVoteAverage)}",
            modifier = Modifier.padding(horizontal = 10.dp),
            fontSize = 14.sp,
            color = Color.Gray
        )

        when (val state = uiState) {
            is ScreenState.Idle -> Text("Liste Film")
            is ScreenState.Loading -> LoadingScreen()
            is ScreenState.SuccessMoviePage -> {
                val filteredResults = state.theMovie.results.filter { movie ->
                    movieMatchesFilters(movie, appliedFilters)
                }

                if (filteredResults.isEmpty()) {
                    Text(
                        text = "Aucun film ne correspond aux filtres.",
                        modifier = Modifier.padding(10.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredResults, key = { it.id }) { movie ->
                            MovieCardItem(movie = movie)
                        }
                    }
                }
            }
            is ScreenState.Error -> Text("Erreur de chargement : ${state.message}", color = Color.Red)
            else -> Text("Liste Film")
        }
    }
}

@Composable
fun MovieCardItem(movie: Movie, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(modifier = modifier
        .padding(8.dp)
        .fillMaxWidth()
        .clickable(
            onClick = {
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("movieId", movie.id)
                context.startActivity(intent)
            }
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp,Color.LightGray)
    ) {
        Box(modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        ){

            Row(verticalAlignment = Alignment.CenterVertically) {

                Card(
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = modifier
                            .width(100.dp)
                            .height(130.dp),
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

                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = movie.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text(text = movie.release_date?.take(4).orEmpty(), fontWeight = FontWeight.W300, fontSize = 14.sp)
                    Text(text = movie.overview, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 15.sp)
                    Row {
                        Icon(
                            Icons.Filled.Star,
                            null,
                            modifier = modifier.size(25.dp),
                            tint = Color(0xFFF5C827)
                        )
                        Text(text = movie.vote_average.toString(), fontWeight = FontWeight.W300, fontSize = 15.sp)
                        Spacer(modifier = modifier.width(16.dp))
                        Text(text = "(${movie.vote_count})", fontWeight = FontWeight.W300, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun movieMatchesFilters(movie: Movie, filters: SearchFilters): Boolean {
    val respectsVote = movie.vote_average >= filters.minVoteAverage
    val releaseYear = movie.release_date
        ?.take(4)
        ?.toIntOrNull()
    val respectsDate = releaseYear?.let { it in filters.dates.first..filters.dates.second } ?: true
    return respectsVote && respectsDate
}
