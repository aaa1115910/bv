package dev.aaa1115910.biliapi.entity.anime

import kotlinx.serialization.Serializable

@Serializable
data class AnimeHomepageData(
    private val _dataV1: AnimeHomepageDataV1? = null,
    private val _dataV2: AnimeHomepageDataV2? = null
) {
    fun getCarouselItems(): List<CarouselItem> {
        val result = mutableListOf<CarouselItem>()
        _dataV1?.carouselList?.forEach {
            result.add(
                CarouselItem(
                    cover = it.img,
                    title = it.title,
                    episodeId = it.blink.split("play\\ep", "?from")[1].toIntOrNull()
                )
            )
        }
        _dataV2?.modules?.banner?.items?.forEach {
            result.add(
                CarouselItem(
                    cover = "https:${it.webpBigCover}",
                    title = it.title,
                    seasonId = it.seasonId
                )
            )
        }
        return result
    }
}

enum class AnimeHomepageDataType {
    V1, V2
}

data class CarouselItem(
    val cover: String,
    val title: String,
    val seasonId: Int? = null,
    val episodeId: Int? = null
)