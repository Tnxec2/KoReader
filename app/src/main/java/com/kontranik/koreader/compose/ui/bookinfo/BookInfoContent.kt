package com.kontranik.koreader.compose.ui.bookinfo

import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.SavedStateHandle
import com.kontranik.koreader.compose.ui.shared.PreviewPortraitLandscapeLightDark
import com.kontranik.koreader.database.model.Author
import de.kontranik.freebudget.ui.theme.AppTheme
import com.kontranik.koreader.compose.theme.paddingMedium
import com.kontranik.koreader.compose.theme.paddingSmall
import kotlinx.coroutines.launch


@Composable
fun BookInfoContent(
    bookInfoDetails: BookInfoDetails,
    navigateToAuthor: (author: Author) -> Unit,
    modifier: Modifier = Modifier,
) {

    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier
            .padding(paddingSmall)
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
        ) {
            bookInfoDetails.cover?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = bookInfoDetails.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = bookInfoDetails.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingSmall)
            )
            Text(
                text = bookInfoDetails.allAuthors,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingSmall)
            )

            Card(modifier = Modifier.padding(bottom = paddingMedium)) {
                AndroidView(factory = { ctx ->
                    TextView(ctx).apply {
                        text = HtmlCompat.fromHtml(
                            bookInfoDetails.annotation,
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                    }
                }, modifier = Modifier.padding(paddingMedium))
            }

            Card(modifier = Modifier.padding(bottom = paddingMedium)) {
                Column(modifier = Modifier.padding(paddingMedium)) {
                    bookInfoDetails.authors.map { author ->
                        Text(
                            text = author.asString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = paddingSmall)
                                .clickable {
                                    coroutineScope.launch { navigateToAuthor(author) }
                                }
                        )
                    }
                }
            }
        }
    }

}

@PreviewPortraitLandscapeLightDark
@Composable
private fun BookInfoContentPreview() {
    val context = LocalContext.current
    val viewModel = BookInfoViewModell(SavedStateHandle(), context)
    val bookInfoDetails = viewModel.bookInfoUiState.bookInfoDetails
    AppTheme {
        BookInfoContent(
            bookInfoDetails = bookInfoDetails,

            navigateToAuthor = {},
        )
    }
}