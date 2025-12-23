package com.example.savekitty.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.savekitty.R
import kotlin.math.roundToInt

@Composable
fun FeedingPopup(
    fishCount: Int,
    onFeed: () -> Unit,
    onClose: () -> Unit,
    onBuyFish: () -> Unit // Temporary way to buy fish inside the popup
) {
    Dialog(onDismissRequest = onClose) {
        // The White Card Background
        Box(
            modifier = Modifier
                .size(350.dp, 300.dp)
                .background(Color.DarkGray, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            // Title
            Text("Feed the Kitty!", color = Color.White, modifier = Modifier.align(Alignment.TopCenter))

            // 1. THE STAGE (Row with Fish Stack and Cat Face)
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT SIDE: The Fish Stack 🐟
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("x$fishCount", color = Color.White, fontSize = 20.sp)

                    if (fishCount > 0) {
                        DraggableFish(onDropOnTarget = onFeed)
                    } else {
                        // Show "Buy" button if empty
                        Button(onClick = onBuyFish) {
                            Text("Buy (5c)")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // RIGHT SIDE: The Hungry Cat (Target Zone) 🐱
                // We just show the head for feeding
                Image(
                    painter = painterResource(id = R.drawable.cat_hungry),
                    contentDescription = "Cat Mouth",
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
fun DraggableFish(onDropOnTarget: () -> Unit) {
    // Current X/Y offset of the dragged fish
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // LOGIC: Did we drop it far enough to the right?
                        // If dragged more than 300px to the right, consider it "Fed"
                        if (offsetX > 200) {
                            onDropOnTarget()
                        }
                        // Snap back to start
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_fish),
            contentDescription = "Fish",
            modifier = Modifier.size(64.dp)
        )
    }
}