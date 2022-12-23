package dev.aaa1115910.bv.screen.settings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.screen.settings.SettingsMenuNavItem
import dev.aaa1115910.bv.ui.theme.BVTheme

@Composable
fun AboutSetting(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = SettingsMenuNavItem.About.getDisplayName(context),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.settings_version_current_version,
                        BuildConfig.VERSION_NAME
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.settings_version_latest_version, ""
                        )
                    )
                    AsyncImage(
                        modifier = Modifier
                            .height(20.dp)
                            .widthIn(max=200.dp),
                        model = ImageRequest.Builder(context)
                            .data("https://img.shields.io/github/v/tag/aaa1115910/bv?label=Version")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        }
        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            text = "https://github.com/aaa1115910/bv"
        )
    }
}

@Preview
@Composable
private fun AboutSettingPreview() {
    BVTheme {
        AboutSetting()
    }
}