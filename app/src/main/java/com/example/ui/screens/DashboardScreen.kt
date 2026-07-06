package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Slate500
import com.example.ui.theme.AppBackground

@Composable
fun DashboardScreen(
    viewModel: com.example.ui.DashboardViewModel,
    onNavigateToCropDoctor: () -> Unit,
    onNavigateToMandiAssistant: () -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Greeting
        Text(
            text = "Namaste, Rajesh Ji",
            style = MaterialTheme.typography.bodySmall,
            color = Slate500
        )
        Text(
            text = "Your Farm at a Glance",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // AI Crop Doctor Hero Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), ambientColor = Emerald100, spotColor = Emerald100)
                .clip(RoundedCornerShape(24.dp))
                .clickable { onNavigateToCropDoctor() },
            color = Emerald800
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Crop Doctor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Detect disease in seconds",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald100,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Grid Layout (Weather & Mandi)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weather Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                if (weatherState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(weatherState.icon, contentDescription = weatherState.condition, tint = Color(0xFF3B82F6))
                                Text("WEATHER", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate500, fontSize = 10.sp)
                            }
                            Text(weatherState.temperature, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }
                        Text("${weatherState.condition}\nRain Prob: ${weatherState.rainProbability}", style = MaterialTheme.typography.labelSmall, color = Slate500)
                    }
                }
            }

            // Mandi Prices Card
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
                    .clickable { onNavigateToMandiAssistant() },
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFF59E0B))
                            Text("MANDI PRICES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate500, fontSize = 10.sp)
                        }
                        Text("₹2,150", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                    }
                    Text("Wheat (UP)\nPrice peaked today", style = MaterialTheme.typography.labelSmall, color = Slate500)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Financial Status Widget
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Financial Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Surface(
                        color = Emerald100,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "PM-KISAN ELIGIBLE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(50))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(8.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                        )
                    }
                    Text("₹4,500/₹5,000", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate500)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("LOAN APPLIED", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Slate500)
                        Text("₹50,000", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("INSURANCE", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = Slate500)
                        Text("Active", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
    }
}
