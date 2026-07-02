package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ProUpgradeDialog(
    onDismiss: () -> Unit,
    onClaimVipFree: () -> Unit
) {
    var selectedTier by remember { mutableStateOf("YEARLY") }
    var vipCodeInput by remember { mutableStateOf("") }
    var showSecretInput by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8DEF8), RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CANVACRAFT PRO", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF49454F))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Unlock Limitless Studio Power",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1D1B20),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Normal users purchase Pro subscriptions. But as a VIP Creator, you can bypass the paywall completely for FREE!",
                    fontSize = 13.sp,
                    color = Color(0xFF49454F),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Benefit Items
                ProBenefitRow("✨ Unlimited Premium Templates & Vector Icons")
                ProBenefitRow("🚀 4K Ultra-HD Crisp Export & Transparent BG")
                ProBenefitRow("🪄 AI Magic Layout & Smart Palette Assistant")
                ProBenefitRow("🛡️ 0% Commission Fee on Safe Escrow Client Contracts")

                Spacer(modifier = Modifier.height(20.dp))

                // Standard Tiers (visual representation of normal pricing)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TierCard(
                        title = "Monthly",
                        price = "$9.99/mo",
                        isSelected = selectedTier == "MONTHLY",
                        onClick = { selectedTier = "MONTHLY" },
                        modifier = Modifier.weight(1f)
                    )
                    TierCard(
                        title = "Yearly (Save 34%)",
                        price = "$79.00/yr",
                        isSelected = selectedTier == "YEARLY",
                        onClick = { selectedTier = "YEARLY" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // THE SPECIAL VIP FREE BYPASS CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClaimVipFree()
                            onDismiss()
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(Color(0xFF6750A4), Color(0xFF9A82DB))))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("👑 VIP Creator Bypass", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Claim 100% Free Lifetime Pro Access", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    onClaimVipFree()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("FREE", color = Color(0xFF6750A4), fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Standard purchases simulate standard Play Billing. VIP Override unlocks all Pro studio tools immediately at zero cost.",
                    fontSize = 11.sp,
                    color = Color(0xFF49454F),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ProBenefitRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, color = Color(0xFF1D1B20), fontSize = 13.sp)
    }
}

@Composable
private fun TierCard(
    title: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF6750A4) else Color(0xFFE7E0EC)
    val bg = if (isSelected) Color(0xFFEADDFF) else Color(0xFFF7F2FA)

    Card(
        modifier = modifier
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color(0xFF49454F), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(price, color = Color(0xFF1D1B20), fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
