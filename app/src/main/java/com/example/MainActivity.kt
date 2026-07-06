package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.screens.CropDoctorScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MandiAssistantScreen
import com.example.ui.screens.CropInfoScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KisanApp()
            }
        }
    }
}

@Composable
fun KisanApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val dashboardViewModel: com.example.ui.DashboardViewModel = viewModel()
    val mandiViewModel: com.example.ui.MandiViewModel = viewModel()
    
    val items = listOf(
        Screen.Dashboard,
        Screen.CropDoctor,
        Screen.MandiAssistant,
        Screen.CropInfo
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.ui.graphics.Color.White,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, style = MaterialTheme.typography.labelSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                        selected = currentRoute == screen.route,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = com.example.ui.theme.Emerald700,
                            selectedTextColor = com.example.ui.theme.Emerald700,
                            indicatorColor = com.example.ui.theme.Emerald100,
                            unselectedIconColor = com.example.ui.theme.Slate400,
                            unselectedTextColor = com.example.ui.theme.Slate400
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToCropDoctor = {
                        navController.navigate(Screen.CropDoctor.route)
                    },
                    onNavigateToMandiAssistant = {
                        navController.navigate(Screen.MandiAssistant.route)
                    }
                )
            }
            composable(Screen.CropDoctor.route) {
                CropDoctorScreen(viewModel)
            }
            composable(Screen.MandiAssistant.route) {
                MandiAssistantScreen(mandiViewModel)
            }
            composable(Screen.CropInfo.route) {
                CropInfoScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    object CropDoctor : Screen("crop_doctor", "Crop Doctor", Icons.Filled.LocalHospital)
    object MandiAssistant : Screen("mandi_assistant", "Mandi", Icons.Filled.Storefront)
    object CropInfo : Screen("crop_info", "Crops", Icons.Filled.Eco)
}
