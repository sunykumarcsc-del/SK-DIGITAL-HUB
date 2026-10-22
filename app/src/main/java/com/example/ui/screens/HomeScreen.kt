package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.LocalPrintshop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CscServiceCatalog
import com.example.data.model.CscServiceItem
import com.example.ui.components.dialPhone
import com.example.ui.components.openWhatsApp
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandSaffron
import com.example.ui.theme.BrandSkyBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit
) {
    val shopConfig by viewModel.shopConfig.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        // 1. Hero Shop Banner Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sk_csc_banner),
                    contentDescription = "SK CSC Center Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1672f / 941f),
                    contentScale = ContentScale.FillWidth
                )
            }
        }

        // 2. Owner & Shop Verified Profile Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(BrandDarkNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SK",
                                color = BrandSaffron,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = shopConfig.ownerName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified CSC Operator",
                                    tint = BrandGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Certified CSC Operator (Etawah, UP)",
                                fontSize = 12.sp,
                                color = BrandDarkNavy,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = shopConfig.fullAddress,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { dialPhone(context, shopConfig.phone1) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandDarkNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("home_call_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Call Us", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                openWhatsApp(
                                    context,
                                    shopConfig.phone1,
                                    "Hello Sunil ji! I would like to enquire about services at SK CSC Center."
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("home_whatsapp_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 3. Search Bar: "आपको कौन-सा टूल चाहिए?"
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.updateSearchQuery(it)
                    },
                    placeholder = {
                        Text(
                            text = "Which tool or service do you need? (Search)",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = BrandDarkNavy
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = BrandDarkNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }
        }

        // 4. Track Application Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { onNavigate("track_request") }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BrandGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = "Track",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Track Application Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = BrandGreen
                        )
                        Text(
                            text = "Check live status using Tracking ID or Mobile Number",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open",
                        tint = BrandGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 5. Popular Working Tools Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Digital Tools",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BrandDarkNavy
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    color = BrandSaffron,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigate("tools_hub") }
                )
            }
        }

        // 6. Tools Section (Big, Prominent & High-Impact Cards)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Featured Tool 1: Passport Photo Maker (BIG HERO CARD)
                BigFeaturedHeroToolCard(
                    title = "Passport Size Photo Maker",
                    subtitle = "Print 4x6 & A4 Sheets",
                    badge = "⭐ Most Popular • 4x6 & A4 Sheets",
                    description = "Generate 8 photos on a 4x6 sheet or 24 photos on an A4 sheet in 1 click. White or blue background with auto cutting borders.",
                    icon = Icons.Default.PhotoCamera,
                    color = BrandSaffron,
                    onClick = { onNavigate("passport_photo") }
                )

                // Featured Tool 2: ID Card Dual-Side Layout (BIG HERO CARD)
                BigFeaturedHeroToolCard(
                    title = "Dual-Side ID Card Combiner",
                    subtitle = "Aadhaar, PAN & Voter ID Layout",
                    badge = "🪪 PVC & A4 Ready • Exact Size",
                    description = "Combine front and back sides of Aadhaar, PAN or Voter Card side-by-side or stacked on a single sheet ready for print.",
                    icon = Icons.Default.Badge,
                    color = BrandDarkNavy,
                    onClick = { onNavigate("id_card_layout") }
                )

                // 2-Column Grid for other large tool cards
                val secondaryTools = listOf(
                    ToolGridItem("Photo Compressor", "Compress to 20KB / 50KB", "Form Ready", Icons.Default.Description, "photo_compress", BrandGreen),
                    ToolGridItem("Photos to A4 PDF", "Multi-Image PDF Converter", "A4 PDF", Icons.Default.PictureAsPdf, "pdf_tools", Color(0xFFE53935)),
                    ToolGridItem("Resume & CV Maker", "Job & Biodata Builder", "CV Maker", Icons.Default.Description, "resume_builder", Color(0xFF8E24AA)),
                    ToolGridItem("Store UPI QR", "PhonePe / GPay Store QR", "Shop QR", Icons.Default.QrCode, "qr_generator", Color(0xFF00897B))
                )

                for (i in secondaryTools.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BigToolGridCard(
                            tool = secondaryTools[i],
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(secondaryTools[i].route) }
                        )
                        if (i + 1 < secondaryTools.size) {
                            BigToolGridCard(
                                tool = secondaryTools[i + 1],
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(secondaryTools[i + 1].route) }
                            )
                        }
                    }
                }
            }
        }

        // 7. CSC Digital Seva Highlights
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Key CSC Digital Services",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BrandDarkNavy
                )
                Text(
                    text = "All 12+ Services",
                    fontSize = 12.sp,
                    color = BrandGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigate("digital_seva") }
                )
            }
        }

        // Horizontal CSC Service Cards
        item {
            val featuredServices = CscServiceCatalog.services.take(5)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(featuredServices) { item ->
                    CscMiniCard(item = item, onClick = { onNavigate("digital_seva") })
                }
            }
        }

        // 8. Print Shop Quick Promo
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandDarkNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clickable { onNavigate("print_shop") }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BrandSaffron),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalPrintshop,
                            contentDescription = "Print Shop",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Order Online Printing",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "B&W ₹${shopConfig.bwPrintRate} | Color ₹${shopConfig.colorPrintRate} | Photo Sheet ₹${shopConfig.photoSheetRate}",
                            color = Color(0xFFFFCC80),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { onNavigate("print_shop") },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandSaffron),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Order", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        // 9. Frequently Asked Questions (FAQ) Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Frequently Asked Questions (FAQs)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BrandDarkNavy,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )

            val faqs = listOf(
                "How to make passport photos?" to "Open the Passport Photo tool, upload your picture, select background color (white/blue), and download 4x6 (8 photos) or A4 (24 photos) sheet in 1 click.",
                "Can I check live status of my application?" to "Yes! When you submit a CSC service request, you receive a unique Tracking ID (e.g. SK-2026-XXXX). Enter it in the 'Track' tab to view live status.",
                "How to access official government portals directly?" to "Every service page provides a direct link to authorized official government portals (e-District, UIDAI, NSDL, PM Kisan)."
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                faqs.forEach { (question, answer) ->
                    FaqAccordionItem(question = question, answer = answer)
                }
            }
        }

        // 10. Shop Footer & Legal Disclaimer
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandDarkNavy)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SK CSC CENTER — ETAWAH",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "Operator: Sunil Kumar | Contact: 7906093113, 9528683541",
                    fontSize = 11.sp,
                    color = Color(0xFFFFCC80),
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "Kataiyapur, Post Beena Road, Etawah (Uttar Pradesh), India",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Important Notice: SK CSC Center is an authorized Common Service Center providing citizen assistance for online government applications and digital printing. It does not claim to be an official government entity.",
                    fontSize = 9.sp,
                    color = Color(0xFF78909C),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class ToolGridItem(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
fun BigFeaturedHeroToolCard(
    title: String,
    subtitle: String,
    badge: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.97f
            isHovered -> 1.025f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heroScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 8.dp else 3.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "heroElevation"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isHovered) BorderStroke(1.5.dp, color.copy(alpha = 0.7f)) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .testTag("featured_tool_$title")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(color.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = color,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = BrandDarkNavy,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Text(
                    text = "Open Tool ➔",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BigToolGridCard(
    tool: ToolGridItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isHovered -> 1.035f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "gridToolScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 8.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "gridToolElevation"
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isHovered) BorderStroke(1.5.dp, tool.color.copy(alpha = 0.7f)) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .testTag("tool_card_${tool.route}")
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tool.color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        tint = tool.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(tool.color.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tool.badge,
                        color = tool.color,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tool.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tool.subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Open ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = tool.color
                )
            }
        }
    }
}

@Composable
fun CscMiniCard(
    item: CscServiceItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.96f
            isHovered -> 1.04f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cscMiniScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 6.dp else 1.5.dp,
        label = "cscMiniElevation"
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isHovered) BorderStroke(1.5.dp, BrandDarkNavy.copy(alpha = 0.6f)) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .width(180.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = item.titleEn,
                    tint = BrandDarkNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.titleEn,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fee: ₹${item.feeInr}",
                fontSize = 11.sp,
                color = BrandGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun FaqAccordionItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = answer,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
