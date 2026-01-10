package com.example.savekitty.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savekitty.R
// import com.example.savekitty.ui.theme.TerminalGreen (Use your color if defined)

@Composable
fun OutsideScreen(
    onFoodShopClick: () -> Unit,
    onFurnitureShopClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. BACKGROUND (Placeholder until you draw the art)
        // Ideally, this image has two buildings drawn on it.
        Image(
            painter = painterResource(id = R.drawable.bg_shop), // Reusing shop BG for now
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. CLICK ZONES (Invisible boxes over your buildings)

        // -- FOOD MARKET (Left Side) --
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 40.dp)
                .size(150.dp, 200.dp)
                .background(Color.Black.copy(alpha = 0.3f)) // Debug dimming (Remove later)
                .clickable { onFoodShopClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("FOOD", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // -- FURNITURE STORE (Right Side) --
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp)
                .size(150.dp, 200.dp)
                .background(Color.Black.copy(alpha = 0.3f)) // Debug dimming (Remove later)
                .clickable { onFurnitureShopClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("DECOR", color = Color.White, fontWeight = FontWeight.Bold)
        }

        // 3. BACK BUTTON
        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
                .size(48.dp)
                .clickable { onBackClick() }
        )
    }
}