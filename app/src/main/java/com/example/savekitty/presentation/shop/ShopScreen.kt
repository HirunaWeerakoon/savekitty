package com.example.savekitty.presentation.shop

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savekitty.R
import com.example.savekitty.data.Food
import com.example.savekitty.data.FoodMenu
import com.example.savekitty.ui.theme.SaveKittyTheme

@Composable
fun ShopScreen(
    coinCount: Int,
    onBuyFood: (Food) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val pixelFont = FontFamily.Monospace

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
            ) {
        // --- LAYER 1: BACKGROUND ---
        // If you don't have bg_shop yet, this grey box acts as a placeholder
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF8D6E63)))



        Image(
            painter = painterResource(id = R.drawable.bg_shop),
            contentDescription = "Shop Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )


        // --- LAYER 2: TOP BAR (Wallet) ---
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_biscuit),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("$coinCount", color = Color.Yellow, fontWeight = FontWeight.Bold)


        }

        // --- LAYER 3: SHELVES & ITEMS ---
        // We can place items manually using Offsets, just like the Room furniture


        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(FoodMenu) { food ->
                ShopItem(
                    imageRes = food.imageRes,
                    price = food.price,
                    label = food.name,
                    canAfford = coinCount >= food.price,
                    onClick = { onBuyFood(food) } // Now this works!
                )
            }
        }

        // --- LAYER 4: EXIT BUTTON ---
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .size(48.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Exit",
                modifier = Modifier
            )

        }
    }
}
@Composable
fun ShopItem(
    imageRes: Int,
    price: Int,
    label: String,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                modifier = Modifier
                    .size(80.dp)
                    .clickable(enabled = canAfford) { onClick() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Price Tag
        Box(
            modifier = Modifier
                .background(if (canAfford) Color.White else Color.Gray, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$price",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (canAfford) Color.Black else Color.DarkGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopScreenPreview() {
    SaveKittyTheme {
        ShopScreen(
            coinCount = 100,
            onBackClick = {},
            onBuyFood = {}
        )
    }
}