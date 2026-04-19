package dev.mobile.tpsae.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "movieDetails")
@Serializable
data class Movie(
    val adult: Boolean,
    val backdrop_path: String? = null,
//    val belongs_to_collection : String?,
//    val budget : Int,
//    val genre_ids: List<Int>,
//    val genres: List<Genre>,
//    val homepage: String,
    @PrimaryKey(autoGenerate = false)
    val id: Int,
//    val imdb_id : String,
//    val origin_country: List<String>,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String? = null,
//    val production_companies: List<Any>,
//    val production_countries: List<Any>,
    val release_date: String? = null,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)