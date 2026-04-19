package dev.mobile.tpsae.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mobile.tpsae.data.DAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelSearchPage: ViewModel(){

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val uiState = _uiState.asStateFlow()

    var page = 1

    fun fetchMoviePage(pageToLoad: Int = page) {
        _uiState.value = ScreenState.Loading
        viewModelScope.launch {
            try {
                val movies = DAO.getPopulars(pageToLoad)
                if (movies != null) {
                    _uiState.value = ScreenState.SuccessMoviePage(movies)
                } else {
                    _uiState.value = ScreenState.Error("Erreur de chargement")
                }
            } catch (e: Exception) {
                _uiState.value = ScreenState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun searchMovies(term: String, pageToLoad: Int = 1) {
        if (term.isBlank()) {
            fetchMoviePage()
            return
        }

        _uiState.value = ScreenState.Loading
        viewModelScope.launch {
            try {
                val movies = DAO.find(term.trim(), pageToLoad)
                if (movies != null) {
                    val normalizedTerm = term.trim()
                    val filteredResults = movies.results.filter { movie ->
                        movie.title.contains(normalizedTerm, ignoreCase = true) ||
                                movie.original_title.contains(normalizedTerm, ignoreCase = true)
                    }

                    _uiState.value = ScreenState.SuccessMoviePage(
                        movies.copy(results = filteredResults)
                    )
                } else {
                    _uiState.value = ScreenState.Error("Aucun resultat")
                }
            } catch (e: Exception) {
                _uiState.value = ScreenState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    fun searchMoviesByGenre(term: String, genreId: Int, pageToLoad: Int = 1) {
        _uiState.value = ScreenState.Loading
        viewModelScope.launch {
            try {
                val movies = DAO.getByGenre(genreId, pageToLoad)
                if (movies != null) {
                    val normalizedTerm = term.trim()
                    val filteredResults = if (normalizedTerm.isBlank()) {
                        movies.results
                    } else {
                        movies.results.filter { movie ->
                            movie.title.contains(normalizedTerm, ignoreCase = true) ||
                                movie.original_title.contains(normalizedTerm, ignoreCase = true)
                        }
                    }

                    _uiState.value = ScreenState.SuccessMoviePage(
                        movies.copy(results = filteredResults)
                    )
                } else {
                    _uiState.value = ScreenState.Error("Aucun resultat")
                }
            } catch (e: Exception) {
                _uiState.value = ScreenState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}