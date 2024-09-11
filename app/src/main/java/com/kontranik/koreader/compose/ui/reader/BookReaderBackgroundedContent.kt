package com.kontranik.koreader.compose.ui.reader

import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.kontranik.koreader.compose.ui.settings.ThemeColors
import java.io.InputStream

@Composable
fun BookReaderBackgroundedContent(
    backgroundColor: Color,
    colors: ThemeColors,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    val context = LocalContext.current

    val backgroundImage = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    val imageBrush = remember(backgroundImage.value) {
        backgroundImage.value?.let {
            ShaderBrush(
                shader = ImageShader(
                    image = it,
                    tileModeX = TileMode.Repeated,
                    tileModeY = TileMode.Repeated,
                )
            )
        }
    }

    val painterModifier = if (colors.showBackgroundImage)
        backgroundImage.value?.let {
            if (colors.backgroundImageTiledRepeat)
                imageBrush?.let { brush ->
                    modifier
                        .background(backgroundColor)
                        .background(brush) }
            else
                modifier
                    .background(backgroundColor)
                    .paint(
                        painter = BitmapPainter(it),
                        alignment = Alignment.TopStart,
                        contentScale = if (colors.stetchBackgroundImage) ContentScale.FillBounds else
                            ContentScale.None
                    )
        } ?: modifier.background(backgroundColor)
        else
            modifier.background(backgroundColor)


    LaunchedEffect(key1 = colors) {
        colors.backgroundImageUri?.let {
            val uri = Uri.parse(colors.backgroundImageUri)
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapDrawable(context.resources, inputStream)
                if (colors.backgroundImageTiledRepeat)
                    bitmap.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                backgroundImage.value = bitmap.bitmap.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    Column(
        painterModifier
            .fillMaxSize()
    ) {
        content()
    }

}
