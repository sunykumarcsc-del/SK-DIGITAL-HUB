package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.PrintOrderEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.ShopConfigEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ServiceRequestEntity::class,
        PrintOrderEntity::class,
        ShopConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceRequestDao(): ServiceRequestDao
    abstract fun printOrderDao(): PrintOrderDao
    abstract fun shopConfigDao(): ShopConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sk_csc_database.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial shop config and demo records
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.shopConfigDao().saveShopConfig(ShopConfigEntity())
                            // Seed a sample request so user immediately sees tracking in action
                            database.serviceRequestDao().insertRequest(
                                ServiceRequestEntity(
                                    trackingNumber = "SK-2026-8942",
                                    customerName = "Ramesh Chandra",
                                    customerPhone = "9876543210",
                                    villageAddress = "Kataiyapur, Etawah",
                                    serviceId = "pan_card",
                                    serviceName = "PAN Card (New / Correction)",
                                    status = "Ready",
                                    customerNotes = "New PAN Card application with Aadhaar",
                                    adminNotes = "PAN Generated. Physical card dispatched.",
                                    feeInr = 150,
                                    isPaid = true
                                )
                            )
                            database.printOrderDao().insertOrder(
                                PrintOrderEntity(
                                    orderNumber = "ORD-7701",
                                    customerName = "Pooja Sharma",
                                    customerPhone = "9528683541",
                                    documentTitle = "College Admission Form",
                                    printType = "Color",
                                    paperSize = "A4",
                                    copies = 2,
                                    calculatedPrice = 20,
                                    status = "Ready for Pickup",
                                    isPaid = true
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
