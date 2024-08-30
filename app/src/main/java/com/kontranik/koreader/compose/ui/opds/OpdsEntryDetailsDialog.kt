package com.kontranik.koreader.compose.ui.opds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.opds.model.Entry
import com.kontranik.koreader.opds.model.Link


@Composable
fun OpdsEntryDetailsDialog(
    onClose: () -> Unit,
    entry: Entry,
    navigateToOpdsEntryLink: (Link) -> Unit,
    download: (Entry, Link) -> Unit,
    openInBrowser: (Link) -> Unit,
    startUrl: String,
    modifier: Modifier = Modifier
) {

    Dialog(
        //properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onClose() },
                        modifier = Modifier.padding(end = paddingSmall)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "close")
                    }
                    Text(
                        text = stringResource(id = R.string.opds_entry_details),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider()
                OpdsEntryDetailsContent(
                    entry = entry,
                    startUrl = startUrl,
                    navigateToOpdsEntryLink = navigateToOpdsEntryLink,
                    download = download,
                    openInBrowser = openInBrowser,
                    modifier = modifier
                )
            }
        }
    }
}