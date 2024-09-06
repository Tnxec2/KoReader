package com.kontranik.koreader.compose.ui.shared

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.preference.PreferenceManager
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.settings.PREF_KEY_COLOR_BACK
import com.kontranik.koreader.compose.ui.settings.PREF_KEY_COLOR_TEXT
import com.kontranik.koreader.compose.ui.settings.defaultColors


@Composable
fun DropdownListThemed(
    themesList: List<String>,
    selectedTheme: String,
    selectedThemeIndex: Int,
    onItemClick: (pos: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
    show: Boolean = false,
    textSize: TextUnit,
    typeface: Typeface,
) {

    val context = LocalContext.current
    var showDropdown by rememberSaveable { mutableStateOf(show) }
    val scrollState = rememberScrollState()
    val (colorBack, colorText) = getThemeColors(context, selectedThemeIndex)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable { showDropdown = !showDropdown; }
                .background(colorBack)
                .fillMaxWidth()
        ) {
            Text(
                text = selectedTheme,
                fontSize = textSize,
                fontFamily = FontFamily(typeface),
                color = colorText,
                modifier = Modifier
                    .padding(paddingSmall)
                    .weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "open",
                tint = colorText
            )
        }

        // dropdown list
        Box {
            if (showDropdown) {
                DropdownListContentThemed(
                    themesList = themesList,
                    scrollState = scrollState,
                    onItemClick = onItemClick,
                    onClose = { showDropdown = false; },
                    textSize = textSize,
                    typeface = typeface,
                )
            }
        }
    }
}

@Composable
fun DropdownListContentThemed(
    themesList: List<String>,
    onItemClick: (pos: Int, value: String) -> Unit,
    scrollState: ScrollState,
    onClose: () -> Unit,
    textSize: TextUnit,
    typeface: Typeface,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(
            excludeFromSystemGesture = true,
        ),
        // to dismiss on click outside
        onDismissRequest = { onClose() }
    ) {
        LazyColumn(
            modifier = modifier
                .heightIn(max = 300.dp)
                .padding(paddingSmall)
                //.verticalScroll(state = scrollState)
                .border(width = 1.dp, color = Color.Gray)
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(
                themesList,
                key = { index, _ -> index}
                ) { index, item ->
                if (index != 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
                }

                val (colorBack, colorText) = getThemeColors(context, index)

                Box(
                    modifier = Modifier
                        .clickable {
                            onItemClick(index, item)
                            onClose()
                        }
                        .background(colorBack)
                        .fillMaxWidth()
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = textSize,
                        fontFamily = FontFamily(typeface),
                        color = colorText,
                        modifier = Modifier
                            .padding(paddingSmall)
                    )
                }
            }

        }
    }
}

@Composable
private fun getThemeColors(
    context: Context,
    themesIndex: Int
): Pair<Color, Color> {
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val defColors = defaultColors[themesIndex]
    var co = prefs.getInt(PREF_KEY_COLOR_BACK + themesIndex, 0)
    val colorBack =
        if (co != 0) Color(co)
        else defColors.colorBackground

    co = prefs.getInt(PREF_KEY_COLOR_TEXT + themesIndex, 0)
    val colorText =
        if (co != 0) Color(co)
        else defColors.colorsText
    return Pair(colorBack, colorText)
}

@Preview
@Composable
private fun DropdownListPreview() {
    val options = listOf("theme1", "theme2", "theme3", "theme4", "theme4")

    var expanded by remember { mutableStateOf(false) }

    AppTheme {
        Surface {

        DropdownListThemed(
            themesList = options,
            selectedTheme = options[1],
            selectedThemeIndex = 1,
            onItemClick = { pos, item ->  },
            textSize = 13.sp,
            typeface = Typeface.DEFAULT,
            show = true,
            )

        }
    }
}


