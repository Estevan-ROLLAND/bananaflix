package dev.mobile.tpsae.viewmodel

import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mobile.tpsae.data.DAO
import dev.mobile.tpsae.model.MoviePage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelMovieDetail: ViewModel(){

    private val _movieDetailState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val actionMovieState = _movieDetailState.asStateFlow()

    fun fetchMovieDetail(idMovie: Int) {
        _movieDetailState.value = ScreenState.Loading
        viewModelScope.launch {
            try {
                val movies = DAO.getById(idMovie)
                if (movies != null) {
                    _movieDetailState.value = ScreenState.SuccessMovieDetail(movies)
                } else {
                    _movieDetailState.value = ScreenState.Error("Erreur de chargement")
                }
            } catch (e: Exception) {
                _movieDetailState.value = ScreenState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }
}