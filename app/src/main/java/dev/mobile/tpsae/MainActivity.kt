package dev.mobile.tpsae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.mobile.tpsae.ui.theme.TpSaeTheme
import dev.mobile.tpsae.data.DAO
import dev.mobile.tpsae.ui.screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val isDarkTheme = isSystemInDarkTheme()
            TpSaeTheme(
                isDarkTheme
            ) {
                val navController = rememberNavController()
                val startDestination = AppDestinations.HOME
                var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                            AppDestinations.entries.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedDestination == index,
                                    onClick = {
                                        navController.navigate(route = destination.route)
                                        selectedDestination = index
                                    },
                                    icon = {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.label
                                        )
                                    },
                                    label = { Text(destination.label) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // La coroutine doit se trouver dans un runBlocking.
    // J'ai pas trouvé d'autre solution.
    runBlocking {
        coroutineScope {
            println("//// C'est le coroutineScope")
            println(DAO.getPopulars())
            this.launch {
                println("//// C'est le launch du coroutineScope")
                println("// DAO.getPopulars()\n"+DAO.getPopulars())
            }
            this.launch {
                println("// DAO.getById(5)\n"+DAO.getById(5))
            }
            this.launch {
                println("// DAO.find(\"deded\")\n"+DAO.find("deded"))
            }
        }
        // On peut aussi directement faire launch,
        // je sais pas en quoi ça diffère ce celle d'une coroutine
        launch {
            println("//// C'est le launch \n" + DAO.getPopulars())
        }
    }
//    coroutineScope { // this: CoroutineScope
//        this.launch {
//            this.launch {
//                delay(2.seconds)
//                println("Child of the enclosing coroutine completed")
//            }
//            println("Child coroutine 1 completed")
//        }
//        this.launch {
//            delay(1.seconds)
//            println("Child coroutine 2 completed")
//        }
//    }
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TpSaeTheme {
        Greeting("Android")
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: AppDestinations,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        AppDestinations.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    AppDestinations.HOME -> HomeScreen(modifier)
                    AppDestinations.SEARCH -> SearchScreen(modifier)
                    AppDestinations.FAVORITES -> FavoritesScreen(modifier)
                    AppDestinations.PROFILE -> SettingsScreen(modifier)
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    HOME("Accueil", Icons.Default.Home, "home"),
    SEARCH("Recherche", Icons.Default.Search, "search"),
    FAVORITES("Favoris", Icons.Default.Favorite, "favorite"),
   PROFILE("Paramètres", Icons.Default.Settings,"settings"),
}