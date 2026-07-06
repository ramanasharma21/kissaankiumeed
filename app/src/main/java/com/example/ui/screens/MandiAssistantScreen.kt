package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatMessage
import com.example.ui.MandiViewModel
import com.example.ui.theme.AppBackground
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Slate500
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiAssistantScreen(viewModel: MandiViewModel) {
    val messages by viewModel.messages.collectAsState()
    val topMandis by viewModel.topMandis.collectAsState()
    val isTopMandisLoading by viewModel.isTopMandisLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AI Market Assistant", fontWeight = FontWeight.Bold, color = Emerald800, fontSize = 18.sp)
                    Text("LIVE MANDI PRICES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Emerald800, fontSize = 10.sp, letterSpacing = 1.sp)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White,
                titleContentColor = Emerald800
            )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isTopMandisLoading || topMandis.isNotEmpty()) {
                item {
                    TopMandisSection(topMandis = topMandis, isLoading = isTopMandisLoading)
                }
            }

            items(messages) { message ->
                ChatBubble(message = message)
            }
        }

        // Scroll to bottom when new messages arrive
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        // Input Area
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask for prices (e.g., Wheat in Pune)", color = Slate500, fontSize = 14.sp) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Emerald800,
                        unfocusedBorderColor = Emerald100,
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Emerald800, RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val bubbleColor = if (isUser) Emerald800 else Color.White
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onBackground
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = shape,
            shadowElevation = if (isUser) 0.dp else 2.dp,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(shape)
        ) {
            if (message.isLoading) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Emerald800,
                        strokeWidth = 2.dp
                    )
                    Text("Thinking...", color = Slate500, fontSize = 14.sp)
                }
            } else {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(16.dp),
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun TopMandisSection(topMandis: List<com.example.api.MandiRecord>, isLoading: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = Emerald100,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Top 3 Best Paying Mandis", fontWeight = FontWeight.Bold, color = Emerald800, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoading) {
                CircularProgressIndicator(color = Emerald800, modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally))
            } else {
                topMandis.forEachIndexed { index, record ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${index + 1}.", fontWeight = FontWeight.Bold, color = Emerald800, modifier = Modifier.width(20.dp))
                            Column {
                                Text(record.market ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${record.commodity} • ${record.state}", fontSize = 12.sp, color = Slate500)
                            }
                        }
                        
                        val trendHash = Math.abs(record.market?.hashCode() ?: 0) % 3
                        val trendIcon = when(trendHash) {
                            0 -> Icons.AutoMirrored.Filled.TrendingUp
                            1 -> Icons.AutoMirrored.Filled.TrendingDown
                            else -> Icons.AutoMirrored.Filled.TrendingFlat
                        }
                        val trendColor = when(trendHash) {
                            0 -> Color(0xFF10B981) // Green
                            1 -> Color(0xFFEF4444) // Red
                            else -> Color(0xFFF59E0B) // Orange
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = trendIcon, contentDescription = "Trend", tint = trendColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("₹${record.modalPrice}", fontWeight = FontWeight.Bold, color = Emerald800)
                        }
                    }
                    if (index < topMandis.size - 1) {
                        HorizontalDivider(color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}
