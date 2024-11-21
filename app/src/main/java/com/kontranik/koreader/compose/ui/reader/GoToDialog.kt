package com.kontranik.koreader.compose.ui.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kontranik.koreader.R
import com.kontranik.koreader.compose.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingBig
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import com.kontranik.koreader.compose.ui.shared.TitledDialog

@Composable
fun GoToDialog(
    sectionInitial: Int,
    pageInitial: Int,
    maxPage: Int,
    aSections: List<String>,
    gotoSection: (Int) -> Unit,
    gotoPage: (Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    var page by remember {
        mutableIntStateOf(pageInitial)
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        lazyListState.scrollToItem(sectionInitial)
    }

    TitledDialog(
        title = stringResource(id = R.string.go_to),
        onClose = { onClose() }
    ) {
        Text(
            text = stringResource(id = R.string.page),
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { if (page > 0 ) page-- }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(
                    id = R.string.decrease
                ))
            }
            Text(text = page.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f))
            IconButton(onClick = { if (page < maxPage ) page++ }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(
                    id = R.string.increase
                ))
            }
            IconButton(onClick = { gotoPage(page) }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "ok")
            }
        }
        Text(
            text = stringResource(id = R.string.section),
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(aSections) { index, section  ->
                Text(
                    text = section,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                        .clickable {
                            gotoSection(index)
                        }
                )
                if (index < aSections.size)
                    HorizontalDivider()
            }
        }
    }
}

@Preview
@Composable
private fun GoToDialogPreview() {
    AppTheme {

        Surface {

        GoToDialog(
            sectionInitial = 0,
            pageInitial = 5,
            maxPage = 10,
            aSections = listOf("Section 1", "Section 2", "Section 3"),
            gotoSection = {},
            gotoPage = {},
            onClose = {  })

        }

    }
}