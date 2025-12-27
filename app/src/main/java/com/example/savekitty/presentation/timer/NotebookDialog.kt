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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.savekitty.viewModel.TodoItem

@Composable
fun NotebookDialog(
    tasks: List<TodoItem>,
    onAdd: (String) -> Unit,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onClose: () -> Unit
) {
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

                // --- TASK LIST ---
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(tasks) { task ->
                        TaskRow(task, onToggle, onDelete)
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- INPUT AREA ---
                var text by remember { mutableStateOf("") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { Text("Add a task...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onAdd(text)
                            text = "" // Clear input
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        }
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