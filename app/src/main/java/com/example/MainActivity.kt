package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppTopBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.DesignToolsScreen
import com.example.ui.screens.DigitalSevaScreen
import com.example.ui.screens.DocToolsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PhotoToolsScreen
import com.example.ui.screens.PdfToolsScreen
import com.example.ui.screens.PrintShopScreen
import com.example.ui.screens.ToolsHubScreen
import com.example.ui.screens.TrackRequestScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppNavContent()
            }
        }
    }
}

@Composable
fun MainAppNavContent(
    viewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    val shopConfig by viewModel.shopConfig.collectAsState()
    val notification by viewModel.notification.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(it.message)
            viewModel.clearNotification()
        }
    }

    val (screenTitle, screenSubtitle, isSubScreen) = when (currentRoute) {
        "home" -> Triple(shopConfig.shopName, "Digital Seva & Print Tools", false)
        "tools_hub" -> Triple("Digital Tools Hub", "Photo, PDF & Document Utilities", false)
        "passport_photo" -> Triple("Passport Photo Maker", "4x6 & A4 Sheet Printing", true)
        "photo_compress" -> Triple("Photo & Signature Compressor", "Resize to 20KB / 50KB", true)
        "id_card_layout" -> Triple("Dual-Side ID Combiner", "PVC & A4 Print Layout", true)
        "doc_scanner" -> Triple("Document Xerox Filter", "Contrast & Photocopy Enhancer", true)
        "pdf_tools" -> Triple("Images to A4 PDF", "Multi-Image PDF Converter", true)
        "resume_builder" -> Triple("Resume & Biodata Maker", "Job & Matrimonial Formats", true)
        "qr_generator" -> Triple("UPI Payment QR Code", "PhonePe / GPay Store QR", true)
        "digital_seva" -> Triple("CSC Digital Services", "Government Schemes & Certificates", false)
        "track_request" -> Triple("Track Application", "Live Status Tracking Portal", true)
        "print_shop" -> Triple("Online Print Shop", "Digital Printing Order Queue", false)
        "admin" -> Triple("Admin Dashboard", "Operator Management Panel", false)
        else -> Triple(shopConfig.shopName, "Digital Seva & Print Tools", false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = screenTitle,
                subtitle = screenSubtitle,
                canNavigateBack = isSubScreen,
                onBackClick = { navController.popBackStack() },
                announcement = shopConfig.announcementText,
                phone = shopConfig.phone1
            )
        },
        bottomBar = {
            AppBottomNav(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable("tools_hub") {
                    ToolsHubScreen(
                        onNavigate = { route -> navController.navigate(route) }
                    )
                }
                composable("passport_photo") {
                    PhotoToolsScreen(
                        viewModel = viewModel,
                        onNavigateToPrint = { navController.navigate("print_shop") }
                    )
                }
                composable("photo_compress") {
                    PhotoToolsScreen(
                        viewModel = viewModel,
                        onNavigateToPrint = { navController.navigate("print_shop") }
                    )
                }
                composable("id_card_layout") {
                    DocToolsScreen(
                        viewModel = viewModel,
                        onNavigateToPrint = { navController.navigate("print_shop") }
                    )
                }
                composable("doc_scanner") {
                    DocToolsScreen(
                        viewModel = viewModel,
                        onNavigateToPrint = { navController.navigate("print_shop") }
                    )
                }
                composable("pdf_tools") {
                    PdfToolsScreen(
                        viewModel = viewModel
                    )
                }
                composable("resume_builder") {
                    DesignToolsScreen(
                        viewModel = viewModel,
                        initialTab = 0
                    )
                }
                composable("qr_generator") {
                    DesignToolsScreen(
                        viewModel = viewModel,
                        initialTab = 1
                    )
                }
                composable("digital_seva") {
                    DigitalSevaScreen(
                        viewModel = viewModel,
                        onNavigateToTrack = { navController.navigate("track_request") }
                    )
                }
                composable("track_request") {
                    TrackRequestScreen(
                        viewModel = viewModel
                    )
                }
                composable("print_shop") {
                    PrintShopScreen(
                        viewModel = viewModel
                    )
                }
                composable("admin") {
                    AdminScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
