package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserProfile(
    @PrimaryKey val id: String = "user_me",
    val name: String = "Alex Rivera",
    val email: String = "alex@canvacraft.design",
    val avatarRes: String = "avatar_creator",
    val isPro: Boolean = false,
    val vipFreeClaimed: Boolean = false,
    val role: String = "DESIGNER_AND_CLIENT", // DESIGNER, CLIENT, BOTH
    val escrowWalletBalance: Double = 1250.00
)

@Entity(tableName = "design_projects")
data class DesignProject(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "Social Poster", "Logo Design", "Presentation", "Escrow Gig"
    val width: Int = 1080,
    val height: Int = 1080,
    val bgColorHex: String = "#1E1E2E",
    val elementsJson: String = "[]", // JSON string of DesignElement
    val isProOnly: Boolean = false,
    val previewResName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

data class DesignElement(
    val id: String,
    val type: String, // "TEXT", "SHAPE", "ICON", "BANNER"
    val content: String,
    val x: Float = 100f,
    val y: Float = 100f,
    val width: Float = 250f,
    val height: Float = 120f,
    val colorHex: String = "#FFFFFF",
    val fontSizeSp: Int = 24,
    val zIndex: Int = 1
)

@Entity(tableName = "escrow_contracts")
data class EscrowContract(
    @PrimaryKey val id: String,
    val clientName: String,
    val clientAvatar: String = "🧑‍💼",
    val designerName: String,
    val projectTitle: String,
    val description: String,
    val amountUsd: Double,
    val status: String = "IN_ESCROW", // "PENDING_DEPOSIT", "IN_ESCROW", "REVIEWING_DELIVERY", "RELEASED", "REFUNDED"
    val createdAt: Long = System.currentTimeMillis(),
    val linkedDesignId: String? = null,
    val deliveryMessage: String? = null
)

@Entity(tableName = "chat_messages")
data class EscrowMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contractId: String,
    val senderName: String,
    val isSystemNotice: Boolean = false,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)
