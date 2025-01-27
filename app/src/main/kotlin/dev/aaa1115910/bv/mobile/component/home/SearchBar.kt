package dev.aaa1115910.bv.mobile.component.home

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme

@Composable
fun HomeSearchTopBarCompact(
    modifier: Modifier = Modifier,
    query: String,
    expanded: Boolean,
    selectedTabIndex: Int,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onOpenNavDrawer: () -> Unit,
    onChangeTabIndex: (Int) -> Unit,
    onSwitchUser: () -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(HomeTab.Recommend) }

    val searchBarHorizontalPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        label = "search bar horizontal padding"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = searchBarHorizontalPadding)
                .zIndex(2f),
            horizontalArrangement = Arrangement.Center
        ) {
            HomeSearchBar(
                modifier = Modifier.fillMaxWidth(),
                query = query,
                expanded = expanded,
                onQueryChange = onQueryChange,
                onExpandedChange = onExpandedChange,
                onOpenNavDrawer = onOpenNavDrawer,
                onSwitchUser = onSwitchUser,
                onSearch = {}
            )
        }

        TabRow(
            modifier = Modifier
                .padding(top = 100.dp)
                .zIndex(1f),
            selectedTabIndex = selectedTabIndex
        ) {
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
            .semantics { isTraversalGroup = true }
            .zIndex(1f)
            .fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.surface)
        )
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.Top
        ) {
            DockedSearchBar(
                modifier = Modifier.padding(vertical = 3.dp, horizontal = 16.dp),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = {},
                        expanded = active,
                        onExpandedChange = onActiveChange
                    )
                },
                expanded = active,
                onExpandedChange = onActiveChange
            ) {
                Text("???")
            }

            val titles = listOf("Tab 1", "Tab 2", "Tab 3 with lots of text")
            var state by remember { mutableStateOf(0) }

            Box(
                modifier = Modifier
                    .height(62.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    expanded: Boolean,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onOpenNavDrawer: () -> Unit,
    onSwitchUser: () -> Unit,
    onSearch: (String) -> Unit,
) {
    SearchBar(
        modifier = modifier,
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {
                    onSearch(it)
                    onExpandedChange(false)
                },
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                leadingIcon = {
                    if (!expanded) {
                        IconButton(onClick = onOpenNavDrawer) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                        }
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {
                    if (!expanded) {
                        IconButton(onClick = onSwitchUser) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        }
                    }
                },
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) { }
}

enum class HomeTab(private val strRes: Int) {
    Recommend(R.string.home_tab_rcmd),
    Popular(R.string.home_tab_popular);

    fun getDisplayName(context: Context = BVApp.context) = context.getString(strRes)
}

@Preview
@Composable
private fun HomeSearchTopBarCompactPreview() {
    var query by remember { mutableStateOf("") }
    val active by remember { derivedStateOf { query != "" } }

    BVMobileTheme {
        Column {
            HomeSearchTopBarCompact(
                query = query,
                expanded = false,
                selectedTabIndex = 0,
                onQueryChange = { query = it },
                onExpandedChange = { },
                onOpenNavDrawer = { },
                onChangeTabIndex = { },
                onSwitchUser = { }
            )

            Text(text = "query: $query")
            Text(text = "active: $active")

        }

    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun HomeSearchTopBarExpandedPreview() {
    BVMobileTheme {
        HomeSearchTopBarExpanded(
            query = "Search",
            active = false,
            onQueryChange = { },
            onActiveChange = { },
        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Demo(modifier: Modifier = Modifier) {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                repeat(40) { idx ->
                    val resultText = "Suggestion $idx"
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = { Text("Additional info") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier =
                        Modifier
                            .clickable {
                                text = resultText
                                expanded = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics { traversalIndex = 1f },
        ) {
            val list = List(100) { "Text $it" }
            items(count = list.size) {
                Text(
                    text = list[it],
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}