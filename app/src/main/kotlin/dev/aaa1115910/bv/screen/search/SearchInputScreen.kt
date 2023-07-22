package dev.aaa1115910.bv.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.search.SearchResultActivity
import dev.aaa1115910.bv.component.TvOutlinedTextFiled
import dev.aaa1115910.bv.component.search.SearchKeyword
import dev.aaa1115910.bv.component.search.SoftKeyboard
import dev.aaa1115910.bv.viewmodel.search.SearchInputViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchInputScreen(
    modifier: Modifier = Modifier,
    searchInputViewModel: SearchInputViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val softKeyboardFirstButtonFocusRequester = remember { FocusRequester() }

    val searchKeyword = searchInputViewModel.keyword
    val hotwords = searchInputViewModel.hotwords
    val searchHistories = searchInputViewModel.searchHistories
    val suggests = searchInputViewModel.suggests

    val onSearch: (String) -> Unit = { keyword ->
        SearchResultActivity.actionStart(context, keyword)
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
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TvOutlinedTextFiled(
                        modifier = Modifier.width(258.dp),
                        value = searchKeyword,
                        onValueChange = { searchInputViewModel.keyword = it },
                        onPressEnter = { onSearch(searchKeyword) },
                        onMoveFocusToDown = { softKeyboardFirstButtonFocusRequester.requestFocus() },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearch(searchKeyword) })
                    )
                    SoftKeyboard(
                        firstButtonFocusRequester = softKeyboardFirstButtonFocusRequester,
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
                        onSearch = { onSearch(searchKeyword) }
                    )
                }
            }

            if (searchKeyword.isEmpty()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = stringResource(R.string.search_input_hotword),
                        style = MaterialTheme.typography.titleLarge
                    )
                    TvLazyColumn {
                        items(hotwords) { hotword ->
                            SearchKeyword(
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
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = stringResource(R.string.search_input_suggest),
                        style = MaterialTheme.typography.titleLarge
                    )
                    TvLazyColumn {
                        items(suggests) { suggest ->
                            SearchKeyword(
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
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    text = stringResource(R.string.search_input_history),
                    style = MaterialTheme.typography.titleLarge
                )
                TvLazyColumn {
                    items(searchHistories) { searchHistory ->
                        SearchKeyword(
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
