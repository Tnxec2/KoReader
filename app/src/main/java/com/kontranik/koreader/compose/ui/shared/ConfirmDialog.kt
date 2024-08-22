package com.kontranik.koreader.compose.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall


@Composable
fun ConfirmDialog(
    title: String?,
    text: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    isCancelable: Boolean = true,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(paddingMedium),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingSmall),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (title != null) Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                //Spacer(modifier = Modifier.padding(paddingSmall))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (isCancelable)
                        TextButton(
                            onClick = { onDismissRequest() },
                            modifier = Modifier.padding(paddingSmall),
                        ) {
                            Text(stringResource(android.R.string.cancel))
                        }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(paddingSmall),
                    ) {
                        Text(stringResource(android.R.string.ok))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmDialogPreview() {
    AppTheme {
        ConfirmDialog(
            title = "Title",
            text = stringResource(id = R.string.sure_delete_book),
            onDismissRequest = {  },
            onConfirmation = { },
            )
    }
}

@Preview
@Composable
private fun ConfirmDialogPreview2() {
    AppTheme {
        ConfirmDialog(
            title = "Title",
            text = stringResource(id = R.string.sure_delete_book),
            onDismissRequest = {  },
            onConfirmation = { },
            isCancelable = false
        )
    }
}