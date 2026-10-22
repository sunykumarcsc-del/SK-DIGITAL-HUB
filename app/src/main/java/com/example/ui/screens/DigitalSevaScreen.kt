package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CscServiceItem
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandSaffron
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DigitalSevaScreen(
    viewModel: MainViewModel,
    onNavigateToTrack: () -> Unit
) {
    val context = LocalContext.current
    val services by viewModel.filteredServices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var selectedServiceForApply by remember { mutableStateOf<CscServiceItem?>(null) }
    var createdTrackingId by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Identity", "Certificates", "Health", "Welfare", "Agriculture", "Utilities")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "CSC Digital Services",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandDarkNavy
            )
            Text(
                text = "Assistance for government certificates, identity cards, and citizen welfare schemes",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search services (e.g. Aadhaar, PAN, Income, Ayushman)", fontSize = 12.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = BrandDarkNavy) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("digital_seva_search")
            )
        }

        // Category Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    val label = when (cat) {
                        "All" -> "All"
                        "Identity" -> "Identity (ID)"
                        "Certificates" -> "Certificates"
                        "Health" -> "Health"
                        "Welfare" -> "Welfare"
                        "Agriculture" -> "Agriculture"
                        "Utilities" -> "Utilities & Bills"
                        else -> cat
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandDarkNavy,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        // Services List
        items(services) { service ->
            CscServiceDetailCard(
                service = service,
                onApplyClick = { selectedServiceForApply = service },
                onOfficialUrlClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(service.officialUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Apply / Enquiry Dialog
    selectedServiceForApply?.let { service ->
        ServiceApplyDialog(
            service = service,
            onDismiss = { selectedServiceForApply = null },
            onSubmit = { name, phone, village, notes ->
                viewModel.submitServiceRequest(
                    customerName = name,
                    customerPhone = phone,
                    villageAddress = village,
                    serviceId = service.id,
                    serviceName = service.titleEn,
                    customerNotes = notes,
                    feeInr = service.feeInr,
                    onSuccess = { trackingId ->
                        selectedServiceForApply = null
                        createdTrackingId = trackingId
                    }
                )
            }
        )
    }

    // Success Tracking Dialog
    createdTrackingId?.let { trackingId ->
        AlertDialog(
            onDismissRequest = { createdTrackingId = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Request Submitted Successfully!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrandDarkNavy
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your Tracking ID is:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = trackingId,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = BrandGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Operator Sunil Kumar (SK CSC) will verify your documents. You can track your status anytime with this ID.",
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        createdTrackingId = null
                        onNavigateToTrack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy)
                ) {
                    Text("Track Status")
                }
            },
            dismissButton = {
                TextButton(onClick = { createdTrackingId = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun CscServiceDetailCard(
    service: CscServiceItem,
    onApplyClick: () -> Unit,
    onOfficialUrlClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandDarkNavy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = service.titleEn,
                        tint = BrandDarkNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.titleEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = service.category,
                        fontSize = 11.sp,
                        color = BrandDarkNavy,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Fee: ₹${service.feeInr}",
                        color = BrandGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = service.descriptionEn,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Documents required checklist box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "Required Documents:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = BrandDarkNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    service.requiredDocuments.forEach { doc ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 1.dp)
                        ) {
                            Text(text = "✓", fontSize = 11.sp, color = BrandGreen, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = doc, fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Apply / Enquiry & Official Portal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApplyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("apply_service_${service.id}")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply / Assist", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onOfficialUrlClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Official Portal", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ServiceApplyDialog(
    service: CscServiceItem,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${service.titleEn}\nApplication Assistance",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BrandDarkNavy
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Please enter your details accurately. Operator Sunil Kumar will contact you shortly.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Applicant Full Name*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile / WhatsApp Number*") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = village,
                    onValueChange = { village = it },
                    label = { Text("Village / Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Details or Notes") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Service Fee: ₹${service.feeInr} (payable upon delivery)",
                    fontSize = 11.sp,
                    color = BrandGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSubmit(name, phone, village, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                enabled = name.isNotBlank() && phone.isNotBlank(),
                modifier = Modifier.testTag("submit_apply_dialog_button")
            ) {
                Text("Submit Request")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
