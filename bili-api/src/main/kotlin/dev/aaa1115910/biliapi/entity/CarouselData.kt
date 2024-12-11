package dev.aaa1115910.biliapi.entity

import dev.aaa1115910.biliapi.util.UrlUtil
import dev.aaa1115910.biliapi.util.toBv
import io.ktor.http.Url

data class CarouselData(
    val items: List<CarouselItem>
) {
    companion object {
        fun fromPgcWebInitialStateData(data: dev.aaa1115910.biliapi.http.entity.pgc.PgcWebInitialStateData): CarouselData {
            val result = mutableListOf<CarouselItem>()
            var isMovie = false
            // 电影板块里的轮播图数据里没有直接包含 episodeId 和 seasonId
            if (data.modules.banner.moduleId == 1668) isMovie = true
            data.modules.banner.items.filter {
                it.episodeId != null || (isMovie && it.link.contains("bangumi/play/ep"))
            }.forEach {
                var cover = it.bigCover ?: it.cover
                if (cover.startsWith("//")) cover = "https:$cover"
                result.add(
                    CarouselItem(
                        cover = cover,
                        title = it.title,
                        seasonId = it.seasonId ?: -1,
                        episodeId = it.episodeId
                            ?: Url(it.link).rawSegments.last().substring(2).toInt()
                    )
                )
            }
            return CarouselData(result)
        }

        fun fromUgcRegionDynamicBanner(data: dev.aaa1115910.biliapi.http.entity.region.RegionDynamic.Banner): CarouselData {
            val result = mutableListOf<CarouselItem>()
            data.top.forEach { top ->
                if (!UrlUtil.isVideoUrl(top.uri)) return@forEach
                val avid = UrlUtil.parseAidFromUrl(top.uri)
                val bvid = avid.toBv()
                result.add(
                    CarouselItem(
                        cover = top.image,
                        title = top.title,
                        avid = avid,
                        bvid = bvid
                    )
                )
            }
            return CarouselData(result)
        }

        fun fromUgcRegionLocs(data: dev.aaa1115910.biliapi.http.entity.region.RegionLocs): CarouselData {
            val result = mutableListOf<CarouselItem>()
            data.data.forEach { (_, value) ->
                value.filter { it.url.contains("/video/") }.forEach { item ->
                    result.add(
                        CarouselItem(
                            cover = item.pic,
                            title = item.title,
                            bvid = Url(item.url).rawSegments.last()
                        )
                    )
                }
            }
            return CarouselData(result)
        }
    }

    data class CarouselItem(
        val cover: String,
        val title: String,
        val seasonId: Int? = null,
        val episodeId: Int? = null,
        val avid: Long? = null,
        val bvid: String? = null
    )
}
