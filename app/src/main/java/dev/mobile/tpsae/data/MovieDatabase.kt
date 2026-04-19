package dev.mobile.tpsae.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.mobile.tpsae.model.Movie


@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun getFavoritesDAO(): FavoritesDAO


}
