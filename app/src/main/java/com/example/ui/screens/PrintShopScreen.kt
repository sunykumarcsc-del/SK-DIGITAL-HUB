package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrintOrderEntity
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandSaffron
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PhotoUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrintShopScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val shopConfig by viewModel.shopConfig.collectAsState()
    val allOrders by viewModel.allPrintOrders.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var documentTitle by remember { mutableStateOf("") }
    var selectedPrintType by remember { mutableStateOf("B&W") } // "B&W", "Color", "Photo Glossy", "PVC Card"
    var selectedPaperSize by remember { mutableStateOf("A4") } // "A4", "4x6 Photo", "PVC Card"
    var copies by remember { mutableIntStateOf(1) }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var attachedImagePath by remember { mutableStateOf("") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val bmp = PhotoUtils.loadBitmapFromUri(context, it)
            attachedBitmap = bmp
            bmp?.let { b ->
                val f = PhotoUtils.saveBitmapToFile(context, b, "SK_Print_Upload_${System.currentTimeMillis()}.jpg")
                attachedImagePath = f.absolutePath
            }
        }
    }

    val unitRate = when (selectedPrintType) {
        "Color" -> shopConfig.colorPrintRate
        "Photo Glossy" -> shopConfig.photoSheetRate
        "PVC Card" -> shopConfig.pvcCardRate
        else -> shopConfig.bwPrintRate
    }
    val totalPrice = unitRate * copies

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
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
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalPrintshop,
                                contentDescription = null,
                                tint = BrandDarkNavy
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Print Shop Order",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = BrandDarkNavy
                            )
                            Text(
                                text = "Send files and print specifications to place an order",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = documentTitle,
                        onValueChange = { documentTitle = it },
                        label = { Text("Document Title*") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Your Name*") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Mobile Number*") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Print Type & Paper:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = BrandDarkNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SheetOptionButton(
                            title = "B&W (₹${shopConfig.bwPrintRate})",
                            selected = selectedPrintType == "B&W",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPrintType = "B&W"
                            selectedPaperSize = "A4"
                        }

                        SheetOptionButton(
                            title = "Color (₹${shopConfig.colorPrintRate})",
                            selected = selectedPrintType == "Color",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPrintType = "Color"
                            selectedPaperSize = "A4"
                        }

                        SheetOptionButton(
                            title = "Photo (₹${shopConfig.photoSheetRate})",
                            selected = selectedPrintType == "Photo Glossy",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPrintType = "Photo Glossy"
                            selectedPaperSize = "4x6 Photo"
                        }

                        SheetOptionButton(
                            title = "PVC (₹${shopConfig.pvcCardRate})",
                            selected = selectedPrintType == "PVC Card",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedPrintType = "PVC Card"
                            selectedPaperSize = "PVC Card"
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Copies Counter & Live Price Calculator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Number of Copies:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                IconButton(
                                    onClick = { if (copies > 1) copies-- },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFEEF2F6), CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "$copies",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                                IconButton(
                                    onClick = { copies++ },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFEEF2F6), CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Live calculated price badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFE8F5E9))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Estimated Total",
                                    fontSize = 10.sp,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "₹$totalPrice",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrandGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // File upload button
                    Button(
                        onClick = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (attachedBitmap != null) "File attached ✓ (Tap to change)" else "Upload Document / Photo for Print")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val title = if (documentTitle.isNotBlank()) documentTitle else "Untitled Document"
                            val name = if (customerName.isNotBlank()) customerName else "Walk-in Customer"
                            viewModel.submitPrintOrder(
                                customerName = name,
                                customerPhone = customerPhone,
                                documentTitle = title,
                                printType = selectedPrintType,
                                paperSize = selectedPaperSize,
                                copies = copies,
                                imagePath = attachedImagePath,
                                onSuccess = {
                                    documentTitle = ""
                                    attachedBitmap = null
                                    attachedImagePath = ""
                                    Toast.makeText(context, "Print order placed successfully!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSaffron),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_print_order_button")
                    ) {
                        Icon(imageVector = Icons.Default.LocalPrintshop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Print Order", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Print Orders History Header
        item {
            Text(
                text = "Recent Print Jobs:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = BrandDarkNavy
            )
        }

        // List of past/current print orders
        items(allOrders) { order ->
            PrintOrderCard(order = order)
        }
    }
}

@Composable
fun PrintOrderCard(order: PrintOrderEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.createdAt))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEDE7F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalPrintshop,
                    contentDescription = null,
                    tint = Color(0xFF512DA8),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${order.documentTitle} (${order.orderNumber})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${order.customerName} | ${order.printType} • ${order.copies} copies • ${order.paperSize}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "$dateStr | Total: ₹${order.calculatedPrice}",
                    fontSize = 10.sp,
                    color = BrandGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
            StatusBadge(status = order.status)
        }
    }
}
