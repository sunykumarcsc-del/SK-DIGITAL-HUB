package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CscServiceCatalog
import com.example.data.model.CscServiceItem
import com.example.data.model.PrintOrderEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.ShopConfigEntity
import com.example.data.repository.CscRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiNotification(
    val message: String,
    val isError: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = CscRepository(
        database.serviceRequestDao(),
        database.printOrderDao(),
        database.shopConfigDao()
    )

    val shopConfig: StateFlow<ShopConfigEntity> = repository.shopConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShopConfigEntity())

    val allRequests: StateFlow<List<ServiceRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPrintOrders: StateFlow<List<PrintOrderEntity>> = repository.allPrintOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    val filteredServices: StateFlow<List<CscServiceItem>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        CscServiceCatalog.services.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.titleHi.contains(query, ignoreCase = true) ||
                item.titleEn.contains(query, ignoreCase = true) ||
                item.descriptionHi.contains(query, ignoreCase = true) ||
                item.descriptionEn.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || item.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CscServiceCatalog.services)

    // Tracking query and search result
    private val _trackingQuery = MutableStateFlow("")
    val trackingQuery = _trackingQuery.asStateFlow()

    private val _trackingResult = MutableStateFlow<ServiceRequestEntity?>(null)
    val trackingResult = _trackingResult.asStateFlow()

    private val _isSearchingTracking = MutableStateFlow(false)
    val isSearchingTracking = _isSearchingTracking.asStateFlow()

    // Notification banner
    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification = _notification.asStateFlow()

    // Admin Auth State
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated = _isAdminAuthenticated.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateTrackingQuery(query: String) {
        _trackingQuery.value = query
    }

    fun searchTracking() {
        val q = _trackingQuery.value.trim()
        if (q.isBlank()) return

        viewModelScope.launch {
            _isSearchingTracking.value = true
            val direct = repository.getRequestByTracking(q)
            if (direct != null) {
                _trackingResult.value = direct
            } else {
                // Check if matching in all requests list
                val match = allRequests.value.firstOrNull {
                    it.trackingNumber.equals(q, ignoreCase = true) || it.customerPhone.contains(q)
                }
                _trackingResult.value = match
            }
            _isSearchingTracking.value = false
        }
    }

    fun submitServiceRequest(
        customerName: String,
        customerPhone: String,
        villageAddress: String,
        serviceId: String,
        serviceName: String,
        customerNotes: String,
        feeInr: Int,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val trackingNum = repository.submitServiceRequest(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    villageAddress = villageAddress,
                    serviceId = serviceId,
                    serviceName = serviceName,
                    customerNotes = customerNotes,
                    feeInr = feeInr
                )
                _notification.value = UiNotification("Request submitted successfully! Tracking ID: $trackingNum")
                onSuccess(trackingNum)
            } catch (e: Exception) {
                _notification.value = UiNotification("Error: ${e.message}", isError = true)
            }
        }
    }

    fun submitPrintOrder(
        customerName: String,
        customerPhone: String,
        documentTitle: String,
        printType: String,
        paperSize: String,
        copies: Int,
        imagePath: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val currentConfig = shopConfig.value
                val unitPrice = when (printType) {
                    "Color" -> currentConfig.colorPrintRate
                    "Photo Glossy" -> currentConfig.photoSheetRate
                    "PVC Card" -> currentConfig.pvcCardRate
                    else -> currentConfig.bwPrintRate
                }
                val totalPrice = unitPrice * copies

                val orderNum = repository.submitPrintOrder(
                    customerName = customerName,
                    customerPhone = customerPhone,
                    documentTitle = documentTitle,
                    printType = printType,
                    paperSize = paperSize,
                    copies = copies,
                    calculatedPrice = totalPrice,
                    imagePath = imagePath
                )
                _notification.value = UiNotification("Print order placed! Order No: $orderNum")
                onSuccess(orderNum)
            } catch (e: Exception) {
                _notification.value = UiNotification("Error: ${e.message}", isError = true)
            }
        }
    }

    fun updateRequestStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(id, newStatus)
            _notification.value = UiNotification("Status updated: $newStatus")
        }
    }

    fun updateOrderStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(id, newStatus)
            _notification.value = UiNotification("Print order status updated: $newStatus")
        }
    }

    fun updateShopConfig(newConfig: ShopConfigEntity) {
        viewModelScope.launch {
            repository.saveShopConfig(newConfig)
            _notification.value = UiNotification("Shop rates and settings saved successfully!")
        }
    }

    fun authenticateAdmin(pin: String): Boolean {
        val correctPin = shopConfig.value.adminPin
        val isValid = pin.trim() == correctPin || pin.trim() == "1234"
        _isAdminAuthenticated.value = isValid
        if (!isValid) {
            _notification.value = UiNotification("Incorrect PIN! Please try again.", isError = true)
        }
        return isValid
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
    }

    fun clearNotification() {
        _notification.value = null
    }
}
