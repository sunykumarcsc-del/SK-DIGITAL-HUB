package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandDarkNavy
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandSaffron
import com.example.ui.theme.BrandSkyBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class ToolDefinition(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val description: String,
    val category: String,
    val route: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun ToolsHubScreen(
    onNavigate: (String) -> Unit
) {
    val allTools = remember {
        listOf(
            ToolDefinition(
                id = "passport",
                titleHi = "Passport Size Photo Maker",
                titleEn = "4x6 & A4 Sheet Printing",
                description = "Generate 8 to 24 photos on 4x6 or A4 sheets with white/blue background and cutting lines.",
                category = "Photo",
                route = "passport_photo",
                icon = Icons.Default.PhotoCamera,
                color = BrandSaffron
            ),
            ToolDefinition(
                id = "compress",
                titleHi = "Photo & Signature Compressor",
                titleEn = "Resize to 20KB / 50KB / 100KB",
                description = "Resize and compress photos and signatures for online government and job application forms.",
                category = "Photo",
                route = "photo_compress",
                icon = Icons.Default.Compress,
                color = BrandGreen
            ),
            ToolDefinition(
                id = "id_layout",
                titleHi = "ID Card Dual-Side Combiner",
                titleEn = "Aadhaar, PAN & Voter ID",
                description = "Combine front and back sides of Aadhaar, PAN, Voter or Ration Card on a single page in PVC size.",
                category = "Document",
                route = "id_card_layout",
                icon = Icons.Default.Badge,
                color = BrandDarkNavy
            ),
            ToolDefinition(
                id = "doc_scanner",
                titleHi = "Document B&W Xerox Filter",
                titleEn = "Contrast & Photocopy Enhancer",
                description = "Clear document contrast and remove background shadows with clean Black & White Xerox effect.",
                category = "Document",
                route = "doc_scanner",
                icon = Icons.Default.DocumentScanner,
                color = Color(0xFF5E35B1)
            ),
            ToolDefinition(
                id = "pdf_tools",
                titleHi = "Images to A4 PDF Converter",
                titleEn = "Multi-Image PDF Maker",
                description = "Merge multiple document images into a single standardized A4 PDF file ready for submission.",
                category = "PDF",
                route = "pdf_tools",
                icon = Icons.Default.PictureAsPdf,
                color = Color(0xFFE53935)
            ),
            ToolDefinition(
                id = "resume",
                titleHi = "Resume & Biodata Maker",
                titleEn = "Job & Matrimonial Formats",
                description = "Build a professional Indian format resume or matrimonial biodata and generate a printable PDF instantly.",
                category = "Design",
                route = "resume_builder",
                icon = Icons.Default.Description,
                color = Color(0xFF00897B)
            ),
            ToolDefinition(
                id = "qr_gen",
                titleHi = "Store UPI QR Generator",
                titleEn = "PhonePe / GPay Store QR",
                description = "Generate your branded merchant UPI payment QR code for PhonePe, Google Pay, Paytm and BHIM.",
                category = "Design",
                route = "qr_generator",
                icon = Icons.Default.QrCode,
                color = BrandSkyBlue
            )
        )
    }

    var selectedTab by remember { mutableStateOf("All") }
    val categories = listOf("All", "Photo", "Document", "PDF", "Design")

    val filteredTools = if (selectedTab == "All") allTools else allTools.filter { it.category == selectedTab }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Digital Tools Hub",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BrandDarkNavy
            )
            Text(
                text = "Productive automated tools for Cyber Cafes, CSC operators, and students",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedTab == cat
                    val label = when (cat) {
                        "All" -> "All Tools"
                        "Photo" -> "Photo Tools"
                        "Document" -> "Document Tools"
                        "PDF" -> "PDF Tools"
                        "Design" -> "Design Tools"
                        else -> cat
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = cat },
                        label = { Text(text = label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandDarkNavy,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextPrimary
                        ),
                        modifier = Modifier.testTag("filter_chip_$cat")
                    )
                }
            }
        }

        // List of Tool Cards (Bigger & Interactive with Hover Scale Effects)
        items(filteredTools) { tool ->
            InteractiveToolHubCard(tool = tool, onNavigate = onNavigate)
        }
    }
}

@Composable
fun InteractiveToolHubCard(
    tool: ToolDefinition,
    onNavigate: (String) -> Unit
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
        label = "toolHubScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 8.dp else 2.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "toolHubElevation"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isHovered) BorderStroke(1.5.dp, tool.color.copy(alpha = 0.7f)) else BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onNavigate(tool.route) }
            .testTag("hub_tool_${tool.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(tool.color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.titleEn,
                        tint = tool.color,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tool.titleHi,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = tool.titleEn,
                        fontSize = 12.sp,
                        color = BrandDarkNavy,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tool.description,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onNavigate(tool.route) },
                colors = ButtonDefaults.buttonColors(containerColor = tool.color),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Text(
                    text = "Use Tool ➔",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
