package dev.mobile.tpsae.ui.screen

import android.content.res.Resources
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp, 10.dp),
    ) {
        // Set la langue de l'app, le theme, etc
        Text(
            text = "Paramètres",
            fontSize = 40.sp
        )

        // LANGUAGE
        Text(
            text = "Langue",
            fontSize = 25.sp,
            modifier = Modifier.padding(top = 20.dp)
        )
        var selectedLanguage by remember { mutableStateOf("fr") }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            var selected by remember { mutableStateOf(false) }
            if (selectedLanguage == "fr") {
                selected = true
            } else {
                selected = false
            }
            RadioButton(
                selected = selected,
                onClick = {
                    if (selectedLanguage == "en") {
                        selected = true
                        selectedLanguage = "fr"
                    } else {
                        selected = false
                        selectedLanguage = "en"
                    }
                }
            )
            Text(
                text = "Français",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            var selected by remember { mutableStateOf(false) }
            if (selectedLanguage == "en") {
                selected = true
            } else {
                selected = false
            }
            RadioButton(
                selected = selected,
                onClick = {
                    if (selectedLanguage == "fr") {
                        selected = true
                        selectedLanguage = "en"
                    } else {
                        selected = false
                        selectedLanguage = "fr"
                    }
                }
            )
            Text(
                text = "Anglais",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // THEME
        Text(
            text = "Thème",
            fontSize = 25.sp,
            modifier = Modifier.padding(top = 20.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            var checked by remember { mutableStateOf(false) }
            if (isSystemInDarkTheme()) {
                checked = true
            } else {
                checked = false
            }
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it

                }
            )
            Text(
                text = "Thème Sombre",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 20.dp)
            )
        }
    }
}