package dev.mobile.tpsae.model

import kotlinx.serialization.Serializable

@Serializable
data class MoviePage(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)
