package dev.mobile.tpsae

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mobile.tpsae.ui.screen.FilterScreen
import dev.mobile.tpsae.ui.theme.TpSaeTheme
import dev.mobile.tpsae.model.SearchFiltersContract

class FilterSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialFilters = SearchFiltersContract.getFilters(intent)

        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            TpSaeTheme(
                isDarkTheme
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 46.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        setResult(RESULT_CANCELED)
                                        finish()
                                    },
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                ) { innerPadding ->
                    FilterScreen(
                        modifier = Modifier.padding(innerPadding),
                        initialFilters = initialFilters,
                        onApply = { filters ->
                            val resultIntent = Intent()
                            SearchFiltersContract.putFilters(resultIntent, filters)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}
