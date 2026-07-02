package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CanvasEditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProUpgradeDialog
import com.example.ui.screens.SafeEscrowScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CanvaViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: CanvaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CanvaCraftApp(viewModel)
            }
        }
    }
}

@Composable
fun CanvaCraftApp(viewModel: CanvaViewModel) {
    val context = LocalContext.current
    val toastMsg by viewModel.toastMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("HOME") } // "HOME", "ESCROW_HUB", "CANVAS_EDITOR"
    var showProDialog by remember { mutableStateOf(false) }

    LaunchedEffect(toastMsg) {
        toastMsg?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentScreen != "CANVAS_EDITOR") {
                NavigationBar(
                    containerColor = Color(0xFFF3EDF7),
                    contentColor = Color(0xFF1D1B20)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "HOME",
                        onClick = { currentScreen = "HOME" },
                        icon = { Icon(Icons.Default.Palette, contentDescription = null, tint = if (currentScreen == "HOME") Color(0xFF21005D) else Color(0xFF49454F)) },
                        label = { Text("Studio", color = if (currentScreen == "HOME") Color(0xFF21005D) else Color(0xFF49454F), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFEADDFF))
                    )

                    NavigationBarItem(
                        selected = currentScreen == "ESCROW_HUB",
                        onClick = { currentScreen = "ESCROW_HUB" },
                        icon = { Icon(Icons.Default.Shield, contentDescription = null, tint = if (currentScreen == "ESCROW_HUB") Color(0xFF146C2E) else Color(0xFF49454F)) },
                        label = { Text("Safe Escrow", color = if (currentScreen == "ESCROW_HUB") Color(0xFF146C2E) else Color(0xFF49454F), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE6F4EA))
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = { showProDialog = true },
                        icon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF6750A4)) },
                        label = { Text("VIP Pro", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE8DEF8))
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentScreen) {
                "HOME" -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenCanvas = { currentScreen = "CANVAS_EDITOR" },
                        onOpenEscrowHub = { currentScreen = "ESCROW_HUB" },
                        onOpenProUpgrade = { showProDialog = true }
                    )
                }
                "ESCROW_HUB" -> {
                    SafeEscrowScreen(
                        viewModel = viewModel,
                        onNavigateToCanvas = { projId ->
                            viewModel.loadProject(projId)
                            currentScreen = "CANVAS_EDITOR"
                        }
                    )
                }
                "CANVAS_EDITOR" -> {
                    CanvasEditorScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "HOME" }
                    )
                }
            }

            if (showProDialog) {
                ProUpgradeDialog(
                    onDismiss = { showProDialog = false },
                    onClaimVipFree = {
                        viewModel.claimVipFreePro()
                    }
                )
            }
        }
    }
}
