package com.kontranik.koreader.compose.ui.opds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.opds.model.EntryEditDetails

@OptIn(ExperimentalLayoutApi::class)
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
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = editDetailsMutableState.value.title,
                    label = {
                        Text(text = stringResource(id = R.string.opds_entry_title))
                    },
                    onValueChange = {
                        editDetailsMutableState.value =
                            editDetailsMutableState.value.copy(title = it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                )
                OutlinedTextField(
                    value = editDetailsMutableState.value.url,
                    label = {
                        Text(text = stringResource(id = R.string.opds_entry_url))
                    },
                    onValueChange = {
                        editDetailsMutableState.value = editDetailsMutableState.value.copy(url = it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
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
                        },
                        enabled = editDetailsMutableState.value.url.isNotEmpty()
                                &&
                                editDetailsMutableState.value.title.isNotEmpty()
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
}

@Preview(widthDp = 200, heightDp = 400)
@Composable
private fun OpdsOverviewEntryEditDialogPreview1() {
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

@Preview(widthDp = 500, heightDp = 400)
@Composable
private fun OpdsOverviewEntryEditDialogPreview2() {
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