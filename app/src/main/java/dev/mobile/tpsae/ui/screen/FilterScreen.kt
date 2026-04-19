package dev.mobile.tpsae.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mobile.tpsae.model.SearchFilters

@Composable
fun FilterScreen(
    modifier: Modifier = Modifier,
    initialFilters: SearchFilters = SearchFilters(),
    onApply: (SearchFilters) -> Unit,
) {
    val genreOptions = listOf(
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

    var genres by rememberSaveable { mutableStateOf(initialFilters.genres) }
    var minVoteAverage by rememberSaveable { mutableDoubleStateOf(initialFilters.minVoteAverage) }
    var dates by rememberSaveable { mutableStateOf(initialFilters.dates) }
    var isGenreMenuExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Filtres",
            fontSize = 28.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text= "Sortie entre : ${dates.first} et ${dates.second}")
        RangeSlider(
            value = dates.first.toFloat()..dates.second.toFloat(),
            onValueChange = { range ->
                dates = range.start.toInt() to range.endInclusive.toInt()
            },
            valueRange = 1900f..2026f,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Genre :",
                modifier = Modifier.padding(end = 20.dp)
            )
            Button(
                onClick = { isGenreMenuExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedLabel = genreOptions
                    .firstOrNull { genres.contains(it.first) }
                    ?.second
                    ?: "Tous"
                Text(text = selectedLabel)
            }
            DropdownMenu(
                expanded = isGenreMenuExpanded,
                onDismissRequest = { isGenreMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Tous") },
                    onClick = {
                        genres = emptySet()
                        isGenreMenuExpanded = false
                    }
                )
                genreOptions.forEach { (id, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            genres = setOf(id)
                            isGenreMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Note minimale: ${"%.1f".format(minVoteAverage)} / 10")
        Slider(
            value = minVoteAverage.toFloat(),
            onValueChange = { minVoteAverage = it.toDouble() },
            valueRange = 0f..10f,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onApply(
                    SearchFilters(
                        minVoteAverage = minVoteAverage,
                        dates = dates,
                        genres = genres
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Appliquer")
        }
    }
}