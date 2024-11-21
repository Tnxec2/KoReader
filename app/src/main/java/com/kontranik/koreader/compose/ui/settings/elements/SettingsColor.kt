package com.kontranik.koreader.compose.ui.settings.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall

/*

https://github.com/skydoves/colorpicker-compose

dependencies {
    implementation("com.github.skydoves:colorpicker-compose:1.1.2")

    // if you're using Version Catalog
    implementation(libs.compose.colorpicker)
}

 */

@Composable
fun SettingsColor(
    text: String,
    color: Color,
    onSelectDefaultColor: () -> Unit,
    onColorChanged: (color: Color) -> Unit,
    modifier: Modifier = Modifier,
    showPickerPopup: Boolean = false,
    selectNoneButtonText: String? = null,
    ) {

    val showPicker = remember {
        mutableStateOf(showPickerPopup)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                showPicker.value = true
            }
    ) {
        Text(
            text = text,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .padding(paddingSmall)
                .background(color)
                .size(30.dp)
        ) {}
    }
    if (showPicker.value)
        Popup(onDismissRequest = {
            showPicker.value = false
        }) {
            ColorPicker(
                color = color,
                selectNoneButtonText = selectNoneButtonText,
                onCancel = {
                    showPicker.value = false
                },
                onSelectDefaultColor = {
                    onSelectDefaultColor()
                    showPicker.value = false
                },
                onSave = {
                    onColorChanged(it)
                    showPicker.value = false
                }
            )
        }
}

@Composable
fun ColorPicker(
    color: Color,
    onCancel: () -> Unit,
    onSave: (color: Color)->Unit,
    onSelectDefaultColor: ()->Unit,
    modifier: Modifier = Modifier,
    selectNoneButtonText: String? = null,
    controller: ColorPickerController = rememberColorPickerController(),
) {

    val hexColor = remember() {
        mutableStateOf(color.toHexCodeWithAlpha())
    }
    LaunchedEffect(key1 = color) {
        println("color: ${color.toHexCodeWithAlpha()}")
        controller.selectByColor(color, false)
    }

    Dialog(
        onDismissRequest = { onCancel() }
    ) {
        Card(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingSmall),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(paddingSmall)
            ) {
            HsvColorPicker(
                initialColor = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .height(200.dp),
                controller = controller,
            )
            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .height(30.dp),
                controller = controller,
            )
            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .height(30.dp),
                controller = controller,
            )

                OutlinedTextField(
                    label = {
                        Text(text = "HEX Color")
                    },
                    value = hexColor.value,
                    onValueChange = {
                        hexColor.value = it
                        try {
                            controller.selectByColor(
                                Color(it.toColorInt()), true
                            )
                        } catch (_: Exception) {

                        }
                    })
                AlphaTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(top = paddingSmall)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingSmall)
            ) {
                OutlinedButton(onClick = { onCancel() }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }

                OutlinedButton(onClick = { onSave(controller.selectedColor.value) }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                selectNoneButtonText?.let {
                    OutlinedButton(onClick = { onSelectDefaultColor() }) {
                        Text(text = it)
                    }
                }
            }

            }
        }
    }
}

fun Color.toHexCodeWithAlpha(): String {
    val alpha = this.alpha*255
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    return String.format("#%02x%02x%02x%02x", alpha.toInt(),red.toInt(), green.toInt(), blue.toInt())
}

fun String.toColorInt(): Int {
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or 0x00000000ff000000L
        } else if (length != 9) {
            throw IllegalArgumentException("Unknown color")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}


@Preview
@Composable
private fun SettingsColorWithNoneButtonPreview() {
    Surface {
        SettingsColor(
            text = "Colorname",
            color = Color.Magenta,
            onColorChanged = {},
            onSelectDefaultColor = {},
            selectNoneButtonText = "set default color",
            showPickerPopup = false
        )
    }
}

@Preview
@Composable
private fun SettingsColorPreview() {
    Surface {
        SettingsColor(
            text = "Colorname",
            color = Color.Magenta,
            onColorChanged = {},
            onSelectDefaultColor = {},
            showPickerPopup = true
        )
    }
}