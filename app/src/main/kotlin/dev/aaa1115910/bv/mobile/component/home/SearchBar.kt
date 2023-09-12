package dev.aaa1115910.bv.mobile.component.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.R

@Composable
fun HomeSearchTopBarCompact(
    modifier: Modifier = Modifier,
    query: String,
    active: Boolean,
    selectedTabIndex: Int,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onOpenNavDrawer: () -> Unit,
    onChangeTabIndex: (Int) -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(HomeTab.Recommend) }

    Box(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                HomeSearchBar(
                    query = query,
                    active = active,
                    onQueryChange = onQueryChange,
                    onActiveChange = onActiveChange,
                    onOpenNavDrawer = onOpenNavDrawer
                )
            }

            TabRow(selectedTabIndex = selectedTabIndex) {
                HomeTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            onChangeTabIndex(index)
                            currentTab = tab
                        },
                        text = {
                            Text(
                                text = tab.getDisplayName(context),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchTopBarExpanded(
    modifier: Modifier = Modifier,
    query: String,
    active: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
) {
    Box(
        modifier
            .semantics { isContainer = true }
            .zIndex(1f)
            .fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
        Row {
            DockedSearchBar(
                modifier = Modifier.padding(vertical = 3.dp),
                query = query,
                onQueryChange = { onQueryChange(it) },
                onSearch = { onActiveChange(false) },
                active = active,
                onActiveChange = { onActiveChange(it) },
                placeholder = { Text("Hinted search text") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            ) {

            }
            val titles = listOf("Tab 1", "Tab 2", "Tab 3 with lots of text")
            var state by remember { mutableStateOf(0) }
            TabRow(
                selectedTabIndex = state
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        text = {
                            Text(
                                text = title,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    active: Boolean,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onOpenNavDrawer: () -> Unit
) {
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { onActiveChange(false) },
        active = active,
        onActiveChange = onActiveChange,
        leadingIcon = {
            if (!active) {
                IconButton(onClick = onOpenNavDrawer) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                }
            } else {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            if (!active) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null)
            }
        },
    ) { }
}

enum class HomeTab(private val strRes: Int) {
    Recommend(R.string.home_tab_rcmd),
    Popular(R.string.home_tab_popular);

    fun getDisplayName(context: Context = BVApp.context) = context.getString(strRes)
}