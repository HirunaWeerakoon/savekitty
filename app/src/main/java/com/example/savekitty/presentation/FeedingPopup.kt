package com.example.savekitty.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.savekitty.data.Food
import com.example.savekitty.data.FoodMenu
import com.example.savekitty.R

@Composable
fun FeedingPopup(
    inventory: Map<String, Int>,
    onEat: (Food) -> Unit,
    onClose: () -> Unit
) {
    // START at Index 0 (Fish)
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentFood = FoodMenu[currentIndex]
    val ownedCount = inventory[currentFood.id] ?: 0

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("FEED KITTY", fontSize = 20.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                // --- SELECTION ROW ---
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // LEFT SIDE: INFO
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentFood.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Restores: ${currentFood.healthPoints / 2.0} ❤️", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF8D6E63), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Owned: $ownedCount", color = Color.White, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // RIGHT SIDE: ARROWS & IMAGE
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // UP ARROW 🔼
                        IconButton(
                            onClick = {
                                // Cycle BACKWARDS (Loop to end if at 0)
                                currentIndex = if (currentIndex > 0) currentIndex - 1 else FoodMenu.lastIndex
                            },
                            modifier = Modifier.size(32.dp).background(Color.LightGray.copy(alpha=0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, null)
                        }

                        // FOOD IMAGE 🍎
                        Image(
                            painter = painterResource(id = currentFood.imageRes),
                            contentDescription = currentFood.name,
                            modifier = Modifier.size(64.dp)
                        )

                        // DOWN ARROW 🔽
                        IconButton(
                            onClick = {
                                // Cycle FORWARDS (Loop to 0 if at end)
                                currentIndex = (currentIndex + 1) % FoodMenu.size
                            },
                            modifier = Modifier.size(32.dp).background(Color.LightGray.copy(alpha=0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- ACTION BUTTON ---
                Button(
                    onClick = {
                        if (ownedCount > 0) onEat(currentFood)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (ownedCount > 0) Color(0xFF4CAF50) else Color.Gray
                    ),
                    enabled = ownedCount > 0
                ) {
                    Text(if (ownedCount > 0) "FEED NOW" else "OUT OF STOCK")
                }
            }
        }
    }
}