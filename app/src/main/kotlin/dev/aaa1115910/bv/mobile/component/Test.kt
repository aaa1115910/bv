package dev.aaa1115910.bv.mobile.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Preview
@Composable
fun ListDetailPaneScaffoldSample() {
    BVMobileTheme {
        val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator()
        ListDetailPaneScaffold(
            directive = scaffoldNavigator.scaffoldDirective,
            value = scaffoldNavigator.scaffoldValue,
            listPane = {
                AnimatedPane(
                    modifier = Modifier.preferredWidth(200.dp),
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = {
                            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    ) {
                        Text("List")
                    }
                }
            },
            detailPane = {
                AnimatedPane(modifier = Modifier) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        onClick = {
                            scaffoldNavigator.navigateBack()
                        }
                    ) {
                        Text("Details")
                    }
                }
            }
        )
    }

}


@Preview
@Composable
private fun SettingPre() {
    BVMobileTheme {
        Surface {
            Settings()
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun Settings() {
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator()
    ListDetailPaneScaffold(
        directive = scaffoldNavigator.scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        listPane = {
            AnimatedPane(
                modifier = Modifier.preferredWidth(200.dp),
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    }
                ) {
                    Text("List")
                }
            }
        },
        detailPane = {
            AnimatedPane(modifier = Modifier) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    onClick = {
                        scaffoldNavigator.navigateBack()
                    }
                ) {
                    Text("Details")
                }
            }
        }
    )
}