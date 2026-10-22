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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
fun DocToolsScreen(
    viewModel: MainViewModel,
    onNavigateToPrint: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: ID Card Combiner, 1: Xerox Filter

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BrandDarkNavy,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BrandSaffron,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Dual-Side ID Combiner", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Document Xerox Filter", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        if (selectedTab == 0) {
            IdCardCombinerTab(viewModel, onNavigateToPrint)
        } else {
            DocScannerTab(viewModel)
        }
    }
}

@Composable
fun IdCardCombinerTab(
    viewModel: MainViewModel,
    onNavigateToPrint: () -> Unit
) {
    val context = LocalContext.current
    var frontBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var backBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isSideBySide by remember { mutableStateOf(true) }
    var isA4Sheet by remember { mutableStateOf(true) }
    var combinedResult by remember { mutableStateOf<Bitmap?>(null) }

    val frontPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { frontBitmap = PhotoUtils.loadBitmapFromUri(context, it) }
    }

    val backPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { backBitmap = PhotoUtils.loadBitmapFromUri(context, it) }
    }

    LaunchedEffect(frontBitmap, backBitmap, isSideBySide, isA4Sheet) {
        if (frontBitmap != null && backBitmap != null) {
            combinedResult = PhotoUtils.combineIdCardLayout(
                front = frontBitmap!!,
                back = backBitmap!!,
                isSideBySide = isSideBySide,
                sheetSizeA4 = isA4Sheet
            )
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
                    text = "1. Select ID Card Front & Back",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandDarkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Front Picker Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, if (frontBitmap != null) BrandGreen else Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable {
                                frontPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (frontBitmap != null) {
                            Image(
                                bitmap = frontBitmap!!.asImageBitmap(),
                                contentDescription = "Front side",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = BrandDarkNavy)
                                Text("Front Side", fontSize = 11.sp, color = BrandDarkNavy, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Back Picker Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, if (backBitmap != null) BrandGreen else Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable {
                                backPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (backBitmap != null) {
                            Image(
                                bitmap = backBitmap!!.asImageBitmap(),
                                contentDescription = "Back side",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = BrandDarkNavy)
                                Text("Back Side", fontSize = 11.sp, color = BrandDarkNavy, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        if (frontBitmap != null && backBitmap != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "2. Layout Settings",
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
                            title = "Side-by-Side",
                            selected = isSideBySide,
                            modifier = Modifier.weight(1f)
                        ) { isSideBySide = true }

                        SheetOptionButton(
                            title = "Stacked (Top/Bottom)",
                            selected = !isSideBySide,
                            modifier = Modifier.weight(1f)
                        ) { isSideBySide = false }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SheetOptionButton(
                            title = "A4 Sheet Print",
                            selected = isA4Sheet,
                            modifier = Modifier.weight(1f)
                        ) { isA4Sheet = true }

                        SheetOptionButton(
                            title = "PVC Card Sheet",
                            selected = !isA4Sheet,
                            modifier = Modifier.weight(1f)
                        ) { isA4Sheet = false }
                    }
                }
            }

            combinedResult?.let { res ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ready to Print, Cut & Laminate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BrandDarkNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(res.width.toFloat() / res.height.toFloat())
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                bitmap = res.asImageBitmap(),
                                contentDescription = "Combined ID Card",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val file = PhotoUtils.saveBitmapToFile(
                                        context,
                                        res,
                                        "SK_ID_Card_${System.currentTimeMillis()}.jpg"
                                    )
                                    Toast.makeText(context, "ID Card layout saved!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    val file = PhotoUtils.saveBitmapToFile(
                                        context,
                                        res,
                                        "SK_ID_Card_share.jpg"
                                    )
                                    PdfUtils.shareFile(context, file, mimeType = "image/jpeg")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    val file = PhotoUtils.saveBitmapToFile(
                                        context,
                                        res,
                                        "SK_ID_Print_${System.currentTimeMillis()}.jpg"
                                    )
                                    viewModel.submitPrintOrder(
                                        customerName = "Walk-in ID Customer",
                                        customerPhone = "",
                                        documentTitle = "Combined ID Card (PVC)",
                                        printType = "PVC Card",
                                        paperSize = "PVC Card",
                                        copies = 1,
                                        imagePath = file.absolutePath,
                                        onSuccess = { onNavigateToPrint() }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandSaffron),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f)
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
fun DocScannerTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    var docBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var filteredBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var filterMode by remember { mutableStateOf("BW_XEROX") } // "BW_XEROX", "GRAYSCALE", "COLOR_ENHANCE"
    var contrastLevel by remember { mutableFloatStateOf(1.3f) }

    val docPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val bmp = PhotoUtils.loadBitmapFromUri(context, it)
            docBitmap = bmp
        }
    }

    LaunchedEffect(docBitmap, filterMode, contrastLevel) {
        docBitmap?.let { src ->
            filteredBitmap = PhotoUtils.enhanceDocument(src, filterMode, contrastLevel)
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
                    text = "Select Document or Photo to Enhance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = BrandDarkNavy
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        docPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pick Document")
                }
            }
        }

        if (docBitmap != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Choose Filter Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandDarkNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SheetOptionButton(
                            title = "B&W Xerox",
                            selected = filterMode == "BW_XEROX",
                            modifier = Modifier.weight(1f)
                        ) { filterMode = "BW_XEROX" }

                        SheetOptionButton(
                            title = "Clear Grayscale",
                            selected = filterMode == "GRAYSCALE",
                            modifier = Modifier.weight(1f)
                        ) { filterMode = "GRAYSCALE" }

                        SheetOptionButton(
                            title = "Enhance Color",
                            selected = filterMode == "COLOR_ENHANCE",
                            modifier = Modifier.weight(1f)
                        ) { filterMode = "COLOR_ENHANCE" }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Contrast Level: ${"%.1f".format(contrastLevel)}x",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = contrastLevel,
                        onValueChange = { contrastLevel = it },
                        valueRange = 0.8f..2.2f,
                        colors = SliderDefaults.colors(
                            thumbColor = BrandSaffron,
                            activeTrackColor = BrandDarkNavy
                        )
                    )
                }
            }

            filteredBitmap?.let { bmp ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(bmp.width.toFloat() / bmp.height.toFloat())
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Scanned filtered document",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    PhotoUtils.saveBitmapToFile(
                                        context,
                                        bmp,
                                        "SK_Scan_${System.currentTimeMillis()}.jpg"
                                    )
                                    Toast.makeText(context, "Clean document saved!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download")
                            }

                            Button(
                                onClick = {
                                    val file = PhotoUtils.saveBitmapToFile(context, bmp, "SK_Scan_share.jpg")
                                    PdfUtils.shareFile(context, file, mimeType = "image/jpeg")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share")
                            }
                        }
                    }
                }
            }
        }
    }
}
