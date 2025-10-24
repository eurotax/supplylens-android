package com.eurotax.supplylens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eurotax.supplylens.core.ui.theme.SupplyLensTheme
import com.eurotax.supplylens.core.ai.AiForecastEngine
import com.eurotax.supplylens.feature.search.SearchScreen
import com.eurotax.supplylens.feature.watchlist.WatchlistScreen
import com.eurotax.supplylens.feature.alerts.AlertsScreen
import com.eurotax.supplylens.feature.forecast.AiForecastScreen
import com.eurotax.supplylens.feature.settings.SettingsScreen

/**
 * Main Activity with bottom navigation and full app navigation graph.
 * 
 * Navigation: Watchlist → Search → Token Details → Alerts → Forecast → Paywall → Settings
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        setContent {
            SupplyLensTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Get app instance and subscription status
    val context = LocalContext.current
    val app = context.applicationContext as MainApplication
    val subscriptionStatus by app.billingManager.subscriptionStatus.collectAsState()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "watchlist",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("watchlist") {
                WatchlistScreen(
                    subscriptionStatus = subscriptionStatus,
                    onSearchClick = { navController.navigate("search") },
                    onUpgradeClick = { navController.navigate("paywall") },
                    onTokenClick = { chain, address -> 
                        navController.navigate("token/$chain/$address") 
                    }
                )
            }
            
            composable("search") {
                SearchScreen(
                    onTokenClick = { chain, address -> 
                        navController.navigate("token/$chain/$address") 
                    }
                )
            }
            
            composable("alerts") {
                AlertsScreen(
                    subscriptionStatus = subscriptionStatus,
                    onUpgradeClick = { navController.navigate("paywall") }
                )
            }
            
            composable("forecast") {
                AiForecastScreen(
                    subscriptionStatus = subscriptionStatus,
                    forecastEngine = AiForecastEngine(context),
                    onUpgradeClick = { navController.navigate("paywall") }
                )
            }
            
            composable("settings") {
                SettingsScreen()
            }
            
            composable("token/{chain}/{address}") { backStackEntry ->
                val chain = backStackEntry.arguments?.getString("chain") ?: ""
                val address = backStackEntry.arguments?.getString("address") ?: ""
                TokenDetailPlaceholder(chain, address)
            }
            
            composable("paywall") {
                PaywallPlaceholder()
            }
        }
    }
}

private val bottomNavItems = listOf(
    BottomNavItem("watchlist", "Watchlist", Icons.Filled.Star),
    BottomNavItem("search", "Search", Icons.Filled.Search),
    BottomNavItem("alerts", "Alerts", Icons.Filled.Notifications),
    BottomNavItem("forecast", "Forecast", Icons.Filled.Home),
    BottomNavItem("settings", "Settings", Icons.Filled.Settings)
)

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun SettingsPlaceholder() {
    Text("Settings Screen", modifier = Modifier.fillMaxSize())
}

@Composable
fun TokenDetailPlaceholder(chain: String, address: String) {
    Text("Token Detail: $chain / $address", modifier = Modifier.fillMaxSize())
}

@Composable
fun PaywallPlaceholder() {
    Text("Paywall - Upgrade to PRO (3-day trial)", modifier = Modifier.fillMaxSize())
}
