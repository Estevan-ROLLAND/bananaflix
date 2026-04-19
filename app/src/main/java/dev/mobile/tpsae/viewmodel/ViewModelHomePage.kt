package dev.mobile.tpsae.viewmodel

import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mobile.tpsae.data.DAO
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.model.MoviePage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScreenState {
    object Idle : ScreenState
    object Loading : ScreenState
    data class SuccessMoviePage(val theMovie: MoviePage) : ScreenState
    data class SuccessMovieDetail(val theMovie: Movie) : ScreenState
    data class SuccessFavorites(val movies: List<Movie>) : ScreenState
    data class Error(val message: String) : ScreenState
}

class ViewModelHomePage: ViewModel(){

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _actionMovieState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val actionMovieState = _actionMovieState.asStateFlow()

    private val _adventureMovieState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val adventureMovieState = _adventureMovieState.asStateFlow()

    private val _fantasyMovieState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val fantasyMovieState = _fantasyMovieState.asStateFlow()

    private val _horrorMovieState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val horrorMovieState = _horrorMovieState.asStateFlow()

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

    fun searchByGenre(genreId: Int, pageToLoad: Int = 1) {
        val _state = getStateFlowForGenre(genreId)
        _state.value = ScreenState.Loading
        viewModelScope.launch {
            try {
                val movies = DAO.getByGenre(genreId, pageToLoad)
                if (movies != null) {
                    _state.value = ScreenState.SuccessMoviePage(movies)
                } else {
                    _state.value = ScreenState.Error("Aucun resultat")
                }
            } catch (e: Exception) {
                _state.value = ScreenState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }

    private fun getStateFlowForGenre(genreId: Int): MutableStateFlow<ScreenState> {
        return when (genreId) {
            28 -> _actionMovieState
            12 -> _adventureMovieState
            14 -> _fantasyMovieState
            27 -> _horrorMovieState
            else -> throw IllegalArgumentException("Genre ID non pris en charge")
        }
    }


//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val app = (this[APPLICATION_KEY] as MyApplication)
//                ViewModelHomePage(repository = app.repository)
//            }
//        }
//    }
}