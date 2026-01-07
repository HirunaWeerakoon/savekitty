package com.example.savekitty.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.savekitty.R

// --- 1. HOSPITAL POPUP 🏥 ---
@Composable
fun HospitalDialog(
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = {}) { // Not dismissable!
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🏥", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Kitty Admitted!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Because you treated him so poorly (0 Health), your cat has been admitted to the hospital and refuses to return home.",
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("I Understand")
                }
            }
        }
    }
}

// --- 2. CAT SELECTION SCREEN 🐈 ---
@Composable
fun CatSelectionScreen(
    deceasedCats: Set<Int>, // Pass this from Repo
    onCatSelected: (String, Int) -> Unit
) {
    // 5 Cats to choose from
    val availableCats = listOf(
        Pair(0, R.drawable.prop_cat), // Orange
        Pair(1, R.drawable.cat_bnw),   // Black (You need this png)
        Pair(2, R.drawable.cat_white),   // White (You need this png)
        Pair(3, R.drawable.cat_ow), // Siamese (You need this png)
        Pair(4, R.drawable.cat_grey)   // Calico (You need this png)
    )

    var selectedSkin by remember { mutableStateOf<Int?>(null) }
    var nameText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8E9))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Adopt a Companion", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        // Cat List
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(availableCats) { (id, res) ->
                val isDeceased = deceasedCats.contains(id)
                val isSelected = selectedSkin == id

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color.Green.copy(alpha=0.2f) else Color.White)
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) Color.Green else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !isDeceased) { selectedSkin = id }
                        .alpha(if (isDeceased) 0.3f else 1f) // Gray out dead cats
                ) {
                    Image(
                        painter = painterResource(id = res),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    )
                    if (isDeceased) {
                        Text("❌", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Name Input
        OutlinedTextField(
            value = nameText,
            onValueChange = { nameText = it },
            label = { Text("Give it a name") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            enabled = selectedSkin != null && nameText.isNotBlank(),
            onClick = { onCatSelected(nameText, selectedSkin!!) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Welcome Home")
        }
    }
}