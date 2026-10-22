package com.example.data.repository

import com.example.data.local.PrintOrderDao
import com.example.data.local.ServiceRequestDao
import com.example.data.local.ShopConfigDao
import com.example.data.model.PrintOrderEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.ShopConfigEntity
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class CscRepository(
    private val requestDao: ServiceRequestDao,
    private val printDao: PrintOrderDao,
    private val configDao: ShopConfigDao
) {
    val allRequests: Flow<List<ServiceRequestEntity>> = requestDao.getAllRequests()
    val allPrintOrders: Flow<List<PrintOrderEntity>> = printDao.getAllOrders()
    val shopConfig: Flow<ShopConfigEntity> = configDao.getShopConfig()

    suspend fun getRequestByTracking(trackingNumber: String): ServiceRequestEntity? {
        return requestDao.getByTrackingNumber(trackingNumber.trim())
    }

    fun searchRequestsByPhone(phone: String): Flow<List<ServiceRequestEntity>> {
        return requestDao.searchByPhone(phone.trim())
    }

    suspend fun submitServiceRequest(
        customerName: String,
        customerPhone: String,
        villageAddress: String,
        serviceId: String,
        serviceName: String,
        customerNotes: String,
        feeInr: Int
    ): String {
        val tracking = "SK-" + (1000 + Random.nextInt(9000)) + "-" + (100 + Random.nextInt(900))
        val entity = ServiceRequestEntity(
            trackingNumber = tracking,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            villageAddress = villageAddress.trim(),
            serviceId = serviceId,
            serviceName = serviceName,
            status = "Pending",
            customerNotes = customerNotes.trim(),
            feeInr = feeInr,
            isPaid = false
        )
        requestDao.insertRequest(entity)
        return tracking
    }

    suspend fun updateRequestStatus(id: Long, newStatus: String) {
        requestDao.updateStatus(id, newStatus)
    }

    suspend fun updateRequest(request: ServiceRequestEntity) {
        requestDao.updateRequest(request)
    }

    suspend fun deleteRequest(id: Long) {
        requestDao.deleteRequest(id)
    }

    suspend fun submitPrintOrder(
        customerName: String,
        customerPhone: String,
        documentTitle: String,
        printType: String,
        paperSize: String,
        copies: Int,
        calculatedPrice: Int,
        imagePath: String
    ): String {
        val orderNum = "ORD-" + (1000 + Random.nextInt(9000))
        val entity = PrintOrderEntity(
            orderNumber = orderNum,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            documentTitle = documentTitle.trim(),
            printType = printType,
            paperSize = paperSize,
            copies = copies,
            calculatedPrice = calculatedPrice,
            status = "Queued",
            imagePath = imagePath
        )
        printDao.insertOrder(entity)
        return orderNum
    }

    suspend fun updateOrderStatus(id: Long, newStatus: String) {
        printDao.updateStatus(id, newStatus)
    }

    suspend fun deleteOrder(id: Long) {
        printDao.deleteOrder(id)
    }

    suspend fun saveShopConfig(config: ShopConfigEntity) {
        configDao.saveShopConfig(config)
    }

    suspend fun getShopConfigOnce(): ShopConfigEntity {
        return configDao.getShopConfigOnce() ?: ShopConfigEntity()
    }
}
