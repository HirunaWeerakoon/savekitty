package com.example.savekitty.presentation.shop

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.savekitty.ui.theme.SaveKittyTheme

@Composable
fun ShopScreen(
    coinCount: Int,
    onBuyFish: () -> Unit,
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
            Text("Coins: $coinCount", color = Color.Yellow, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_coin),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }

        // --- LAYER 3: SHELVES & ITEMS ---
        // We can place items manually using Offsets, just like the Room furniture

        // 🐟 ITEM 1: FISH (5 Coins)
        Column(
            modifier = Modifier
                .align(Alignment.Center) // Place in middle of screen for now
                .offset(y = (-50).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // The Item Image
            Image(
                // Use your shop specific image, or standard fish icon for now
                painter = painterResource(id = R.drawable.ic_fish),
                contentDescription = "Fish",
                modifier = Modifier
                    .size(80.dp)
                    .clickable {
                        if (coinCount >= 5) {
                            onBuyFish()
                            Toast.makeText(context, "You bought a fish!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Not enough coins!", Toast.LENGTH_SHORT).show()
                        }
                    }
            )
            // The Price Tag
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("5 Coins", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- LAYER 4: EXIT BUTTON ---
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("Exit Shop")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ShopScreenPreview() {
    SaveKittyTheme {
        ShopScreen(
            coinCount = 100,
            onBuyFish = {},
            onBackClick = {}
        )
    }
}