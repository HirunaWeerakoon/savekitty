package com.example.savekitty.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.savekitty.R
import com.example.savekitty.data.FoodMenu
import com.example.savekitty.data.Food
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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

    // Drag Animation State
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            modifier = Modifier.width(350.dp) // Make it wide enough for dragging
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "DRAG TO FEED",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // --- LEFT SIDE: FOOD SELECTOR ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f).zIndex(1f) // Ensure food renders on top when dragged
                    ) {
                        // UP ARROW 🔼
                        IconButton(
                            onClick = {
                                currentIndex = if (currentIndex > 0) currentIndex - 1 else FoodMenu.lastIndex
                            },
                            modifier = Modifier.size(32.dp).background(Color.LightGray.copy(alpha=0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, null)
                        }

                        // 🍎 DRAGGABLE FOOD IMAGE
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            if (ownedCount > 0) {
                                Image(
                                    painter = painterResource(id = currentFood.imageRes),
                                    contentDescription = currentFood.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragEnd = {
                                                    // CHECK DROP ZONE (Is it far enough right?)
                                                    // Since the cat is on the right, we check X > 150 (approx distance)
                                                    if (offset.value.x > 150f) {
                                                        // SUCCESS! Feed the cat
                                                        onEat(currentFood)
                                                        // Reset immediately for next feed
                                                        scope.launch { offset.snapTo(Offset.Zero) }
                                                    } else {
                                                        // FAILURE: Snap back to start
                                                        scope.launch { offset.animateTo(Offset.Zero) }
                                                    }
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    scope.launch {
                                                        offset.snapTo(
                                                            Offset(
                                                                offset.value.x + dragAmount.x,
                                                                offset.value.y + dragAmount.y
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                )
                            } else {
                                // Greyed out if out of stock
                                Image(
                                    painter = painterResource(id = currentFood.imageRes),
                                    contentDescription = "Out of Stock",
                                    alpha = 0.3f,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        // DOWN ARROW 🔽
                        IconButton(
                            onClick = {
                                currentIndex = (currentIndex + 1) % FoodMenu.size
                            },
                            modifier = Modifier.size(32.dp).background(Color.LightGray.copy(alpha=0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }

                        // INFO
                        Text(currentFood.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("x$ownedCount", fontSize = 12.sp, color = Color.Gray)
                    }

                    // --- RIGHT SIDE: THE CAT (TARGET) ---
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cat_hungry), // Use hungry or waiting face
                            contentDescription = "Hungry Cat",
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Feed Me!",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Close Text
                TextButton(onClick = onClose) {
                    Text("CLOSE", color = Color.Gray, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}