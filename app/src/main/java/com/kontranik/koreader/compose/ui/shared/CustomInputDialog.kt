package com.kontranik.koreader.compose.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.theme.AppTheme

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
                modifier = Modifier.padding(16.dp),
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
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "clear")
                            }
                    }
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
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

                Spacer(modifier = Modifier.weight(1f))

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
                    Text(stringResource(id = R.string.save))
                }
            }

        }

    }
}

@Preview
@Composable
private fun CustomInputDialogPreview() {
    AppTheme {
        Surface {
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