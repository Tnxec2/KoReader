package com.kontranik.koreader.compose.ui.library.byauthor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingMedium

@Composable
fun AuthorsItemPopupMenu(
    title: String,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier) {

    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(
            excludeFromSystemGesture = true,
        ),
        // to dismiss on click outside
        onDismissRequest = { onClose() },
    ) {
        Column(
            modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .padding(4.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(paddingMedium)
                        .weight(1f)
                )
                IconButton(onClick = { onClose() }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "close")
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)

            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onDelete() }
            ) {
                Text(
                    text = stringResource(id = R.string.deletebook),
                    modifier = Modifier
                        .padding(horizontal = paddingMedium, vertical = paddingMedium)
                )
            }
        }
    }
}
