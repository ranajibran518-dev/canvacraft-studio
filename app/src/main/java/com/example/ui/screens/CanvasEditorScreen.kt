package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DesignElement
import com.example.data.model.DesignProject
import com.example.ui.viewmodel.CanvaViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasEditorScreen(
    viewModel: CanvaViewModel,
    onBack: () -> Unit
) {
    val project by viewModel.activeProject.collectAsState()
    val elements by viewModel.activeElements.collectAsState()
    val selectedId by viewModel.selectedElementId.collectAsState()
    val contracts by viewModel.allContracts.collectAsState()

    var showAddTextDialog by remember { mutableStateOf(false) }
    var showSendEscrowDialog by remember { mutableStateOf(false) }

    val currentProj = project
    if (currentProj == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFEF7FF)), contentAlignment = Alignment.Center) {
            Text("Loading canvas...", color = Color(0xFF1D1B20))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(currentProj.title, color = Color(0xFF1D1B20), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8DEF8), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(currentProj.category, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1D1B20))
                    }
                },
                actions = {
                    Button(
                        onClick = { showSendEscrowDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Deliver to Escrow", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            CanvasBottomToolbar(
                selectedElement = elements.find { it.id == selectedId },
                onAddText = { showAddTextDialog = true },
                onAddShape = { type, color -> viewModel.addElement("SHAPE", type, color) },
                onAddSticker = { iconStr -> viewModel.addElement("ICON", iconStr, "#FFD700", 48) },
                onChangeBg = { hex -> viewModel.changeCanvasBgColor(hex) },
                onMove = { dx, dy -> viewModel.moveSelectedElement(dx, dy) },
                onDelete = { viewModel.removeSelectedElement() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4EFF4)),
            contentAlignment = Alignment.Center
        ) {
            // The Canvas Frame (Simulated 1:1 or 4:3 Design Workspace)
            val bgColor = parseHexColor(currentProj.bgColorHex)
            Box(
                modifier = Modifier
                    .width(360.dp)
                    .height(440.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(2.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
            ) {
                // Render all elements
                elements.forEach { el ->
                    val isSel = el.id == selectedId
                    val borderMod = if (isSel) Modifier.border(2.dp, Color(0xFF6750A4), RoundedCornerShape(4.dp)) else Modifier

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(el.x.roundToInt(), el.y.roundToInt()) }
                            .width(el.width.dp)
                            .height(el.height.dp)
                            .then(borderMod)
                            .clickable { viewModel.selectElement(el.id) }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (el.type) {
                            "TEXT" -> {
                                Text(
                                    text = el.content,
                                    color = parseHexColor(el.colorHex),
                                    fontSize = el.fontSizeSp.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            "SHAPE" -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(parseHexColor(el.colorHex), if (el.content == "CIRCLE") CircleShape else RoundedCornerShape(8.dp))
                                )
                            }
                            "ICON" -> {
                                Text(
                                    text = el.content,
                                    fontSize = el.fontSizeSp.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddTextDialog) {
        var textInput by remember { mutableStateOf("") }
        var colorHex by remember { mutableStateOf("#FFFFFF") }

        AlertDialog(
            onDismissRequest = { showAddTextDialog = false },
            title = { Text("Add Text Layer", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Text content") }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Color:", color = Color(0xFF49454F), fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        listOf("#1D1B20", "#6750A4", "#B58392", "#FFFFFF").forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .clickable { colorHex = hex }
                                    .border(if (colorHex == hex) 2.dp else 1.dp, Color(0xFF6750A4), CircleShape)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.addElement("TEXT", textInput, colorHex)
                            showAddTextDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                ) {
                    Text("Add", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTextDialog = false }) {
                    Text("Cancel", color = Color(0xFF6750A4))
                }
            },
            containerColor = Color.White
        )
    }

    if (showSendEscrowDialog) {
        var selectedContractId by remember { mutableStateOf(contracts.firstOrNull()?.id ?: "") }
        var deliveryNote by remember { mutableStateOf("Completed CanvaCraft design. Ready for client review!") }

        AlertDialog(
            onDismissRequest = { showSendEscrowDialog = false },
            title = { Text("Deliver Canvas to Safe Escrow", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select Escrow Contract:", color = Color(0xFF49454F), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    contracts.forEach { c ->
                        val isSel = c.id == selectedContractId
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedContractId = c.id },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0xFFEADDFF) else Color(0xFFF7F2FA)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color(0xFF6750A4) else Color(0xFFE7E0EC))
                        ) {
                            Text("${c.projectTitle} (${c.clientName}) - $${c.amountUsd}", color = Color(0xFF1D1B20), fontSize = 13.sp, modifier = Modifier.padding(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = deliveryNote,
                        onValueChange = { deliveryNote = it },
                        label = { Text("Delivery Note to Client") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedContractId.isNotEmpty()) {
                            viewModel.submitProjectToEscrow(selectedContractId, currentProj.id, deliveryNote)
                            showSendEscrowDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))
                ) {
                    Text("Submit to Client ($0% VIP Fee)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendEscrowDialog = false }) {
                    Text("Cancel", color = Color(0xFF6750A4))
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun CanvasBottomToolbar(
    selectedElement: DesignElement?,
    onAddText: () -> Unit,
    onAddShape: (String, String) -> Unit,
    onAddSticker: (String) -> Unit,
    onChangeBg: (String) -> Unit,
    onMove: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFE7E0EC))
            .padding(12.dp)
    ) {
        if (selectedElement != null) {
            // Inspector Row for selected element
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F2FA), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Selected: ${selectedElement.type}", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onMove(-15f, 0f) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color(0xFF1D1B20))
                    }
                    IconButton(onClick = { onMove(15f, 0f) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF1D1B20))
                    }
                    IconButton(onClick = { onMove(0f, -15f) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF1D1B20))
                    }
                    IconButton(onClick = { onMove(0f, 15f) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF1D1B20))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFBA1A1A))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = onAddText, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4))) {
                Icon(Icons.Default.TextFields, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Text", color = Color.White)
            }

            Button(onClick = { onAddShape("RECTANGLE", "#6750A4") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEF8))) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Box", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
            }

            Button(onClick = { onAddShape("CIRCLE", "#B58392") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEF8))) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Circle", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
            }

            Button(onClick = { onAddSticker("👑") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEF8))) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Crown", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
            }

            Button(onClick = { onAddSticker("✨") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEF8))) {
                Text("✨ Sparkle", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
            }

            Button(onClick = { onChangeBg("#FEF7FF") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7F2FA))) {
                Icon(Icons.Default.ColorLens, contentDescription = null, tint = Color(0xFF1D1B20), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("BG Color", color = Color(0xFF1D1B20))
            }
        }
    }
}

fun parseHexColor(hexStr: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hexStr))
    } catch (e: Exception) {
        Color(0xFF1E1E2E)
    }
}
