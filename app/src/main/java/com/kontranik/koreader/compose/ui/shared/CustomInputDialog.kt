package com.kontranik.koreader.compose.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomInputDialog(
    label: String,
    onSave: () -> Unit,
    onClose: () -> Unit,
    initText: String,
    onChange: (String) -> Unit,
) {
    Dialog(onDismissRequest = { onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {

                OutlinedTextField(
                    value = initText,
                    label = {
                        Text(text = label)
                    },
                    onValueChange = { onChange(it) },
                    singleLine = true,
                    trailingIcon = {
                        if (initText.isNotEmpty())
                            IconButton(onClick = { onChange("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "clear"
                                )
                            }
                    }
                )

                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(
                        paddingSmall,
                        Alignment.Top
                    ),
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall, Alignment.End),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = paddingMedium)
                        .fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            onClose()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = stringResource(id = android.R.string.cancel),
                            modifier = Modifier.padding(end = paddingSmall)
                        )
                        Text(stringResource(id = android.R.string.cancel))
                    }

                    OutlinedButton(
                        onClick = {
                            onSave()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(id = R.string.save),
                            modifier = Modifier.padding(end = paddingSmall)
                        )
                        Text(
                            stringResource(id = R.string.save), modifier = Modifier
                        )
                    }
                }
            }
        }

    }
}

@Preview(widthDp = 200, heightDp = 400)
@Composable
private fun CustomInputDialogPreview() {
    AppTheme {
        Surface() {
            CustomInputDialog(
                label = "Label",
                onSave = {},
                onClose = {},
                onChange = {},
                initText = "Text",
            )
        }
    }
}

@Preview(widthDp = 500, heightDp = 400)
@Composable
private fun CustomInputDialogPreviewWeight() {
    AppTheme {
        Surface() {
            CustomInputDialog(
                label = "Label",
                onSave = {},
                onClose = {},
                onChange = {},
                initText = "Text",
            )
        }
    }
}