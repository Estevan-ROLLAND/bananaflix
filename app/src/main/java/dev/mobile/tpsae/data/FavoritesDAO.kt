package dev.mobile.tpsae.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.mobile.tpsae.model.Movie

@Dao
interface FavoritesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: Movie)

    @Query("SELECT * FROM movieDetails WHERE id = :movieId")
    suspend fun getFavorite(movieId: Int): Movie?

    @Query("DELETE FROM movieDetails WHERE id = :movieId")
    suspend fun deleteFavorite(movieId: Int)

    @Query("SELECT * FROM movieDetails")
    suspend fun getAllFavorites(): List<Movie>
}