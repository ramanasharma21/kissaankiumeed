package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppBackground
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate500

data class Crop(
    val name: String,
    val season: String,
    val temp: String,
    val soil: String,
    val water: String,
    val duration: String,
    val description: String,
    val nutrients: String
)

val cropList = listOf(
    Crop(
        name = "Wheat (गेहूँ)",
        season = "Rabi (Oct - Mar)",
        temp = "10°C to 25°C",
        soil = "Well-drained fertile loamy and clayey",
        water = "Moderate (50-100 cm)",
        duration = "120 - 150 days",
        description = "Wheat is the main cereal crop in India. It requires a cool growing season and bright sunshine at the time of ripening.",
        nutrients = "High Nitrogen (N), Moderate Phosphorus (P), Low Potassium (K)"
    ),
    Crop(
        name = "Rice / Paddy (धान)",
        season = "Kharif (Jun - Oct)",
        temp = "21°C to 37°C",
        soil = "Clayey and loamy soil which can retain water",
        water = "High (Above 100 cm)",
        duration = "100 - 150 days",
        description = "Rice is a staple food for the majority of the population in India. It is a water-intensive crop.",
        nutrients = "High Nitrogen, High Zinc, Moderate Phosphorus"
    ),
    Crop(
        name = "Cotton (कपास)",
        season = "Kharif (Jun - Oct)",
        temp = "21°C to 30°C",
        soil = "Black soil of the Deccan plateau",
        water = "Moderate (50-100 cm)",
        duration = "150 - 180 days",
        description = "Cotton is a major cash crop. It requires 210 frost-free days and bright sunshine for its growth.",
        nutrients = "Moderate Nitrogen, High Potassium, Zinc and Boron"
    ),
    Crop(
        name = "Sugarcane (गन्ना)",
        season = "Kharif & Rabi",
        temp = "21°C to 27°C",
        soil = "Deep rich loamy soil",
        water = "Moderate to High (75-150 cm)",
        duration = "10 - 14 months",
        description = "Sugarcane is a tropical as well as a subtropical crop. India is the second-largest producer of sugarcane.",
        nutrients = "High Nitrogen, Moderate Phosphorus, High Potassium"
    ),
    Crop(
        name = "Maize (मक्का)",
        season = "Kharif (Jun - Sep)",
        temp = "21°C to 27°C",
        soil = "Old alluvial soil",
        water = "Moderate (50-100 cm)",
        duration = "90 - 110 days",
        description = "Maize is a crop which is used both as food and fodder. It requires well-drained fertile soils.",
        nutrients = "High Nitrogen, High Phosphorus, Moderate Potassium"
    )
)

@Composable
fun CropInfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        TopAppBar()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cropList) { crop ->
                CropCard(crop)
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom nav
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Crop Information", fontWeight = FontWeight.Bold, color = Emerald800, fontSize = 18.sp)
                Text("FARMING GUIDE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Emerald800, fontSize = 10.sp, letterSpacing = 1.sp)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Emerald800
        )
    )
}

@Composable
fun CropCard(crop: Crop) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp), spotColor = Emerald100, ambientColor = Emerald100),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Emerald100,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🌿", fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = crop.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = crop.season, style = MaterialTheme.typography.labelMedium, color = Emerald800)
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Slate500
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(text = crop.description, style = MaterialTheme.typography.bodyMedium, color = Slate500)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBox(label = "Temperature", value = crop.temp, icon = Icons.Default.Thermostat, modifier = Modifier.weight(1f))
                        InfoBox(label = "Duration", value = crop.duration, icon = Icons.Default.Timer, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoBox(label = "Water", value = crop.water, icon = Icons.Default.WaterDrop, iconTint = Color(0xFF3B82F6), modifier = Modifier.weight(1f))
                        InfoBox(label = "Soil", value = crop.soil, icon = Icons.Default.Grass, iconTint = Color(0xFF8B5A2B), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoBox(label = "Nutrients", value = crop.nutrients, icon = Icons.Default.Science, iconTint = Color(0xFF9C27B0), modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun InfoBox(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, iconTint: Color = Emerald800, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Slate500, fontWeight = FontWeight.Bold)
        }
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 4.dp))
    }
}
