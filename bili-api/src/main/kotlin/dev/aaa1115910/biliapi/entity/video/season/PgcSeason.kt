package dev.aaa1115910.biliapi.entity.video.season

import dev.aaa1115910.biliapi.http.entity.season.OtherSeason

data class UgcSeason(
    val id: Int,
    val title: String,
    val cover: String,
    val sections: List<Section>
) {
    companion object {
        fun fromUgcSeason(ugcSeason: bilibili.app.view.v1.UgcSeason) = UgcSeason(
            id = ugcSeason.id.toInt(),
            title = ugcSeason.title,
            cover = ugcSeason.cover,
            sections = ugcSeason.sectionsList.map { Section.fromSection(it) }
        )

        fun fromUgcSeason(ugcSeason: dev.aaa1115910.biliapi.http.entity.video.UgcSeason) =
            UgcSeason(
                id = ugcSeason.id,
                title = ugcSeason.title,
                cover = ugcSeason.cover,
                sections = ugcSeason.sections.map { Section.fromSection(it) }
            )
    }
}

/**
 * 剧集信息
 *
 * @param seasonId 剧集id
 * @param title 剧集标题，仅 App 端
 * @param shortTitle 剧集短标题，用于 TabRow 处显示
 */
data class PgcSeason(
    val seasonId: Int,
    val title: String?,
    val shortTitle: String,
    val cover: String,
    val horizontalCover: String?
) {
    companion object {
        fun fromSeason(season: OtherSeason): PgcSeason {
            return PgcSeason(
                seasonId = season.seasonId,
                title = season.title,
                shortTitle = season.seasonTitle,
                cover = season.cover,
                horizontalCover = season.horizontalCover ?: season.newEp.cover
            )
        }
    }
}