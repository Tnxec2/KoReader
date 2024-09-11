package com.kontranik.koreader.compose.ui.reader

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.utils.ImageUtils

@Composable
fun ImageViewerDialog(
    imageBitmap: Bitmap,
    isDarkMode: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    var invertImage by remember {
        mutableStateOf(isDarkMode)
    }

    Dialog(
        onDismissRequest = { onClose() }
    ) {
        Card {

        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { onClose() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "close")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { invertImage = invertImage.not() }) {
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_compare_24), contentDescription = "invert")
                }
            }
            Image(
                bitmap = if (invertImage)
                    ImageUtils.invertAndTint(imageBitmap, tintColor = null).asImageBitmap()
                else
                    imageBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .padding(paddingSmall)
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            // Update the scale based on zoom gestures.
                            scale *= zoom

                            // Limit the zoom levels within a certain range (optional).
                            scale = scale.coerceIn(0.5f, 3f)

                            // Update the offset to implement panning when zoomed.
                            offset = if (scale == 1f) Offset(0f, 0f) else offset + pan
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale, scaleY = scale,
                        translationX = offset.x, translationY = offset.y
                    )
            )
        }

        }
    }
}

@Preview
@Composable
private fun ImageViewerPreview() {
    val context = LocalContext.current
    val bitmap = context.getDrawable(R.drawable.book_mockup)!!.let { ImageUtils.drawableToBitmap(it)}
    AppTheme {
        Surface {
            ImageViewerDialog(
                imageBitmap = bitmap,
                isDarkMode = false,
                onClose = { }
            )
        }
    }
}