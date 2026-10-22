package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_requests")
data class ServiceRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackingNumber: String,
    val customerName: String,
    val customerPhone: String,
    val villageAddress: String,
    val serviceId: String,
    val serviceName: String,
    val status: String = "Pending", // Pending, Processing, Ready, Completed, Rejected
    val customerNotes: String = "",
    val adminNotes: String = "",
    val feeInr: Int = 0,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "print_orders")
data class PrintOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val documentTitle: String,
    val printType: String = "B&W", // "B&W", "Color", "Photo Glossy"
    val paperSize: String = "A4", // "A4", "4x6 Photo", "PVC Card"
    val copies: Int = 1,
    val calculatedPrice: Int = 0,
    val status: String = "Queued", // Queued, Printing, Ready for Pickup, Delivered
    val imagePath: String = "",
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "shop_config")
data class ShopConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val shopName: String = "SK CSC CENTER",
    val ownerName: String = "Sunil Kumar",
    val phone1: String = "7906093113",
    val phone2: String = "9528683541",
    val fullAddress: String = "Kataiyapur, Post Beena Road, Etawah, Uttar Pradesh, India",
    val announcementText: String = "🔴 SK CSC Center, Kataiyapur Etawah — Aadhaar, PAN, Certificates, Photo & Printing Services Available | Contact: 7906093113",
    val bwPrintRate: Int = 3,
    val colorPrintRate: Int = 10,
    val photoSheetRate: Int = 30,
    val pvcCardRate: Int = 60,
    val adminPin: String = "1234"
)
