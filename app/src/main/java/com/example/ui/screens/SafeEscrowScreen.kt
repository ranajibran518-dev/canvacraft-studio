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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.EscrowContract
import com.example.ui.viewmodel.CanvaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeEscrowScreen(
    viewModel: CanvaViewModel,
    onNavigateToCanvas: (String) -> Unit
) {
    val contracts by viewModel.allContracts.collectAsState()
    val user by viewModel.userProfile.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Active Escrows, 1: Request & Hire, 2: Chat Hub
    var selectedContractForChat by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF7FF))
    ) {
        // Hero Escrow Guarantee Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, Color(0xFFE7E0EC), RoundedCornerShape(24.dp)),
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
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8DEF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("CanvaCraft Safe Escrow Vault", color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Clients easily deposit project funds. Money remains 100% locked & safe in Escrow until the client approves design delivery!",
                        color = Color(0xFF49454F),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFF3EDF7),
            contentColor = Color(0xFF1D1B20),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF6750A4),
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Active Escrows (${contracts.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Reach Out & Hire", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = {
                    selectedTab = 2
                    if (selectedContractForChat == null) {
                        selectedContractForChat = contracts.firstOrNull()?.id
                    }
                },
                text = { Text("Escrow Chat", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTab) {
            0 -> ActiveEscrowsList(
                contracts = contracts,
                onApproveRelease = { id -> viewModel.approveAndReleaseFunds(id) },
                onOpenChat = { id ->
                    selectedContractForChat = id
                    selectedTab = 2
                },
                onViewDesign = { designId ->
                    if (designId != null) onNavigateToCanvas(designId)
                }
            )
            1 -> ReachOutHireForm(
                onSubmitGig = { client, title, desc, amt ->
                    viewModel.createNewEscrowGig(client, title, desc, amt)
                    selectedTab = 0
                }
            )
            2 -> EscrowChatSection(
                contracts = contracts,
                selectedContractId = selectedContractForChat,
                onSelectContract = { selectedContractForChat = it }
            )
        }
    }
}

@Composable
private fun ActiveEscrowsList(
    contracts: List<EscrowContract>,
    onApproveRelease: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    onViewDesign: (String?) -> Unit
) {
    if (contracts.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active escrow contracts.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(contracts) { contract ->
            val statusBg = when (contract.status) {
                "IN_ESCROW" -> Color(0xFFEADDFF)
                "REVIEWING_DELIVERY" -> Color(0xFFE8DEF8)
                "RELEASED" -> Color(0xFFE6F4EA)
                else -> Color(0xFFF3EDF7)
            }
            val statusTextColor = when (contract.status) {
                "IN_ESCROW" -> Color(0xFF21005D)
                "REVIEWING_DELIVERY" -> Color(0xFF6750A4)
                "RELEASED" -> Color(0xFF146C2E)
                else -> Color(0xFF49454F)
            }
            val statusLabel = when (contract.status) {
                "IN_ESCROW" -> "🛡️ LOCKED IN ESCROW"
                "REVIEWING_DELIVERY" -> "🎨 DESIGN SUBMITTED - REVIEW"
                "RELEASED" -> "✅ FUNDS RELEASED TO DESIGNER"
                else -> contract.status
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE7E0EC), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(contract.clientAvatar, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(contract.clientName, color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Designer: ${contract.designerName}", color = Color(0xFF49454F), fontSize = 12.sp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(statusBg, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(statusLabel, color = statusTextColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(contract.projectTitle, color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(contract.description, color = Color(0xFF49454F), fontSize = 13.sp)

                    if (contract.deliveryMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF7F2FA), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text("Delivery Note: \"${contract.deliveryMessage}\"", color = Color(0xFF6750A4), fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF146C2E), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                String.format("$%.2f USD Safe Hold", contract.amountUsd),
                                color = Color(0xFF146C2E),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onOpenChat(contract.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8DEF8)),
                                shape = RoundedCornerShape(50)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat", color = Color(0xFF6750A4), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (contract.status != "RELEASED") {
                                Button(
                                    onClick = { onApproveRelease(contract.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve & Release", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReachOutHireForm(
    onSubmitGig: (String, String, String, Double) -> Unit
) {
    var clientName by remember { mutableStateOf("Sophia Liang (Brand Client)") }
    var projectTitle by remember { mutableStateOf("Custom Brand Kit & Social Pack") }
    var description by remember { mutableStateOf("Looking for high quality Instagram carousel banners and vector brand logo.") }
    var budget by remember { mutableStateOf("350.00") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "Reach Out to Designer & Deposit into Safe Escrow",
                color = Color(0xFF1D1B20),
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                "When clients deposit funds here, CanvaCraft holds 100% of the money in the secure escrow vault. The designer is guaranteed payment upon delivery, and the client is protected until they approve.",
                color = Color(0xFF49454F),
                fontSize = 13.sp
            )
        }

        item {
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Client Organization / Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1D1B20),
                    unfocusedTextColor = Color(0xFF1D1B20),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = projectTitle,
                onValueChange = { projectTitle = it },
                label = { Text("Gig Project Title") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1D1B20),
                    unfocusedTextColor = Color(0xFF1D1B20),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Detailed Requirements & Style") },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1D1B20),
                    unfocusedTextColor = Color(0xFF1D1B20),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Escrow Deposit Amount (USD $)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1D1B20),
                    unfocusedTextColor = Color(0xFF1D1B20),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val amt = budget.toDoubleOrNull() ?: 250.0
                    if (projectTitle.isNotBlank()) {
                        onSubmitGig(clientName, projectTitle, description, amt)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lock Funds in Escrow & Start Gig", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun EscrowChatSection(
    contracts: List<EscrowContract>,
    selectedContractId: String?,
    onSelectContract: (String) -> Unit
) {
    if (contracts.isEmpty() || selectedContractId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select an escrow contract to view dispute-protected messages.", color = Color.Gray)
        }
        return
    }

    val currentContract = contracts.find { it.id == selectedContractId } ?: contracts.first()

    Column(modifier = Modifier.fillMaxSize()) {
        // Contract selector bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3EDF7))
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            contracts.forEach { c ->
                val isSel = c.id == currentContract.id
                val bg = if (isSel) Color(0xFF6750A4) else Color(0xFFE8DEF8)
                val txtColor = if (isSel) Color.White else Color(0xFF1D1B20)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(bg)
                        .clickable { onSelectContract(c.id) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(c.projectTitle.take(18) + "...", color = txtColor, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        // Simulated chat stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7F2FA), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        "🔒 Safe Escrow Chat: All communication is encrypted and recorded to protect client funds and designer copyright.",
                        color = Color(0xFF6750A4),
                        fontSize = 12.sp
                    )
                }
            }

            item {
                ChatBubble("System Vault 🛡️", "Contract #${currentContract.id} initialized for $${currentContract.amountUsd} USD.", true)
            }
            item {
                ChatBubble(currentContract.clientName, "Hi! Looking forward to the CanvaCraft designs for this gig.", false)
            }
            item {
                ChatBubble(currentContract.designerName, "Thanks! I'm starting on the canvas layout now. I'll submit for escrow review shortly.", false)
            }
            if (currentContract.deliveryMessage != null) {
                item {
                    ChatBubble(currentContract.designerName, "Delivery Note: ${currentContract.deliveryMessage}", false)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(sender: String, message: String, isSystem: Boolean) {
    val align = if (isSystem) Alignment.CenterHorizontally else if (sender.contains("You")) Alignment.End else Alignment.Start
    val bg = if (isSystem) Color(0xFFEADDFF) else if (sender.contains("You")) Color(0xFF6750A4) else Color.White
    val textColor = if (isSystem) Color(0xFF21005D) else if (sender.contains("You")) Color.White else Color(0xFF1D1B20)
    val border = if (sender.contains("You") || isSystem) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE7E0EC))

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Text(sender, color = Color(0xFF49454F), fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bg),
            border = border,
            elevation = CardDefaults.cardElevation(if (sender.contains("You") || isSystem) 0.dp else 1.dp)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                Text(message, color = textColor, fontSize = 13.sp)
            }
        }
    }
}
