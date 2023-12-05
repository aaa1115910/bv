package dev.aaa1115910.bv.viewmodel.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.aaa1115910.biliapi.entity.season.IndexResultItem
import dev.aaa1115910.biliapi.entity.season.IndexResultPage
import dev.aaa1115910.biliapi.http.entity.index.IndexOrder
import dev.aaa1115910.biliapi.repositories.IndexRepository
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.util.fError
import dev.aaa1115910.bv.util.toast
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeIndexViewModel(
    private val indexRepository: IndexRepository
) : ViewModel() {
    companion object {
        private val logger = KotlinLogging.logger { }
    }

    val indexResultItems = mutableStateListOf<IndexResultItem>()

    private var updating = false
    private var nextPage = IndexResultPage()
    val noMore get() = nextPage.hasNext.not()

    var order by mutableStateOf(IndexOrder.FollowCount)
    var seasonVersion by mutableIntStateOf(-1)
    var spokenLanguageType by mutableIntStateOf(-1)
    var area by mutableIntStateOf(-1)
    var isFinish by mutableIntStateOf(-1)
    var copyright by mutableIntStateOf(-1)
    var seasonStatus by mutableIntStateOf(-1)
    var seasonMonth by mutableIntStateOf(-1)
    var year by mutableStateOf("-1")
    var styleId by mutableIntStateOf(-1)
    var desc by mutableStateOf(true)

    suspend fun loadMore() {
        if (!updating) loadData()
    }

    private suspend fun loadData() {
        updating = true
        if (!nextPage.hasNext) {
            updating = false
            return
        }
        runCatching {
            val result = indexRepository.getAnimeIndex(
                sort = order,
                seasonVersion = seasonVersion,
                spokenLanguageType = spokenLanguageType,
                area = area,
                isFinish = isFinish,
                copyright = copyright,
                seasonStatus = seasonStatus,
                seasonMonth = seasonMonth,
                year = year,
                styleId = styleId,
                desc = desc,
                page = nextPage
            )
            indexResultItems.addAll(result.list)
            nextPage = result.nextPage
        }.onFailure {
            logger.fError { "Load anime index list failed: ${it.stackTraceToString()}" }
            withContext(Dispatchers.Main) {
                "加载番剧索引失败: ${it.localizedMessage}".toast(BVApp.context)
            }
        }
        updating = false
    }

    fun clearData() {
        indexResultItems.clear()
        nextPage = IndexResultPage()
        updating = false
    }
}