package dev.aaa1115910.bv.screen.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.search.SearchResultActivity
import dev.aaa1115910.bv.component.createCustomInitialFocusRestorerModifiers
import dev.aaa1115910.bv.component.ifElse
import dev.aaa1115910.bv.component.search.SearchKeyword
import dev.aaa1115910.bv.component.search.SoftKeyboard
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.search.SearchInputViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchInputScreen(
    modifier: Modifier = Modifier,
    defaultFocusRequester: FocusRequester,
    searchInputViewModel: SearchInputViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val hotsFocusRestorerModifiers = createCustomInitialFocusRestorerModifiers()
    val historyFocusRestorerModifiers = createCustomInitialFocusRestorerModifiers()
    val suggestFocusRestorerModifiers = createCustomInitialFocusRestorerModifiers()

    val searchKeyword = searchInputViewModel.keyword
    val hotwords = searchInputViewModel.hotwords
    val searchHistories = searchInputViewModel.searchHistories
    val suggests = searchInputViewModel.suggests

    var enableProxy by remember { mutableStateOf(false) }

    val onSearch: (String) -> Unit = { keyword ->
        SearchResultActivity.actionStart(context, keyword, enableProxy)
        searchInputViewModel.keyword = keyword
        searchInputViewModel.addSearchHistory(keyword)
    }

    LaunchedEffect(searchKeyword) {
        searchInputViewModel.updateSuggests()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.search_input_title),
                        fontSize = 48.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = 8.dp)
                .padding(start = 24.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.width(258.dp),
                        value = searchKeyword,
                        onValueChange = { searchInputViewModel.keyword = it },
                        maxLines = 1,
                        shape = MaterialTheme.shapes.large,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearch(searchKeyword) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                            cursorColor = MaterialTheme.colorScheme.inverseSurface
                        )
                    )
                    SoftKeyboard(
                        firstButtonFocusRequester = defaultFocusRequester,
                        showSearchWithProxy = Prefs.enableProxy,
                        enableSearchWithProxy = enableProxy,
                        onClick = {
                            searchInputViewModel.keyword += it
                        },
                        onClear = {
                            searchInputViewModel.keyword = ""
                        },
                        onDelete = {
                            if (searchKeyword.isNotEmpty()) {
                                searchInputViewModel.keyword = searchKeyword.dropLast(1)
                            }
                        },
                        onSearch = {
                            onSearch(searchKeyword)
                        },
                        onEnableSearchWithProxyChange = {
                            enableProxy = it
                        }
                    )
                }
            }

            if (searchKeyword.isEmpty()) {
                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .fillMaxHeight(),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = stringResource(R.string.search_input_hotword),
                        style = MaterialTheme.typography.titleLarge
                    )
                    LazyColumn(
                        modifier = Modifier
                            .then(hotsFocusRestorerModifiers.parentModifier)
                    ) {
                        itemsIndexed(hotwords) { index, hotword ->
                            SearchKeyword(
                                modifier = Modifier
                                    .ifElse(index == 0, hotsFocusRestorerModifiers.childModifier),
                                keyword = hotword.showName,
                                icon = hotword.icon ?: "",
                                onClick = { onSearch(hotword.showName) }
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .fillMaxHeight(),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = stringResource(R.string.search_input_suggest),
                        style = MaterialTheme.typography.titleLarge
                    )
                    LazyColumn(
                        modifier = Modifier
                            .then(suggestFocusRestorerModifiers.parentModifier)
                    ) {
                        itemsIndexed(suggests) { index, suggest ->
                            SearchKeyword(
                                modifier = Modifier
                                    .ifElse(
                                        index == 0,
                                        suggestFocusRestorerModifiers.childModifier
                                    ),
                                keyword = suggest,
                                icon = "",
                                onClick = { onSearch(suggest) }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .padding(end = 10.dp),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    text = stringResource(R.string.search_input_history),
                    style = MaterialTheme.typography.titleLarge
                )
                LazyColumn(
                    modifier = Modifier
                        .then(historyFocusRestorerModifiers.parentModifier)
                ) {
                    itemsIndexed(searchHistories) { index, searchHistory ->
                        SearchKeyword(
                            modifier = Modifier
                                .ifElse(index == 0, historyFocusRestorerModifiers.childModifier),
                            keyword = searchHistory.keyword,
                            icon = "",
                            onClick = { onSearch(searchHistory.keyword) }
                        )
                    }
                }
            }
        }
    }
}
