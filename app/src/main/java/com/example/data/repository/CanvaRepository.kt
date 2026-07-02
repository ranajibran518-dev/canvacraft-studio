package com.example.data.repository

import com.example.data.db.CanvaDao
import com.example.data.model.DesignElement
import com.example.data.model.DesignProject
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowMessage
import com.example.data.model.UserProfile
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class CanvaRepository(private val dao: CanvaDao) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, DesignElement::class.java)
    private val elementAdapter = moshi.adapter<List<DesignElement>>(listType)

    fun elementsToJson(elements: List<DesignElement>): String {
        return try {
            elementAdapter.toJson(elements)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun jsonToElements(json: String): List<DesignElement> {
        return try {
            elementAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allProjects: Flow<List<DesignProject>> = dao.getAllProjects()
    val allContracts: Flow<List<EscrowContract>> = dao.getAllContracts()

    fun getMessages(contractId: String): Flow<List<EscrowMessage>> = dao.getMessagesForContract(contractId)

    suspend fun initSeedIfNeeded() {
        val currentUser = dao.getUserProfile().firstOrNull()
        if (currentUser == null) {
            // Seed Profile
            val defaultProfile = UserProfile(
                id = "user_me",
                name = "Alex Rivera",
                email = "alex@canvacraft.design",
                isPro = false,
                vipFreeClaimed = false,
                role = "DESIGNER_AND_CLIENT",
                escrowWalletBalance = 1450.00
            )
            dao.saveUserProfile(defaultProfile)

            // Seed Sample Projects
            val posterElements = listOf(
                DesignElement(id = "1", type = "TEXT", content = "CANVACRAFT PRO", x = 80f, y = 140f, width = 360f, height = 80f, colorHex = "#FF007F", fontSizeSp = 32),
                DesignElement(id = "2", type = "TEXT", content = "FUTURE OF CREATIVE DESIGN", x = 80f, y = 230f, width = 340f, height = 60f, colorHex = "#00FFFF", fontSizeSp = 18),
                DesignElement(id = "3", type = "SHAPE", content = "RECTANGLE", x = 80f, y = 320f, width = 320f, height = 180f, colorHex = "#3B1A6E", fontSizeSp = 14),
                DesignElement(id = "4", type = "ICON", content = "✦ VIP PRO TEMPLATE ✦", x = 110f, y = 390f, width = 260f, height = 50f, colorHex = "#FFD700", fontSizeSp = 20)
            )
            dao.saveProject(
                DesignProject(
                    id = "proj_1",
                    title = "Neon Cyber Festival Poster",
                    category = "Social Poster",
                    bgColorHex = "#160C28",
                    elementsJson = elementsToJson(posterElements),
                    isProOnly = true,
                    previewResName = "img_template_social"
                )
            )

            val logoElements = listOf(
                DesignElement(id = "l1", type = "ICON", content = "🎨", x = 180f, y = 160f, width = 140f, height = 140f, colorHex = "#FFD700", fontSizeSp = 64),
                DesignElement(id = "l2", type = "TEXT", content = "APEX STUDIO", x = 120f, y = 320f, width = 260f, height = 70f, colorHex = "#FFFFFF", fontSizeSp = 28),
                DesignElement(id = "l3", type = "TEXT", content = "BRAND IDENTITY KIT", x = 140f, y = 390f, width = 220f, height = 40f, colorHex = "#00FFCC", fontSizeSp = 14)
            )
            dao.saveProject(
                DesignProject(
                    id = "proj_2",
                    title = "Apex Studio Brand Logo",
                    category = "Logo Design",
                    bgColorHex = "#0F172A",
                    elementsJson = elementsToJson(logoElements),
                    isProOnly = false,
                    previewResName = "img_app_logo"
                )
            )

            // Seed Escrow Contracts
            val contract1 = EscrowContract(
                id = "escrow_101",
                clientName = "Marcus Vance (CEO @ TechPulse)",
                clientAvatar = "🧑‍💻",
                designerName = "Alex Rivera (You)",
                projectTitle = "Brand Identity & 3D Vector Logo",
                description = "Need a futuristic tech logo with glowing violet and cyan gradients for our AI launch.",
                amountUsd = 450.00,
                status = "IN_ESCROW",
                linkedDesignId = "proj_2"
            )
            dao.saveContract(contract1)
            dao.insertMessage(EscrowMessage(contractId = "escrow_101", senderName = "System Vault 🛡️", isSystemNotice = true, messageText = "$450.00 deposited into CanvaCraft Safe Escrow. Funds locked securely until delivery."))
            dao.insertMessage(EscrowMessage(contractId = "escrow_101", senderName = "Marcus Vance", messageText = "Hi Alex! Excited to see the initial canvas concepts."))

            val contract2 = EscrowContract(
                id = "escrow_102",
                clientName = "Elena Rostova (Fashion Brand)",
                clientAvatar = "👩‍🎨",
                designerName = "Alex Rivera (You)",
                projectTitle = "Instagram Summer Sale Promo Kit",
                description = "Bold social media banners with neon coral & purple aesthetics.",
                amountUsd = 280.00,
                status = "REVIEWING_DELIVERY",
                linkedDesignId = "proj_1",
                deliveryMessage = "Hi Elena! Here is the completed Neon Cyber Festival poster design."
            )
            dao.saveContract(contract2)
            dao.insertMessage(EscrowMessage(contractId = "escrow_102", senderName = "System Vault 🛡️", isSystemNotice = true, messageText = "$280.00 locked in Escrow. Designer submitted final design for approval."))

            val contract3 = EscrowContract(
                id = "escrow_103",
                clientName = "David K. (Fintech App)",
                clientAvatar = "👨‍💼",
                designerName = "Alex Rivera (You)",
                projectTitle = "Mobile Escrow Shield Illustration",
                description = "Custom vector graphic illustrating safe transactions.",
                amountUsd = 520.00,
                status = "RELEASED",
                linkedDesignId = null
            )
            dao.saveContract(contract3)
            dao.insertMessage(EscrowMessage(contractId = "escrow_103", senderName = "System Vault 🛡️", isSystemNotice = true, messageText = "$520.00 successfully released from Escrow to Alex Rivera's wallet! Client approved project."))
        }
    }

    suspend fun saveUserProfile(user: UserProfile) = dao.saveUserProfile(user)
    suspend fun saveProject(project: DesignProject) = dao.saveProject(project)
    suspend fun getProjectById(id: String) = dao.getProjectById(id)
    suspend fun deleteProject(id: String) = dao.deleteProject(id)

    suspend fun saveContract(contract: EscrowContract) = dao.saveContract(contract)
    suspend fun sendMessage(contractId: String, sender: String, text: String, isSystem: Boolean = false) {
        dao.insertMessage(
            EscrowMessage(
                contractId = contractId,
                senderName = sender,
                messageText = text,
                isSystemNotice = isSystem
            )
        )
    }
}
