package dev.aaa1115910.bv.tv.manager

import dev.aaa1115910.biliapi.entity.FavoriteFolderMetadata
import dev.aaa1115910.biliapi.repositories.CoinRepository
import dev.aaa1115910.biliapi.repositories.FavoriteRepository
import dev.aaa1115910.biliapi.repositories.LikeRepository
import dev.aaa1115910.bv.util.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get

data class VideoActionState(
    val liked: Boolean = false,
    val favorited: Boolean = false,
    val coin: Boolean = false,
    val favoriteFolderIds: List<Long> = emptyList(),
    val favoriteFolders: List<FavoriteFolderMetadata> = emptyList()
)

/**
 * Simple in-memory manager for video user actions (like/favorite/coin).
 * Keyed by aid. Exposes a StateFlow per aid so UI can collect and share state across screens.
 * Network operations delegate to repositories from Koin and update the corresponding flow on success.
 */
object VideoUserActionManager {
    // key = Pair(uid, aid)
    private val stateMap = mutableMapOf<Pair<Long, Long>, MutableStateFlow<VideoActionState>>()

    private fun key(uid: Long, aid: Long) = uid to aid

    private fun ensure(aid: Long, uid: Long = Prefs.uid): MutableStateFlow<VideoActionState> {
        val k = key(uid, aid)
        return stateMap.getOrPut(k) { MutableStateFlow(VideoActionState()) }
    }

    fun getStateFlow(aid: Long, uid: Long = Prefs.uid): StateFlow<VideoActionState> = ensure(aid, uid)

    fun updateFromLoadedData(aid: Long, liked: Boolean, favorited: Boolean, coin: Boolean, uid: Long = Prefs.uid) {
        val flow = ensure(aid, uid)
        flow.value = flow.value.copy(liked = liked, favorited = favorited, coin = coin)
    }

    suspend fun fetchFavoriteData(aid: Long, uid: Long = Prefs.uid) {
        if (aid <= 0) return
        val favoriteRepository: FavoriteRepository = get(FavoriteRepository::class.java)
        try {
            val list = withContext(Dispatchers.IO) {
                favoriteRepository.getAllFavoriteFolderMetadataList(
                    mid = uid,
                    rid = aid,
                    preferApiType = Prefs.apiType
                )
            }
            ensure(aid, uid).value = ensure(aid, uid).value.copy(
                favoriteFolders = list,
                favoriteFolderIds = list.filter { it.videoInThisFav }.map { it.id }
            )
        } catch (_: Exception) {
            // ignore
        }
    }

    suspend fun addLike(aid: Long, uid: Long = Prefs.uid): Boolean {
        if (aid <= 0) return false
        val likeRepository: LikeRepository = get(LikeRepository::class.java)
        return try {
            withContext(Dispatchers.IO) { likeRepository.addVideoLike(aid = aid) }
            ensure(aid, uid).value = ensure(aid, uid).value.copy(liked = true)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun delLike(aid: Long, uid: Long = Prefs.uid): Boolean {
        if (aid <= 0) return false
        val likeRepository: LikeRepository = get(LikeRepository::class.java)
        return try {
            withContext(Dispatchers.IO) { likeRepository.delVideoLike(aid = aid) }
            ensure(aid, uid).value = ensure(aid, uid).value.copy(liked = false)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun addCoin(aid: Long, uid: Long = Prefs.uid): Boolean {
        if (aid <= 0) return false
        val coinRepository: CoinRepository = get(CoinRepository::class.java)
        return try {
            withContext(Dispatchers.IO) { coinRepository.addVideoCoin(aid = aid) }
            ensure(aid, uid).value = ensure(aid, uid).value.copy(coin = true)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun updateVideoFavoriteFolders(aid: Long, folderIds: List<Long>, uid: Long = Prefs.uid): Boolean {
        if (aid <= 0) return false
        val favoriteRepository: FavoriteRepository = get(FavoriteRepository::class.java)
        val currentFolders = ensure(aid, uid).value.favoriteFolders
        return try {
            withContext(Dispatchers.IO) {
                require(currentFolders.isNotEmpty())
                favoriteRepository.updateVideoToFavoriteFolder(
                    aid = aid,
                    addMediaIds = folderIds,
                    delMediaIds = currentFolders.map { it.id } - folderIds.toSet()
                )
            }
            ensure(aid, uid).value = ensure(aid, uid).value.copy(
                favoriteFolderIds = folderIds,
                favorited = folderIds.isNotEmpty()
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun addToDefaultFavoriteFolder(aid: Long, uid: Long = Prefs.uid): Boolean {
        if (aid <= 0) return false
        val flow = ensure(aid, uid)
        val default = flow.value.favoriteFolders.firstOrNull { it.title == "默认收藏夹" }
            ?: return false
        return updateVideoFavoriteFolders(aid, listOf(default.id), uid)
    }
}
