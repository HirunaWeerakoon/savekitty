package com.example.savekitty.presentation.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savekitty.R
import com.example.savekitty.data.Decoration
import com.example.savekitty.data.DecorationType
import com.example.savekitty.data.ItemCatalog

@Composable
fun FurnitureShopScreen(
    coinCount: Int,
    inventory: Map<String, Int>, // What we own
    placedItems: Map<DecorationType, String>, // What is currently on the wall
    onBuy: (Decoration) -> Unit,
    onEquip: (Decoration) -> Unit,
    onBackClick: () -> Unit
) {
    // TABS state
    var selectedCategory by remember { mutableStateOf(DecorationType.CLOCK) }
    val categories = DecorationType.values()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8E9)) // Cream
            .systemBarsPadding()
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Back",
                modifier = Modifier.size(40.dp).clickable { onBackClick() }
            )

            // COIN DISPLAY
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$coinCount",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E3333)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_biscuit),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // --- CATEGORY TABS ---
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            containerColor = Color.Transparent,
            contentColor = Color(0xFF008080),
            edgePadding = 16.dp
        ) {
            categories.forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    text = {
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ITEMS GRID ---
        // Filter items by the selected tab
        val visibleItems = ItemCatalog.decorations.filter { it.type == selectedCategory }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(visibleItems) { item ->
                val isOwned = (inventory[item.id] ?: 0) > 0
                val isEquipped = placedItems[item.type] == item.id

                FurnitureItemCard(
                    item = item,
                    isOwned = isOwned,
                    isEquipped = isEquipped,
                    canAfford = coinCount >= item.price,
                    onAction = {
                        if (isOwned) {
                            onEquip(item)
                        } else {
                            onBuy(item)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FurnitureItemCard(
    item: Decoration,
    isOwned: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // IMAGE
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // NAME
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // BUTTON
            Button(
                onClick = onAction,
                enabled = isOwned || canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isEquipped -> Color(0xFF4CAF50) // Green for Equipped
                        isOwned -> Color(0xFF2196F3)    // Blue for Equip
                        else -> Color(0xFF008080)       // Teal for Buy
                    }
                ),
                modifier = Modifier.fillMaxWidth().height(36.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = when {
                        isEquipped -> "EQUIPPED"
                        isOwned -> "EQUIP"
                        else -> "${item.price} 🍪"
                    },
                    fontSize = 12.sp
                )
            }
        }
    }
}