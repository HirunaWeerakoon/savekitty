package com.example.savekitty.presentation.SpriteAnimation

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay

@Composable
fun SpriteAnimation(
    frames: List<Int>,
    frameDurationMillis: Long = 150,
    modifier: Modifier = Modifier
) {
    if (frames.isEmpty()) return
    var currentFrame by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(frameDurationMillis)
            currentFrame = (currentFrame + 1) % frames.size
        }
    }

    Image(
        painter = painterResource(id = frames[currentFrame]),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}