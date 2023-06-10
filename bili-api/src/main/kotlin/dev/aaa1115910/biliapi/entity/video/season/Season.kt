package dev.aaa1115910.biliapi.entity.video.season

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
