package com.kontranik.koreader.compose.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall

@Composable
fun TitledDialog(
    title: String,
    onClose: () -> Unit,
    fillMaxSize: Boolean = true,
    modifier: Modifier = Modifier,
    actionIcons: @Composable () -> Unit  = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = fillMaxSize.not()),
        onDismissRequest = { onClose() }
    ) {
        Card(
            modifier = (if (fillMaxSize) modifier
                .fillMaxSize() else modifier)
                .padding(paddingSmall)
        ) {
            Column(
                Modifier.padding(paddingSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { onClose() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close")
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )
                    actionIcons()
                }
                HorizontalDivider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )
                Column(modifier = Modifier.padding(top = paddingSmall)) {
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
private fun TitledDialogPreview() {
    AppTheme {

        Surface {
            TitledDialog(
                title = "Title",
                actionIcons = {
                    IconButton(onClick = {  }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_compare_24),
                            contentDescription = "invert"
                        )
                    }
                },
                onClose = { /*TODO*/ },
            ) {
                Text(text = "Text1")
                Text(text = "Text2")
            }
        }
    }
}

@Preview
@Composable
private fun TitledDialogPreviewShrinkSize() {
    AppTheme {

        Surface {
            TitledDialog(
                title = "Title",
                fillMaxSize = false,
                actionIcons = {
                    IconButton(onClick = {  }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_compare_24),
                            contentDescription = "invert"
                        )
                    }
                },
                onClose = { /*TODO*/ },
            ) {
                Text(text = "Text1")
                Text(text = "Text2")
            }
        }
    }
}