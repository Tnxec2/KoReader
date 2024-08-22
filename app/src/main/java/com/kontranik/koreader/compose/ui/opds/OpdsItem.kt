package com.kontranik.koreader.compose.ui.opds

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.rememberOpdsEntryUiDetails
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link
import com.kontranik.koreader.opds.model.OpdsTypes
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.opds.model.Author

@Composable
fun OpdsItem(
    entry: Entry,
    startUrl: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPopup by rememberSaveable { mutableStateOf(false) }
    val uiDetails = rememberOpdsEntryUiDetails(entry = entry, startUrl = startUrl)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = { onClick() },
            )
            .fillMaxWidth()
            .heightIn(min = 30.dp)
    ) {
        uiDetails.value.cover?.let {
            Image(
                bitmap = it,
                contentDescription = uiDetails.value.title,
                modifier = Modifier
                    .padding(horizontal = paddingMedium, vertical = paddingSmall)
                    .size(width = 50.dp, height = 50.dp)
            )
        }

        Column(
            Modifier
                .padding(start = paddingMedium, bottom = paddingSmall)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = uiDetails.value.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (startUrl == OVERVIEW)
                    IconButton(onClick = { showPopup = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "show popup")
                    }
            }
            uiDetails.value.author?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }
            uiDetails.value.content?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                )
            }
        }
        if (showPopup) {
            OpdsEntryPopupMenu(
                title = entry.title,
                onDelete = { showPopup = false; onDelete() },
                onEdit = { showPopup = false; onEdit() },
                onClose = { showPopup = false })
        }

    }

}


@PreviewLightDark
@Composable
private fun OpdsItemPreview() {
    AppTheme {
        Surface {
            OpdsItem(
                entry = Entry(
                    title = "Opds Entry",
                    author = Author(
                        name = "Author", null, null
                    ),
                    thumbnail = Link(
                        type = OpdsTypes.TYPE_LINK_IMAGE_PNG,
                        title = null,
                        href = stringResource(R.string.icon_back_base64),
                        rel = OpdsTypes.REL_IMAGE
                    ),
                    content = null,
                ),
                startUrl = OVERVIEW,
                onClick = { },
                onDelete = { },
                onEdit = { },
            )
        }
    }
}
