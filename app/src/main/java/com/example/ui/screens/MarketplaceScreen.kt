package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.Emerald800
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Slate500
import com.example.ui.theme.AppBackground

data class Product(val id: Int, val name: String, val price: String, val farmer: String, val imageUrl: String)

val dummyProducts = listOf(
    Product(1, "Organic Tomatoes", "₹40 / kg", "Ramesh Kumar", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=400&q=80"),
    Product(2, "Fresh Potatoes", "₹25 / kg", "Suresh Singh", "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=400&q=80"),
    Product(3, "Green Peas", "₹60 / kg", "Anita Devi", "https://images.unsplash.com/photo-1579633633640-5232d3989063?w=400&q=80"),
    Product(4, "Basmati Rice", "₹90 / kg", "Vikram Farm", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400&q=80")
)

@Composable
fun MarketplaceScreen() {
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
            items(dummyProducts) { product ->
                ProductCard(product)
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
                Text("Farm-to-Customer", fontWeight = FontWeight.Bold, color = Emerald800, fontSize = 18.sp)
                Text("FRESH PRODUCE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Emerald800, fontSize = 10.sp, letterSpacing = 1.sp)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Emerald800
        )
    )
}

@Composable
fun ProductCard(product: Product) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp), spotColor = Emerald100, ambientColor = Emerald100),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "By ${product.farmer}", style = MaterialTheme.typography.labelMedium, color = Slate500)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.price, style = MaterialTheme.typography.titleMedium, color = Emerald800, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = { /* Add to cart */ },
                modifier = Modifier
                    .background(Emerald100, RoundedCornerShape(50))
                    .size(40.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Cart", tint = Emerald800, modifier = Modifier.size(20.dp))
            }
        }
    }
}
