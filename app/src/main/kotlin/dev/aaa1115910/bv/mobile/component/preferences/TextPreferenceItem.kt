package dev.aaa1115910.bv.mobile.component.preferences

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

@Composable
fun TextPreferenceItem(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLineTitle: Boolean = false,
    singleLineSummary: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier
            .clickable(enabled) { onClick?.invoke() },
        headlineContent = {
            StatusWrapper(enabled) {
                Text(
                    text = title,
                    maxLines = if (singleLineTitle) 1 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        supportingContent = (@Composable {
            StatusWrapper(enabled) {
                Text(
                    text = summary!!,
                    maxLines = if (singleLineSummary) 1 else Int.MAX_VALUE,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }).takeIf { summary != null },
        leadingContent = (@Composable {
            StatusWrapper(enabled) {
                leadingContent?.invoke()
            }
        }).takeIf { leadingContent != null },
        trailingContent = (@Composable {
            StatusWrapper(enabled) {
                trailingContent?.invoke()
            }
        }).takeIf { trailingContent != null },
    )
}

@Composable
fun StatusWrapper(enabled: Boolean = true, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentColor provides if (enabled) LocalContentColor.current
        else LocalContentColor.current.copy(alpha = 0.38f)
    ) {
        content()
    }
}

private class TextPreferenceItemPreviewParameterProvider :
    PreviewParameterProvider<TextPreferenceItemPreviewData> {
    override val values = sequenceOf(
        TextPreferenceItemPreviewData("text preference item", "enabled", true),
        TextPreferenceItemPreviewData("text preference item", "disabled", false),
        TextPreferenceItemPreviewData("text preference item", null, true),
        TextPreferenceItemPreviewData(
            title = "long title long title long title long title long title long title ",
            summary = "long summary long summary long summary long summary long summary ",
            enabled = false,
            setTrainingContent = true
        ),
        TextPreferenceItemPreviewData(
            title = "long title long title long title long title long title long title ",
            summary = "long summary long summary long summary long summary long summary ",
            enabled = true,
            singleLineTitle = true,
            singleLineSummary = true,
            setTrainingContent = true
        ),
    )
}

private data class TextPreferenceItemPreviewData(
    val title: String,
    val summary: String?,
    val enabled: Boolean,
    val singleLineTitle: Boolean = false,
    val singleLineSummary: Boolean = false,
    val setTrainingContent: Boolean = false,
)

@Preview
@Composable
private fun TextPreferenceItemPreview(
    @PreviewParameter(TextPreferenceItemPreviewParameterProvider::class) data: TextPreferenceItemPreviewData
) {
    BVMobileTheme {
        Surface {
            TextPreferenceItem(
                title = data.title,
                summary = data.summary,
                enabled = data.enabled,
                singleLineTitle = data.singleLineTitle,
                singleLineSummary = data.singleLineSummary,
                trailingContent = (@Composable {
                    Switch(checked = false, onCheckedChange = {})
                }).takeIf { data.setTrainingContent }
            )
        }
    }
}
