package dev.mobile.tpsae.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.mobile.tpsae.model.Genre
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.model.MoviePage
import dev.mobile.tpsae.util.ConstantesAPI
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.Date

/**
 * Test de la création d'un DAO
 * Afin que les requests de ktor fonctionne, il faut un client (ici mis en attribut)
 * Cependant, ce client peux être configuré, et je ne sais pas quel genre de configuration pourrais être utile
 */
object DAO {
    private val client by lazy {
        HttpClient(Android) {

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
    }

    var popularsMovies = mutableStateOf<MoviePage?>(null)
        private set
    var genres = mutableStateOf<List<Genre>>(emptyList())
        private set

    // On peut aussi stocker la date de la dernière mise à jour, pour éviter de faire trop de requêtes
    var lastUpdate = mutableStateOf<Date?>(null)
        private set

    suspend fun getPopulars(page: Int = 1): MoviePage? {
        if (lastUpdate.value != null && popularsMovies.value != null) {
            val now = Date()
            val diff = now.time - lastUpdate.value!!.time
            if (diff < 60 * 60 * 1000) { // Si la dernière mise à jour a moins d'une heure, on retourne les données en cache
                return popularsMovies.value
            }
        }
        val suffix = "/movie/popular?api_key=${ConstantesAPI.key}&language=${ConstantesAPI.lang}&page=${page}"
        try {
            val response: HttpResponse = client.get(ConstantesAPI.baseURL.plus(suffix))
            popularsMovies = mutableStateOf(response.body<MoviePage>())
            return popularsMovies.value
        }  catch(_: Exception) {
            return null
        }
    }

    suspend fun getById(id: Int): Movie? {
        val suffix = "/movie/${id}?api_key=${ConstantesAPI.key}&language=${ConstantesAPI.lang}"
        try {
            val response: HttpResponse = client.get(ConstantesAPI.baseURL.plus(suffix))
            return response.body<Movie>()
        }  catch(_: Exception) {
            return null
        }
    }

    suspend fun find(term: String, page:Int = 1): MoviePage? {
        if (term.isBlank()) return getPopulars(page)

        try {
            val response: HttpResponse = client.get(ConstantesAPI.baseURL.plus("/search/movie")) {
                parameter("api_key", ConstantesAPI.key)
                parameter("language", ConstantesAPI.lang)
                parameter("query", term)
                parameter("page", page)
                parameter("include_adult", false)
            }
            return response.body<MoviePage>()
        } catch (_: Exception) {
            return null
        }
    }

    suspend fun getByGenre(genreId: Int, page: Int = 1): MoviePage? {
        val suffix = "/discover/movie?api_key=${ConstantesAPI.key}&language=${ConstantesAPI.lang}&with_genres=${genreId}&page=${page}"
        try {
            val response: HttpResponse = client.get(ConstantesAPI.baseURL.plus(suffix))
            return response.body<MoviePage>()
        }  catch(_: Exception) {
            return null
        }
    }

}