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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.util.QrUtils
import java.io.File

@Composable
fun DesignToolsScreen(
    viewModel: MainViewModel,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }

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
                text = { Text("Resume & Biodata", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("UPI QR Code", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        if (selectedTab == 0) {
            ResumeBuilderTab(viewModel)
        } else {
            QrGeneratorTab(viewModel)
        }
    }
}

@Composable
fun ResumeBuilderTab(viewModel: MainViewModel) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var category by remember { mutableStateOf("OBC") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("Etawah, Uttar Pradesh") }
    var education by remember {
        mutableStateOf("High School (10th) - UP Board (68%)\nIntermediate (12th) - UP Board (72%)\nGraduation (B.A/B.Sc/B.Com) - CSJM University")
    }
    var skills by remember {
        mutableStateOf("Basic Computer (CCC Course), MS Office & Excel\nHindi & English Typing\nInternet & Online Form Filling Expert")
    }
    var experience by remember {
        mutableStateOf("Fresher / Ready to work in Cyber Cafe, CSC or Office")
    }

    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var generatedResumeFile by remember { mutableStateOf<File?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { photoBitmap = PhotoUtils.loadBitmapFromUri(context, it) }
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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Enter Resume / Biodata Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BrandDarkNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Photo picker for resume
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp, 85.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, Color.Gray, RoundedCornerShape(6.dp))
                            .clickable {
                                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoBitmap != null) {
                            Image(
                                bitmap = photoBitmap!!.asImageBitmap(),
                                contentDescription = "Photo",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray)
                                Text("Add Photo", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Applicant Full Name*") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = fatherName,
                            onValueChange = { fatherName = it },
                            label = { Text("Father's Name*") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Date of Birth (DD/MM/YYYY)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number*") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Permanent Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = education,
                    onValueChange = { education = it },
                    label = { Text("Educational Qualifications") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Skills & Computer Knowledge") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Work Experience") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank()) {
                            Toast.makeText(context, "Please enter your name and phone number!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val file = PdfUtils.createResumePdf(
                            context = context,
                            name = name,
                            fatherName = fatherName,
                            dob = dob,
                            gender = gender,
                            category = category,
                            phone = phone,
                            email = email,
                            address = address,
                            education = education,
                            skills = skills,
                            experience = experience,
                            photoBitmap = photoBitmap
                        )
                        generatedResumeFile = file
                        if (file != null) {
                            Toast.makeText(context, "Resume PDF created successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_resume_pdf_button")
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Resume A4 PDF", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Generated Resume File
        generatedResumeFile?.let { file ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Resume Generated: ${file.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { PdfUtils.shareFile(context, file, "application/pdf") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share / WhatsApp")
                        }

                        Button(
                            onClick = { PdfUtils.shareFile(context, file, "application/pdf") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandSaffron),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print / View")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QrGeneratorTab(viewModel: MainViewModel) {
    val context = LocalContext.current
    var upiId by remember { mutableStateOf("7906093113@upi") }
    var payeeName by remember { mutableStateOf("Sunil Kumar (SK CSC)") }
    var amount by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(upiId, payeeName, amount) {
        val upiString = buildString {
            append("upi://pay?pa=$upiId&pn=${Uri.encode(payeeName)}&cu=INR")
            if (amount.isNotBlank()) {
                append("&am=$amount")
            }
        }
        qrBitmap = QrUtils.generateQrBitmap(upiString, size = 512)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                Text(
                    text = "UPI Payment QR Code",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BrandDarkNavy
                )
                Text(
                    text = "Ready to scan via PhonePe, Google Pay, Paytm & BHIM",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID (VPA)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = payeeName,
                    onValueChange = { payeeName = it },
                    label = { Text("Shop / Payee Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹) [Optional]") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        qrBitmap?.let { bmp ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SK CSC CENTER",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = BrandDarkNavy
                    )
                    Text(
                        text = payeeName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandSaffron
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .border(2.dp, BrandDarkNavy, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "UPI QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Scan & Pay with any UPI App\nGoogle Pay | PhonePe | Paytm | BHIM",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

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
                                    "SK_UPI_QR_${System.currentTimeMillis()}.jpg"
                                )
                                Toast.makeText(context, "QR Code saved to gallery!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Download", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val file = PhotoUtils.saveBitmapToFile(context, bmp, "SK_UPI_QR_share.jpg")
                                PdfUtils.shareFile(context, file, "image/jpeg")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
