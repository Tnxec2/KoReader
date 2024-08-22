package com.kontranik.koreader.compose.ui.opds

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.opds.model.EntryEditDetails
import com.kontranik.koreader.compose.theme.AppTheme

@Composable
fun OpdsOverviewEntryEditDialog(
    editDetailsMutableState: MutableState<EntryEditDetails>,
    onSave: () -> Unit,
    onClose: () -> Unit,
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
                TextField(
                    value = editDetailsMutableState.value.title,
                    label = {
                        Text(text = stringResource(id = R.string.opds_entry_title))
                    },
                    onValueChange = { editDetailsMutableState.value = editDetailsMutableState.value.copy(title = it) },
                    singleLine = true
                )
                TextField(
                    value = editDetailsMutableState.value.url,
                    label = {
                        Text(text = stringResource(id = R.string.opds_entry_url))
                    },
                    onValueChange = { editDetailsMutableState.value = editDetailsMutableState.value.copy(url = it) },
                    singleLine = true
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
private fun OpdsOverviewEntryEditDialogPreview() {
    AppTheme {
        Surface {
            OpdsOverviewEntryEditDialog(
                editDetailsMutableState = remember {
                    mutableStateOf(EntryEditDetails("Entry Title", "http://link/to/entry"))
                },
                onSave = {},
                onClose = {},
            )
        }
    }

}