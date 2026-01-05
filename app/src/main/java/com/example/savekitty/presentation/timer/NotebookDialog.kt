package com.example.savekitty.presentation.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.savekitty.data.TodoItem

@Composable
fun NotebookDialog(
    tasks: List<TodoItem>,
    onAdd: (String, Boolean) -> Unit,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onClose: () -> Unit
) {
    var newTaskText by remember { mutableStateOf("") }

    // TAB STATE: true = Daily, false = Long Term
    var isDailyTab by remember { mutableStateOf(true) }

    // Filter the list based on the tab
    val currentList = tasks.filter { it.isDaily == isDailyTab }

    Dialog(onDismissRequest = onClose) {
        // The "Paper" Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp)) // Paper Color
                .padding(16.dp)
        ) {
            Column {
                // --- HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Session Goals", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    TabButton(
                        text = "DAILY",
                        isSelected = isDailyTab,
                        onClick = { isDailyTab = true },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "LONG TERM",
                        isSelected = !isDailyTab,
                        onClick = { isDailyTab = false },
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(color = Color.Gray, thickness = 1.dp)

                // --- TASK LIST ---
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(top = 8.dp)
                ) {
                    items(currentList) { item ->
                        TodoRow(item, onToggle, onDelete)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- INPUT AREA ---
                var text by remember { mutableStateOf("") }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    TextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        placeholder = { Text("New task...", fontFamily = FontFamily.Monospace) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    IconButton(
                        onClick = {
                            if (newTaskText.isNotBlank()) {
                                // Add to the CURRENT active tab
                                onAdd(newTaskText, isDailyTab)
                                newTaskText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                    }
                }
            }
        }
    }
}
@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color(0xFF8D6E63) else Color.Transparent) // Brown if selected
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}


@Composable
fun TaskRow(task: TodoItem, onToggle: (Long) -> Unit, onDelete: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onToggle(task.id) }
        )
        Text(
            text = task.text,
            modifier = Modifier.weight(1f),
            style = if (task.isDone) TextStyle(textDecoration = TextDecoration.LineThrough, color = Color.Gray)
            else TextStyle(color = Color.Black)
        )
        IconButton(onClick = { onDelete(task.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
        }
    }
}
@Composable
fun TodoRow(item: TodoItem, onToggle: (Long) -> Unit, onDelete: (Long) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = item.isDone,
            onCheckedChange = { onToggle(item.id) },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF8D6E63))
        )
        Text(
            text = item.text,
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily.Monospace,
            style = if (item.isDone) androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else androidx.compose.ui.text.TextStyle()
        )
        IconButton(onClick = { onDelete(item.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
        }
    }
}