package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceRequestEntity
import com.example.ui.components.dialPhone
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandSaffron
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrackRequestScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val trackingQuery by viewModel.trackingQuery.collectAsState()
    val trackingResult by viewModel.trackingResult.collectAsState()
    val isSearching by viewModel.isSearchingTracking.collectAsState()
    val shopConfig by viewModel.shopConfig.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = BrandGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Track Application Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BrandDarkNavy
                        )
                        Text(
                            text = "Enter Tracking ID or Mobile Number",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = trackingQuery,
                    onValueChange = { viewModel.updateTrackingQuery(it) },
                    placeholder = { Text("e.g. SK-2026-8942 or 9876543210") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BrandDarkNavy) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("track_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.searchTracking() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("track_search_button")
                ) {
                    Text("Search Status")
                }
            }
        }

        // Search Result Card
        trackingResult?.let { result ->
            TrackingDetailCard(result, shopConfig.phone1, context)
        } ?: run {
            if (trackingQuery.isNotBlank() && !isSearching) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No records found for this ID or mobile number.",
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please verify your tracking number or contact operator Sunil Kumar.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingDetailCard(
    request: ServiceRequestEntity,
    phone: String,
    context: Context
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(request.createdAt))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tracking ID",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = request.trackingNumber,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = BrandDarkNavy
                    )
                }

                StatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Service: ${request.serviceName}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
            )
            Text(
                text = "Customer: ${request.customerName} | ${request.villageAddress}",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Text(
                text = "Date: $dateStr",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress Timeline
            Text(
                text = "Progress Timeline:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = BrandDarkNavy
            )
            Spacer(modifier = Modifier.height(8.dp))

            val currentStep = when (request.status) {
                "Pending" -> 1
                "Processing" -> 2
                "Ready" -> 3
                "Completed" -> 4
                else -> 1
            }

            TimelineStep(step = 1, title = "Application Submitted & Registered", isDone = currentStep >= 1)
            TimelineStep(step = 2, title = "Document Verification & Processing", isDone = currentStep >= 2)
            TimelineStep(step = 3, title = "Document / Certificate Ready", isDone = currentStep >= 3)
            TimelineStep(step = 4, title = "Delivered to Customer", isDone = currentStep >= 4, isLast = true)

            if (request.adminNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Operator Remark (SK CSC Notes):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFFF57F17)
                        )
                        Text(
                            text = request.adminNotes,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Contact Shop regarding this request
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { dialPhone(context, phone) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Us", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        openWhatsApp(
                            context,
                            phone,
                            "Hello Sunil ji! I would like to check the status of my application (ID: ${request.trackingNumber})."
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TimelineStep(
    step: Int,
    title: String,
    isDone: Boolean,
    isLast: Boolean = false
) {
    Row(verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isDone) BrandGreen else Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
                        .background(if (isDone) BrandGreen else Color.LightGray)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = if (isDone) TextPrimary else TextSecondary,
            fontWeight = if (isDone) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status) {
        "Pending" -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Pending")
        "Processing" -> Triple(Color(0xFFE3F2FD), Color(0xFF0277BD), "Processing")
        "Ready" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Ready")
        "Completed" -> Triple(Color(0xFFE8F5E9), Color(0xFF1B5E20), "Completed")
        else -> Triple(Color(0xFFECEFF1), Color(0xFF37474F), status)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
