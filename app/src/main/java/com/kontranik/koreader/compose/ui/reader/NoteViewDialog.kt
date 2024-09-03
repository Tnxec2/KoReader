package com.kontranik.koreader.compose.ui.reader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.Html
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLight


@Composable
fun NoteViewDialog(
    note: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier) {


    Dialog(
        onDismissRequest = { onClose() }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { onClose() },
                        modifier = Modifier.padding(end = paddingSmall)) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "close")
                    }
                    Text(
                        text = stringResource(id = R.string.note),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Column(

                ) {
                    Html(
                        text = note,
                        textSize = MaterialTheme.typography.bodyLarge.fontSize.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                                .verticalScroll(rememberScrollState())
                            .weight(weight = 1f, fill = false)
                    )
                }
            }
        }
    }
}

@PreviewPortraitLight
@Composable
private fun NoteViewDialogPreview() {
    AppTheme {
        Surface {
            NoteViewDialog(
                note = "<h1>Header Level 1</h1>\n" +
                        "<p><strong>Auf dem letzten Hause eines kleinen Dörfchens</strong> befand sich ein <abbr title=\"Behausung eines langbeinigen Vogels\">Storchnest</abbr>. Die Storchmutter saß im Neste bei ihren vier Jungen, welche den Kopf mit dem kleinen <em>schwarzen Schnabel</em>, denn er war noch nicht rot geworden, hervorstreckten. Ein Stückchen davon stand auf der Dachfirste starr und steif der Storchvater <code>syntax</code>. Man hätte meinen können, er wäre aus Holz gedrechselt, so stille stand er. „Gewiss sieht es recht vornehm aus, dass meine Frau eine Schildwache bei dem Neste hat!“ dachte er. Und er stand unermüdlich auf <a href=\"#nirgendwo\" title=\"Title für einem Bein\">einem Beine</a>.</p>",
                onClose = {  },
            )

        }
    }
}