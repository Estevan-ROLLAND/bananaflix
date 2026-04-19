package dev.mobile.tpsae.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.mobile.tpsae.data.MyApplication
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.util.ConstantesAPI.imgURL
import dev.mobile.tpsae.viewmodel.ScreenState
import dev.mobile.tpsae.viewmodel.ViewModelFavoritesPage
import dev.mobile.tpsae.viewmodel.ViewModelMovieDetail
import java.util.Locale


@Composable
fun MovieDetailScreen(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    val heroHeight = 420.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 16.dp)
    ) {
        val scale = remember { Animatable(1.12f) }
        val offsetX = remember { Animatable(36f) }

//        LaunchedEffect(movie.id) {
//            offsetX.animateTo(
//                targetValue = -45f,
//                animationSpec = tween(durationMillis = 7000, easing = FastOutSlowInEasing)
//            )
//        }

        val backdropPath = movie.backdrop_path.orEmpty()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heroHeight)
        ) {
            AsyncImage(
                model = ("$imgURL/$backdropPath"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
//                    .graphicsLayer {
//                        translationX = offsetX.value
//                        scaleX = scale.value
//                        scaleY = scale.value
//                    },
            )

            // Double voile pour une transition cinema plus progressive vers le fond.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Transparent,
                                0.52f to Color.Transparent,
                                0.82f to Color.Black.copy(alpha = 0.65f),
                                1.00f to MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.00f to Color.Black.copy(alpha = 0.16f),
                                0.18f to Color.Transparent,
                                1.00f to Color.Transparent
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = heroHeight - 112.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Card(
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier
                            .width(140.dp)
                            .height(200.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = movie.release_date?.take(4).orEmpty(),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f),
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Icon(
                            Icons.Filled.Star,
                            null,
                            modifier = Modifier.size(22.dp),
                            tint = Color(0xFFF5C827)
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", movie.vote_average),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.W500,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "(${movie.vote_count})",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontWeight = FontWeight.W500,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    val app = LocalContext.current.applicationContext as MyApplication
                    val viewmodel: ViewModelFavoritesPage = viewModel(
                        factory = ViewModelFavoritesPage.factory(app.appDatabase.getFavoritesDAO())
                    )
                    val favoriteState = viewmodel.favoriteMoviesState.collectAsStateWithLifecycle().value
                    var isFavorite by remember { mutableStateOf(false) }
                    var favoriteText by remember { mutableStateOf("Ajouter aux favoris") }

                    LaunchedEffect(Unit) {
                        viewmodel.favoriteMoviesFromDB()
                    }
                    LaunchedEffect(favoriteState, movie.id) {
                        isFavorite = favoriteState.any { it.id == movie.id }
                        favoriteText = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris"
                    }

                    Button(
                        onClick = {
                            if (!isFavorite){
                                isFavorite = true
                                favoriteText = "Retirer des favoris"
                                viewmodel.addingToFavorites(movie)
                            } else {
                                isFavorite = false
                                favoriteText = "Ajouter aux favoris"
                                viewmodel.removingFromFavorites(movie)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isFavorite) Color.Red else Color.Gray.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    ) {
                        Text(
                            text = favoriteText,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Ajouter aux favoris",
                            tint = if (!isFavorite) Color.White else Color.LightGray.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            Card(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.74f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = movie.overview,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        lineHeight = 23.sp
                    )
                }
            }
        }
    }
}