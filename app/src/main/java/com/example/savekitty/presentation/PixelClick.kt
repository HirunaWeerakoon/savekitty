package com.example.savekitty.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize

fun Modifier.pixelClickable(
    imageBitmap: ImageBitmap,
    onClick: () -> Unit
): Modifier {
    return this.pointerInput(imageBitmap) {
        detectTapGestures { offset ->
            val androidBitmap = imageBitmap.asAndroidBitmap()

            // 1. Calculate the scaling factor (in case image is resized on screen)
            val scaleX = androidBitmap.width / size.width.toFloat()
            val scaleY = androidBitmap.height / size.height.toFloat()

            // 2. Find the pixel on the original bitmap
            val pixelX = (offset.x * scaleX).toInt()
            val pixelY = (offset.y * scaleY).toInt()

            // 3. Check boundaries to avoid crashes
            if (pixelX in 0 until androidBitmap.width && pixelY in 0 until androidBitmap.height) {
                // 4. Check Alpha (Transparency)
                val pixel = androidBitmap.getPixel(pixelX, pixelY)
                val alpha = (pixel shr 24) and 0xFF // Extract Alpha channel

                if (alpha > 20) { // If not transparent (allow slight anti-aliasing)
                    onClick()
                }
            }
        }
    }
}