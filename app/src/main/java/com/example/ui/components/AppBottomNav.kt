package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandSaffron

sealed class Screen(val route: String, val title: String, val titleHi: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", "होम", Icons.Default.Home)
    object ToolsHub : Screen("tools_hub", "Tools", "टूल्स", Icons.Default.Build)
    object DigitalSeva : Screen("digital_seva", "Seva", "सीएससी", Icons.Default.Public)
    object PrintShop : Screen("print_shop", "Print", "प्रिंट", Icons.Default.LocalPrintshop)
    object Admin : Screen("admin", "Admin", "एडमिन", Icons.Default.AdminPanelSettings)
}

@Composable
fun AppBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Screen.Home,
        Screen.ToolsHub,
        Screen.DigitalSeva,
        Screen.PrintShop,
        Screen.Admin
    )

    NavigationBar(
        containerColor = BrandDarkNavy,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen.route) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandDarkNavy,
                    selectedTextColor = BrandSaffron,
                    indicatorColor = BrandSaffron,
                    unselectedIconColor = Color(0xFFB0BEC5),
                    unselectedTextColor = Color(0xFF90A4AE)
                ),
                modifier = Modifier.testTag("nav_item_${screen.route}")
            )
        }
    }
}
