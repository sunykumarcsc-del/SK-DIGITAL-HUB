package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandSaffron
import com.example.ui.theme.BrandSkyBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PdfUtils
import com.example.util.PhotoUtils

@Composable
fun PhotoToolsScreen(
    viewModel: MainViewModel,
    onNavigateToPrint: () -> Unit
) {
    val context = LocalContext.current
    var selectedToolTab by remember { mutableIntStateOf(0) } // 0: Passport Maker, 1: Compress & Resize

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        TabRow(
            selectedTabIndex = selectedToolTab,
            containerColor = BrandDarkNavy,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                    color = BrandSaffron,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedToolTab == 0,
                onClick = { selectedToolTab = 0 },
                text = { Text("Passport Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedToolTab == 1,
                onClick = { selectedToolTab = 1 },
                text = { Text("Compress & Resize", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        if (selectedToolTab == 0) {
            PassportPhotoMakerTab(viewModel, onNavigateToPrint)
        } else {
            PhotoCompressorTab(viewModel)
        }
    }
}

@Composable
fun PassportPhotoMakerTab(
    viewModel: MainViewModel,
    onNavigateToPrint: () -> Unit
) {
    val context = LocalContext.current
    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Sheet type: "SINGLE", "4X6", "A4"
    var sheetType by remember { mutableStateOf("4X6") }
    // Background color: White, Studio Blue, Grey, Red
    var selectedBgColor by remember { mutableIntStateOf(AndroidColor.WHITE) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val bmp = PhotoUtils.loadBitmapFromUri(context, it)
            sourceBitmap = bmp
        }
    }

    // Regenerate preview whenever source, sheetType, or bgColor changes
    LaunchedEffect(sourceBitmap, sheetType, selectedBgColor) {
        sourceBitmap?.let { src ->
            val singlePhoto = PhotoUtils.processPassportPhoto(src, bgColor = selectedBgColor)
            val sheet = when (sheetType) {
                "4X6" -> PhotoUtils.create4x6PassportSheet(singlePhoto)
                "A4" -> PhotoUtils.createA4PassportSheet(singlePhoto, count = 24)
                else -> singlePhoto
            }
            previewBitmap = sheet
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "1. Select Customer Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandDarkNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pick_passport_photo_button")
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Image from Gallery")
                }
            }
        }

        // Configuration Options
        if (sourceBitmap != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "2. Select Sheet Format",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandDarkNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SheetOptionButton(
                            title = "4x6 Sheet (8 Photos)",
                            selected = sheetType == "4X6",
                            modifier = Modifier.weight(1f)
                        ) { sheetType = "4X6" }

                        SheetOptionButton(
                            title = "A4 Sheet (24 Photos)",
                            selected = sheetType == "A4",
                            modifier = Modifier.weight(1f)
                        ) { sheetType = "A4" }

                        SheetOptionButton(
                            title = "Single Photo",
                            selected = sheetType == "SINGLE",
                            modifier = Modifier.weight(1f)
                        ) { sheetType = "SINGLE" }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "3. Background Color",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandDarkNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val bgColors = listOf(
                        Pair(AndroidColor.WHITE, "White"),
                        Pair(AndroidColor.parseColor("#A0D2EB"), "Studio Blue"),
                        Pair(AndroidColor.parseColor("#E0E0E0"), "Light Grey"),
                        Pair(AndroidColor.parseColor("#C62828"), "Red")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bgColors.forEach { (colorVal, name) ->
                            val isSelected = selectedBgColor == colorVal
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(colorVal))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) BrandSaffron else Color.Gray,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedBgColor = colorVal },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (colorVal == AndroidColor.WHITE) BrandDarkNavy else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Preview Section
            previewBitmap?.let { bmp ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Print Preview & Cutting Guidelines",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BrandDarkNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(bmp.width.toFloat() / bmp.height.toFloat())
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Passport Sheet Preview",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons: Save, Share, Send to Print Shop
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val file = PhotoUtils.saveBitmapToFile(
                                        context,
                                        bmp,
                                        "SK_Passport_${sheetType}_${System.currentTimeMillis()}.jpg"
                                    )
                                    Toast.makeText(context, "Passport sheet saved to gallery / files!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_passport_sheet_button")
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val file = PhotoUtils.saveBitmapToFile(
                                        context,
                                        bmp,
                                        "SK_Passport_${sheetType}_share.jpg"
                                    )
                                    PdfUtils.shareFile(context, file, mimeType = "image/jpeg")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_passport_sheet_button")
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val file = PhotoUtils.saveBitmapToFile(
                                        context,
                                        bmp,
                                        "SK_Print_${System.currentTimeMillis()}.jpg"
                                    )
                                    viewModel.submitPrintOrder(
                                        customerName = "Walk-in Customer",
                                        customerPhone = "",
                                        documentTitle = "Passport Sheet ($sheetType)",
                                        printType = "Photo Glossy",
                                        paperSize = if (sheetType == "4X6") "4x6 Photo" else "A4",
                                        copies = 1,
                                        imagePath = file.absolutePath,
                                        onSuccess = { onNavigateToPrint() }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSaffron),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("send_to_print_shop_button")
                            ) {
                                Icon(imageVector = Icons.Default.LocalPrintshop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Print", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SheetOptionButton(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) BrandDarkNavy else Color(0xFFF1F5F9))
            .border(
                1.dp,
                if (selected) BrandSaffron else Color(0xFFCBD5E1),
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else TextPrimary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PhotoCompressorTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var targetWidth by remember { mutableStateOf("600") }
    var targetHeight by remember { mutableStateOf("800") }
    var quality by remember { mutableFloatStateOf(80f) }
    var compressedBytes by remember { mutableStateOf<ByteArray?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val bmp = PhotoUtils.loadBitmapFromUri(context, it)
            sourceBitmap = bmp
            bmp?.let { b ->
                targetWidth = b.width.toString()
                targetHeight = b.height.toString()
            }
        }
    }

    LaunchedEffect(sourceBitmap, targetWidth, targetHeight, quality) {
        sourceBitmap?.let { bmp ->
            val w = targetWidth.toIntOrNull() ?: bmp.width
            val h = targetHeight.toIntOrNull() ?: bmp.height
            if (w > 0 && h > 0) {
                val (bytes, _) = PhotoUtils.compressAndResize(
                    bitmap = bmp,
                    targetWidth = w,
                    targetHeight = h,
                    format = Bitmap.CompressFormat.JPEG,
                    quality = quality.toInt()
                )
                compressedBytes = bytes
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Select File to Compress (Photo / Signature)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandDarkNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Image")
                }
            }
        }

        if (sourceBitmap != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Quick Presets for Govt Forms",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandDarkNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PresetChip(label = "Exam Form (<50 KB)", onClick = {
                            targetWidth = "200"
                            targetHeight = "230"
                            quality = 65f
                        }, modifier = Modifier.weight(1f))

                        PresetChip(label = "Signature (<20 KB)", onClick = {
                            targetWidth = "140"
                            targetHeight = "60"
                            quality = 55f
                        }, modifier = Modifier.weight(1f))

                        PresetChip(label = "PAN Card (<100 KB)", onClick = {
                            targetWidth = "300"
                            targetHeight = "400"
                            quality = 75f
                        }, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = targetWidth,
                            onValueChange = { targetWidth = it },
                            label = { Text("Width (px)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = targetHeight,
                            onValueChange = { targetHeight = it },
                            label = { Text("Height (px)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quality: ${quality.toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Slider(
                        value = quality,
                        onValueChange = { quality = it },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = BrandSaffron,
                            activeTrackColor = BrandDarkNavy
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Size display in KB
                    compressedBytes?.let { bytes ->
                        val kb = bytes.size / 1024f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Resulting File Size: %.1f KB".format(kb),
                                fontWeight = FontWeight.Bold,
                                color = BrandGreen,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            compressedBytes?.let { bytes ->
                                val file = PhotoUtils.saveBitmapToFile(
                                    context,
                                    sourceBitmap!!,
                                    "SK_Compressed_${System.currentTimeMillis()}.jpg"
                                )
                                Toast.makeText(context, "Compressed image saved successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Compressed Image")
                    }
                }
            }
        }
    }
}

@Composable
fun PresetChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFEEF2F6))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandDarkNavy,
            textAlign = TextAlign.Center
        )
    }
}
