package dev.mobile.tpsae.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.mobile.tpsae.data.FavoritesDAO
import dev.mobile.tpsae.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelFavoritesPage(
    private val dao: FavoritesDAO
) : ViewModel() {
    private val _favoriteMoviesState = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMoviesState = _favoriteMoviesState.asStateFlow()

    fun favoriteMoviesFromDB() {
        viewModelScope.launch {
            _favoriteMoviesState.value = dao.getAllFavorites()
        }
    }

    fun removingFromFavorites(movie: Movie) {
        viewModelScope.launch {
            dao.deleteFavorite(movie.id)
            _favoriteMoviesState.value = dao.getAllFavorites()
        }
    }

    fun addingToFavorites(movie: Movie) {
        viewModelScope.launch {
            dao.insertFavorite(movie)
            _favoriteMoviesState.value = dao.getAllFavorites()
        }
    }

    // Factory for creating ViewModel with parameters
    companion object {
        /** Factory method to create ViewModelFavoritesPage with a FavoritesDAO parameter.
         * This allows us to inject the DAO when creating the ViewModel, which is necessary
         * since ViewModels are typically created by the system and need a way to receive dependencies.
         */
        fun factory(dao: FavoritesDAO): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ViewModelFavoritesPage::class.java)) {
                        return ViewModelFavoritesPage(dao) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}