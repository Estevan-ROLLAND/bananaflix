package dev.mobile.tpsae.data

import android.app.Application
import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MyApplication : Application() {
    val client by lazy {
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

    val appDatabase: MovieDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            MovieDatabase::class.java,
            "movie_database.db"
        ).build()
    }

}