package dev.aaa1115910.bv.screen.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import dev.aaa1115910.biliapi.BiliApi
import dev.aaa1115910.biliapi.entity.search.HotwordResponse
import dev.aaa1115910.biliapi.entity.search.KeywordSuggest
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.activities.search.SearchResultActivity
import dev.aaa1115910.bv.component.TvOutlinedTextFiled
import dev.aaa1115910.bv.component.search.SearchKeyword
import dev.aaa1115910.bv.component.search.SoftKeyboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var searchKeyword by remember { mutableStateOf("") }
    val hotwords = remember { mutableStateListOf<HotwordResponse.Hotword>() }
    val suggests = remember { mutableStateListOf<KeywordSuggest.Result.Tag>() }

    val onSearch: (String) -> Unit = { keyword ->
        SearchResultActivity.actionStart(context, keyword)
    }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.Default) {
            runCatching {
                val hotwordResponse = BiliApi.getHotwords()
                hotwords.clear()
                hotwords.addAll(hotwordResponse.list)
            }
        }
    }

    LaunchedEffect(searchKeyword) {
        scope.launch(Dispatchers.Default) {
            runCatching {
                val suggestResponse = BiliApi.getKeywordSuggest(term = searchKeyword)
                suggests.clear()
                suggests.addAll(suggestResponse.suggests)
            }
        }
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
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TvOutlinedTextFiled(
                        modifier = Modifier.width(258.dp),
                        value = searchKeyword,
                        onValueChange = { searchKeyword = it }
                    )
                    SoftKeyboard(
                        onClick = {
                            searchKeyword += it
                        },
                        onClear = {
                            searchKeyword = ""
                        },
                        onDelete = {
                            if (searchKeyword.isNotEmpty()) {
                                searchKeyword = searchKeyword.dropLast(1)
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
                                icon = hotword.icon,
                                onClick = {
                                    searchKeyword = hotword.showName
                                    onSearch(hotword.showName)
                                }
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
                                keyword = suggest.value,
                                icon = "",
                                onClick = {
                                    searchKeyword = suggest.value
                                    onSearch(suggest.value)
                                }
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
                Text(text = "待会写")
            }
        }
    }
}
