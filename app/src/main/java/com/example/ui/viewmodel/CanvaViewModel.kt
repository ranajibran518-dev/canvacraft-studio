package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DesignElement
import com.example.data.model.DesignProject
import com.example.data.model.EscrowContract
import com.example.data.model.EscrowMessage
import com.example.data.model.UserProfile
import com.example.data.repository.CanvaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CanvaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CanvaRepository
    init {
        val dao = AppDatabase.getDatabase(application).canvaDao()
        repository = CanvaRepository(dao)
        viewModelScope.launch {
            repository.initSeedIfNeeded()
        }
    }

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allProjects: StateFlow<List<DesignProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContracts: StateFlow<List<EscrowContract>> = repository.allContracts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeProject = MutableStateFlow<DesignProject?>(null)
    val activeProject: StateFlow<DesignProject?> = _activeProject.asStateFlow()

    private val _activeElements = MutableStateFlow<List<DesignElement>>(emptyList())
    val activeElements: StateFlow<List<DesignElement>> = _activeElements.asStateFlow()

    private val _selectedElementId = MutableStateFlow<String?>(null)
    val selectedElementId: StateFlow<String?> = _selectedElementId.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun clearToast() {
        _toastMessage.value = null
    }

    // --- PRO & VIP FREE BYPASS ---
    fun claimVipFreePro() {
        viewModelScope.launch {
            val current = userProfile.firstOrNull() ?: return@launch
            val updated = current.copy(isPro = true, vipFreeClaimed = true)
            repository.saveUserProfile(updated)
            _toastMessage.value = "🎉 VIP Override Applied! CanvaCraft PRO Unlocked Completely FREE!"
        }
    }

    fun toggleAccountRole(role: String) {
        viewModelScope.launch {
            val current = userProfile.firstOrNull() ?: return@launch
            repository.saveUserProfile(current.copy(role = role))
            _toastMessage.value = "Switched persona view to: $role"
        }
    }

    // --- CANVAS STUDIO OPERATIONS ---
    fun createNewProject(title: String, category: String, bgColorHex: String) {
        viewModelScope.launch {
            val newId = "proj_" + UUID.randomUUID().toString().take(6)
            val initialElements = listOf(
                DesignElement(
                    id = UUID.randomUUID().toString().take(6),
                    type = "TEXT",
                    content = title.uppercase(),
                    x = 80f,
                    y = 150f,
                    width = 320f,
                    height = 60f,
                    colorHex = "#FFFFFF",
                    fontSizeSp = 26
                )
            )
            val proj = DesignProject(
                id = newId,
                title = title,
                category = category,
                bgColorHex = bgColorHex,
                elementsJson = repository.elementsToJson(initialElements),
                isProOnly = false
            )
            repository.saveProject(proj)
            loadProject(newId)
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val proj = repository.getProjectById(projectId)
            if (proj != null) {
                _activeProject.value = proj
                _activeElements.value = repository.jsonToElements(proj.elementsJson)
                _selectedElementId.value = _activeElements.value.firstOrNull()?.id
            }
        }
    }

    fun selectElement(elementId: String?) {
        _selectedElementId.value = elementId
    }

    fun addElement(type: String, content: String, colorHex: String, fontSize: Int = 22) {
        val current = _activeElements.value
        val newEl = DesignElement(
            id = "el_" + UUID.randomUUID().toString().take(6),
            type = type,
            content = content,
            x = 80f + (current.size * 20f),
            y = 120f + (current.size * 30f),
            width = if (type == "ICON") 100f else 280f,
            height = if (type == "ICON") 100f else 60f,
            colorHex = colorHex,
            fontSizeSp = fontSize
        )
        val newList = current + newEl
        _activeElements.value = newList
        _selectedElementId.value = newEl.id
        saveActiveCanvas()
    }

    fun removeSelectedElement() {
        val selId = _selectedElementId.value ?: return
        val newList = _activeElements.value.filter { it.id != selId }
        _activeElements.value = newList
        _selectedElementId.value = newList.lastOrNull()?.id
        saveActiveCanvas()
    }

    fun moveSelectedElement(dx: Float, dy: Float) {
        val selId = _selectedElementId.value ?: return
        val newList = _activeElements.value.map { el ->
            if (el.id == selId) {
                el.copy(x = el.x + dx, y = el.y + dy)
            } else el
        }
        _activeElements.value = newList
        saveActiveCanvas()
    }

    fun changeCanvasBgColor(hex: String) {
        val proj = _activeProject.value ?: return
        val updated = proj.copy(bgColorHex = hex, lastModified = System.currentTimeMillis())
        _activeProject.value = updated
        viewModelScope.launch { repository.saveProject(updated) }
    }

    private fun saveActiveCanvas() {
        val proj = _activeProject.value ?: return
        val updatedJson = repository.elementsToJson(_activeElements.value)
        val updated = proj.copy(
            elementsJson = updatedJson,
            lastModified = System.currentTimeMillis()
        )
        _activeProject.value = updated
        viewModelScope.launch {
            repository.saveProject(updated)
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            _toastMessage.value = "Project deleted."
        }
    }

    // --- ESCROW MARKETPLACE OPERATIONS ---
    fun createNewEscrowGig(clientName: String, title: String, desc: String, amountUsd: Double) {
        viewModelScope.launch {
            val newId = "escrow_" + UUID.randomUUID().toString().take(5)
            val contract = EscrowContract(
                id = newId,
                clientName = clientName,
                designerName = "Alex Rivera (You)",
                projectTitle = title,
                description = desc,
                amountUsd = amountUsd,
                status = "IN_ESCROW"
            )
            repository.saveContract(contract)
            repository.sendMessage(newId, "System Vault 🛡️", "$$amountUsd deposited securely into CanvaCraft Escrow Vault.", true)
            _toastMessage.value = "🛡️ $$amountUsd locked safely in Escrow for project: $title"
        }
    }

    fun submitProjectToEscrow(contractId: String, projectId: String, note: String) {
        viewModelScope.launch {
            val all = allContracts.firstOrNull() ?: return@launch
            val target = all.find { it.id == contractId } ?: return@launch
            val updated = target.copy(status = "REVIEWING_DELIVERY", linkedDesignId = projectId, deliveryMessage = note)
            repository.saveContract(updated)
            repository.sendMessage(contractId, "Alex Rivera", note)
            repository.sendMessage(contractId, "System Vault 🛡️", "Designer submitted canvas project for review. Funds remaining safely in Escrow until client approval.", true)
            _toastMessage.value = "Project delivered to client for review!"
        }
    }

    fun approveAndReleaseFunds(contractId: String) {
        viewModelScope.launch {
            val all = allContracts.firstOrNull() ?: return@launch
            val target = all.find { it.id == contractId } ?: return@launch
            val updated = target.copy(status = "RELEASED")
            repository.saveContract(updated)
            
            // Add funds to designer wallet
            val user = userProfile.firstOrNull()
            if (user != null) {
                repository.saveUserProfile(user.copy(escrowWalletBalance = user.escrowWalletBalance + target.amountUsd))
            }

            repository.sendMessage(contractId, "System Vault 🛡️", "🎉 Client Approved Delivery! $${target.amountUsd} released from Safe Escrow to Designer Wallet.", true)
            _toastMessage.value = "🎉 Escrow Released! $${target.amountUsd} added to Wallet!"
        }
    }
}
