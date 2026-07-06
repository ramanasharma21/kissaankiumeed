package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CropDoctorState
import com.example.ui.MainViewModel
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Slate500
import com.example.ui.theme.AppBackground

@Composable
fun CropDoctorScreen(viewModel: MainViewModel) {
    val state by viewModel.cropDoctorState.collectAsState()
    var selectedImageUri by remember<MutableState<Uri?>> { mutableStateOf(null) }
    var bitmap by remember<MutableState<Bitmap?>> { mutableStateOf(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = Emerald800,
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌱", fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "AI Crop Doctor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Emerald800
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Upload a photo of your crop to detect diseases and get treatment advice.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate500,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(32.dp), spotColor = Emerald100, ambientColor = Emerald100)
                .clickable { launcher.launch("image/*") },
            shape = RoundedCornerShape(32.dp),
            color = Color.White
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Selected Crop Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Emerald50)
                        .border(1.dp, Emerald100, RoundedCornerShape(32.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Emerald800
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tap to select image", color = Emerald800, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                bitmap?.let { viewModel.analyzeCropImage(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(28.dp), spotColor = Emerald100),
            enabled = bitmap != null && state !is CropDoctorState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Emerald800,
                disabledContainerColor = Emerald100
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (state is CropDoctorState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Analyze Crop", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (state) {
            is CropDoctorState.Success -> {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp), spotColor = Emerald100),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Diagnosis Result",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Emerald800
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (state as CropDoctorState.Success).result,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            is CropDoctorState.Error -> {
                Text(
                    text = "Error: ${(state as CropDoctorState.Error).message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {}
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
    }
}
