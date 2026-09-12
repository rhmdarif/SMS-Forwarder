package id.majopay.ngateway.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Rule
import androidx.compose.material.icons.automirrored.rounded.Rule
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.majopay.ngateway.ui.screen.history.HistoryScreen
import id.majopay.ngateway.ui.screen.rules.RulesScreen
import id.majopay.ngateway.ui.screen.settings.SettingsScreen
import id.majopay.ngateway.ui.screen.setup.CredentialsViewModel
import id.majopay.ngateway.ui.screen.setup.SetupScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsForwarderNavigation() {
    val navController = rememberNavController()
    val credentialsViewModel: CredentialsViewModel = hiltViewModel()
    val credentials by credentialsViewModel.credentials.collectAsState()

    // Start destination hanya dievaluasi sekali saat NavHost pertama kali dibuat:
    // tanpa kredensial -> layar setup (bisa dilewati), selain itu langsung ke Aturan.
    val startDestination = remember {
        if (credentials == null) Screen.Setup.route else Screen.Rules.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Setup.route

    val openSetup: () -> Unit = {
        navController.navigate(Screen.Setup.route) { launchSingleTop = true }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController) },
        // Scaffold luar hanya mengurus bottom bar. Inset status bar diserahkan ke
        // Scaffold/TopAppBar di masing-masing layar agar tidak diterapkan dua kali.
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            // consumeWindowInsets: bottom bar sudah menutup inset navigation bar,
            // jadi Scaffold di dalam layar tidak perlu menambah padding bawah lagi.
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Screen.Setup.route) {
                SetupScreen(
                    onDone = {
                        // Setup selalu keluar ke Aturan dan dibuang dari back stack,
                        // baik saat dibuka dari awal maupun dari banner/Settings.
                        navController.navigate(Screen.Rules.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Rules.route) { RulesScreen(onSetupClick = openSetup) }
            composable(Screen.History.route) { HistoryScreen(onSetupClick = openSetup) }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onCredentialsCleared = {
                        // Setelah hapus: kembali ke layar setup, bersihkan seluruh back stack.
                        navController.navigate(Screen.Setup.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val screens = listOf(Screen.Rules, Screen.History, Screen.Settings)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Flat: bar putih polos tanpa elevasi, indikator pil biru lembut, ikon "rounded" saat aktif.
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 0.dp
    ) {
        screens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    /** Layar setup kredensial; tidak muncul di bottom bar. */
    object Setup : Screen("setup", "Setup", Icons.Outlined.Settings)
    object Rules : Screen("rules", "Aturan", Icons.AutoMirrored.Outlined.Rule, Icons.AutoMirrored.Rounded.Rule)
    object History : Screen("history", "Riwayat", Icons.Outlined.History, Icons.Rounded.History)
    object Settings : Screen("settings", "Pengaturan", Icons.Outlined.Settings, Icons.Rounded.Settings)
}
