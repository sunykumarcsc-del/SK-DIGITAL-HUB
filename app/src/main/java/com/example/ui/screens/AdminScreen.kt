package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrintOrderEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.ShopConfigEntity
import com.example.ui.components.dialPhone
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandSaffron
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AdminScreen(
    viewModel: MainViewModel
) {
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()

    if (!isAuthenticated) {
        AdminLoginCard(viewModel)
    } else {
        AdminDashboardContent(viewModel)
    }
}

@Composable
fun AdminLoginCard(viewModel: MainViewModel) {
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BrandDarkNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Login",
                        tint = BrandSaffron,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Operator Admin Login",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrandDarkNavy
                )
                Text(
                    text = "Sunil Kumar (Operator) Secure Portal",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 6) pin = it },
                    label = { Text("4-Digit Security PIN (Default: 1234)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val ok = viewModel.authenticateAdmin(pin)
                        if (ok) {
                            Toast.makeText(context, "Admin login successful!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("admin_login_button")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enter Dashboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardContent(viewModel: MainViewModel) {
    val requests by viewModel.allRequests.collectAsState()
    val printOrders by viewModel.allPrintOrders.collectAsState()
    val shopConfig by viewModel.shopConfig.collectAsState()
    val context = LocalContext.current

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Requests, 1: Print Orders, 2: Rates & Settings

    val pendingCount = requests.count { it.status == "Pending" }
    val readyCount = requests.count { it.status == "Ready" || it.status == "Completed" }
    val totalRevenue = requests.sumOf { if (it.isPaid) it.feeInr else 0 } + printOrders.sumOf { it.calculatedPrice }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Admin Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SK CSC Admin Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = BrandDarkNavy
                    )
                    Text(
                        text = "Operator: ${shopConfig.ownerName} | Etawah",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                IconButton(
                    onClick = { viewModel.logoutAdmin() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFEBEE), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Metrics Overview Grid (Total Requests, Pending, Print Jobs, Total Revenue)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Total Requests",
                    value = "${requests.size}",
                    color = BrandDarkNavy,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Pending",
                    value = "$pendingCount",
                    color = BrandSaffron,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Print Orders",
                    value = "${printOrders.size}",
                    color = Color(0xFF5E35B1),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Revenue",
                    value = "₹$totalRevenue",
                    color = BrandGreen,
                    modifier = Modifier.weight(1.1f)
                )
            }
        }

        // Admin Tabs: Requests, Print Jobs, Shop Settings
        item {
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.White,
                contentColor = BrandDarkNavy,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                        color = BrandSaffron,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = { Text("Requests (${requests.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = { Text("Print Jobs (${printOrders.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    text = { Text("Rates & Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Content based on tab
        if (selectedAdminTab == 0) {
            items(requests) { req ->
                AdminRequestCard(req = req, viewModel = viewModel, context = context)
            }
        } else if (selectedAdminTab == 1) {
            items(printOrders) { order ->
                AdminPrintOrderCard(order = order, viewModel = viewModel)
            }
        } else {
            item {
                AdminSettingsCard(shopConfig = shopConfig, onSave = { newConfig ->
                    viewModel.updateShopConfig(newConfig)
                })
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = title, fontSize = 9.sp, color = TextSecondary, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun AdminRequestCard(
    req: ServiceRequestEntity,
    viewModel: MainViewModel,
    context: Context
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = req.trackingNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandDarkNavy
                    )
                    Text(
                        text = req.serviceName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Box {
                    Button(
                        onClick = { showStatusMenu = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(req.status, color = BrandDarkNavy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("Pending", "Processing", "Ready", "Completed").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    viewModel.updateRequestStatus(req.id, status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Customer: ${req.customerName} | Phone: ${req.customerPhone} | Address: ${req.villageAddress}",
                fontSize = 11.sp,
                color = TextSecondary
            )

            if (req.customerNotes.isNotBlank()) {
                Text(
                    text = "Notes: ${req.customerNotes}",
                    fontSize = 11.sp,
                    color = BrandDarkNavy
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direct Call & WhatsApp buttons for admin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { dialPhone(context, req.customerPhone) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call Customer", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = {
                        openWhatsApp(
                            context,
                            req.customerPhone,
                            "Hello ${req.customerName} ji! Contacting you from SK CSC Center regarding your request (${req.trackingNumber} - ${req.serviceName})."
                        )
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun AdminPrintOrderCard(
    order: PrintOrderEntity,
    viewModel: MainViewModel
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${order.documentTitle} (${order.orderNumber})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Customer: ${order.customerName} | ${order.customerPhone}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${order.printType} • ${order.copies} copies • Total: ₹${order.calculatedPrice}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen
                )
            }

            Box {
                Button(
                    onClick = { showStatusMenu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(order.status, color = BrandDarkNavy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false }
                ) {
                    listOf("Queued", "Printing", "Ready for Pickup", "Delivered").forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status) },
                            onClick = {
                                viewModel.updateOrderStatus(order.id, status)
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsCard(
    shopConfig: ShopConfigEntity,
    onSave: (ShopConfigEntity) -> Unit
) {
    var bwRate by remember { mutableStateOf(shopConfig.bwPrintRate.toString()) }
    var colorRate by remember { mutableStateOf(shopConfig.colorPrintRate.toString()) }
    var photoRate by remember { mutableStateOf(shopConfig.photoSheetRate.toString()) }
    var pvcRate by remember { mutableStateOf(shopConfig.pvcCardRate.toString()) }
    var announcement by remember { mutableStateOf(shopConfig.announcementText) }
    var phone1 by remember { mutableStateOf(shopConfig.phone1) }
    var phone2 by remember { mutableStateOf(shopConfig.phone2) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Shop Rates & Price Management",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = BrandDarkNavy
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bwRate,
                    onValueChange = { bwRate = it },
                    label = { Text("B&W Print Rate (₹)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = colorRate,
                    onValueChange = { colorRate = it },
                    label = { Text("Color Print Rate (₹)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = photoRate,
                    onValueChange = { photoRate = it },
                    label = { Text("4x6 Photo Sheet (₹)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = pvcRate,
                    onValueChange = { pvcRate = it },
                    label = { Text("PVC Card Print (₹)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = announcement,
                onValueChange = { announcement = it },
                label = { Text("Top Announcement Marquee") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = phone1,
                    onValueChange = { phone1 = it },
                    label = { Text("Contact Number 1") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = phone2,
                    onValueChange = { phone2 = it },
                    label = { Text("Contact Number 2") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val updated = shopConfig.copy(
                        bwPrintRate = bwRate.toIntOrNull() ?: shopConfig.bwPrintRate,
                        colorPrintRate = colorRate.toIntOrNull() ?: shopConfig.colorPrintRate,
                        photoSheetRate = photoRate.toIntOrNull() ?: shopConfig.photoSheetRate,
                        pvcCardRate = pvcRate.toIntOrNull() ?: shopConfig.pvcCardRate,
                        announcementText = announcement,
                        phone1 = phone1,
                        phone2 = phone2
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_admin_settings_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Changes & Rates")
            }
        }
    }
}
