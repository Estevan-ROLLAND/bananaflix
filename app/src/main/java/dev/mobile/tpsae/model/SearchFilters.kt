package dev.mobile.tpsae.model

import android.content.Intent
import java.util.Calendar

private val currentYear: Int
    get() = Calendar.getInstance().get(Calendar.YEAR)

data class SearchFilters(
    val minVoteAverage: Double = 0.0,
    val dates: Pair<Int, Int> = Pair(1900, currentYear),
    val genres: Set<Int> = emptySet()
)

object SearchFiltersContract {
    private const val EXTRA_GENRES = "search_filters_genres"
    private const val EXTRA_DATES_START = "search_filters_dates_start"
    private const val EXTRA_DATES_END = "search_filters_dates_end"
    private const val EXTRA_MIN_VOTE_AVERAGE = "search_filters_min_vote_average"

    fun putFilters(intent: Intent, filters: SearchFilters) {
        intent.putIntegerArrayListExtra(EXTRA_GENRES, ArrayList(filters.genres))
        intent.putExtra(EXTRA_DATES_START, filters.dates.first)
        intent.putExtra(EXTRA_DATES_END, filters.dates.second)
        intent.putExtra(EXTRA_MIN_VOTE_AVERAGE, filters.minVoteAverage)
    }

    fun getFilters(intent: Intent?): SearchFilters {
        if (intent == null) return SearchFilters()

        return SearchFilters(
            genres = intent.getIntegerArrayListExtra(EXTRA_GENRES)?.toSet() ?: emptySet(),
            dates = Pair(
                intent.getIntExtra(EXTRA_DATES_START, 1900),
                intent.getIntExtra(EXTRA_DATES_END, currentYear)
            ),
            minVoteAverage = intent.getDoubleExtra(EXTRA_MIN_VOTE_AVERAGE, 0.0)
        )
    }
}

