package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DesignProject
import com.example.ui.viewmodel.CanvaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CanvaViewModel,
    onOpenCanvas: (String) -> Unit,
    onOpenEscrowHub: () -> Unit,
    onOpenProUpgrade: () -> Unit
) {
    val user by viewModel.userProfile.collectAsState()
    val projects by viewModel.allProjects.collectAsState()
    val contracts by viewModel.allContracts.collectAsState()

    var showNewProjDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE8DEF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo_1783007647969),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("CanvaCraft Studio", color = Color(0xFF1D1B20), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        Text(if (user?.role?.contains("CLIENT") == true) "Designer & Safe Escrow Client" else "Creative Studio", color = Color(0xFF49454F), fontSize = 12.sp)
                    }
                }

                // Escrow balance pill
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { onOpenEscrowHub() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF146C2E), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            String.format("$%.2f", user?.escrowWalletBalance ?: 0.0),
                            color = Color(0xFF146C2E),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // PRO STATUS BANNER / UPGRADE CARD
        item {
            val isPro = user?.isPro == true
            val vipFree = user?.vipFreeClaimed == true

            if (isPro) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(Color(0xFF6750A4), Color(0xFF9A82DB))))
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("PRO ACCESS UNLOCKED", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    if (vipFree) "Unlocked via VIP Creator Bypass ($0.00 Free Forever). Unlimited 4K Studio & 0% Escrow Fee!"
                                    else "All Pro Studio design assets unlocked.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProUpgrade() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(Color(0xFF6750A4), Color(0xFF9A82DB))))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pro Access Unlocked", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Claim your limited-time free Pro upgrade for design assets.", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onOpenProUpgrade,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("ACTIVATE NOW", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // NAVIGATION SWITCHER BUTTON (Safe Escrow Client Portal)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE7E0EC), RoundedCornerShape(24.dp))
                    .clickable { onOpenEscrowHub() },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8DEF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Safe Escrow Hire Portal", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.background(Color(0xFFE8DEF8), RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text("ACTIVE", color = Color(0xFF6750A4), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Funds are held safely until delivery & milestone completion.", color = Color(0xFF49454F), fontSize = 12.sp)
                    }
                }
            }
        }

        // QUICK CREATE BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Start Creating", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Button(
                    onClick = { showNewProjDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Canvas", color = Color.White)
                }
            }
        }

        // CATEGORY PILLS
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    "Social Poster" to Color(0xFFEADDFF),
                    "Logo Design" to Color(0xFFE8DEF8),
                    "Escrow Banner" to Color(0xFFF3EDF7),
                    "Presentation" to Color(0xFFEADDFF)
                ).forEach { (cat, bgColor) ->
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .border(1.dp, Color(0xFFE7E0EC), RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.createNewProject("New $cat", cat, "#FFFFFF")
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF21005D), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(cat, color = Color(0xFF1D1B20), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // FEATURED TEMPLATES
        item {
            Text("Featured Studio Templates", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    TemplateCard(
                        title = "Neon Cyber Festival",
                        category = "Social Poster",
                        isPro = true,
                        imageRes = R.drawable.img_template_social_1783007666455,
                        onClick = {
                            val p = projects.find { it.title.contains("Cyber") }
                            if (p != null) {
                                viewModel.loadProject(p.id)
                                onOpenCanvas(p.id)
                            } else {
                                viewModel.createNewProject("Neon Cyber Festival", "Social Poster", "#160C28")
                            }
                        }
                    )
                }
                item {
                    TemplateCard(
                        title = "Apex Brand Logo",
                        category = "Logo Design",
                        isPro = false,
                        imageRes = R.drawable.img_app_logo_1783007647969,
                        onClick = {
                            val p = projects.find { it.title.contains("Apex") }
                            if (p != null) {
                                viewModel.loadProject(p.id)
                                onOpenCanvas(p.id)
                            } else {
                                viewModel.createNewProject("Apex Brand Logo", "Logo Design", "#0F172A")
                            }
                        }
                    )
                }
            }
        }

        // RECENT DESIGNS LIST
        item {
            Text("Recent Canvas Projects", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        items(projects) { proj ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE7E0EC), RoundedCornerShape(20.dp))
                    .clickable {
                        viewModel.loadProject(proj.id)
                        onOpenCanvas(proj.id)
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFF3EDF7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(proj.title, color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${proj.category} • ${proj.width}x${proj.height}", color = Color(0xFF49454F), fontSize = 12.sp)
                                if (proj.isProOnly) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("PRO", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Row {
                        IconButton(onClick = {
                            viewModel.loadProject(proj.id)
                            onOpenCanvas(proj.id)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF6750A4))
                        }
                        IconButton(onClick = { viewModel.deleteProject(proj.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFBA1A1A))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showNewProjDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Social Poster") }

        AlertDialog(
            onDismissRequest = { showNewProjDialog = false },
            title = { Text("Start New Canvas Design", color = Color(0xFF1D1B20)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Project Title") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val t = if (title.isBlank()) "My Design Project" else title
                        viewModel.createNewProject(t, category, "#FFFFFF")
                        showNewProjDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                ) {
                    Text("Create & Open", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjDialog = false }) {
                    Text("Cancel", color = Color(0xFF49454F))
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun TemplateCard(
    title: String,
    category: String,
    isPro: Boolean,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .border(1.dp, Color(0xFFE7E0EC), RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(130.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (isPro) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color(0xFF6750A4), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PRO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(category, color = Color(0xFF49454F), fontSize = 11.sp)
            }
        }
    }
}
