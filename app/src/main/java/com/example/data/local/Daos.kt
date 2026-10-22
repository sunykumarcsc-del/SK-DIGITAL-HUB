package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PrintOrderEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.ShopConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceRequestDao {
    @Query("SELECT * FROM service_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<ServiceRequestEntity>>

    @Query("SELECT * FROM service_requests WHERE trackingNumber = :trackingNumber LIMIT 1")
    suspend fun getByTrackingNumber(trackingNumber: String): ServiceRequestEntity?

    @Query("SELECT * FROM service_requests WHERE customerPhone LIKE '%' || :phone || '%' ORDER BY createdAt DESC")
    fun searchByPhone(phone: String): Flow<List<ServiceRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ServiceRequestEntity): Long

    @Update
    suspend fun updateRequest(request: ServiceRequestEntity)

    @Query("UPDATE service_requests SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String)

    @Query("DELETE FROM service_requests WHERE id = :id")
    suspend fun deleteRequest(id: Long)
}

@Dao
interface PrintOrderDao {
    @Query("SELECT * FROM print_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<PrintOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: PrintOrderEntity): Long

    @Update
    suspend fun updateOrder(order: PrintOrderEntity)

    @Query("UPDATE print_orders SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String)

    @Query("DELETE FROM print_orders WHERE id = :id")
    suspend fun deleteOrder(id: Long)
}

@Dao
interface ShopConfigDao {
    @Query("SELECT * FROM shop_config WHERE id = 1 LIMIT 1")
    fun getShopConfig(): Flow<ShopConfigEntity>

    @Query("SELECT * FROM shop_config WHERE id = 1 LIMIT 1")
    suspend fun getShopConfigOnce(): ShopConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShopConfig(config: ShopConfigEntity)
}
