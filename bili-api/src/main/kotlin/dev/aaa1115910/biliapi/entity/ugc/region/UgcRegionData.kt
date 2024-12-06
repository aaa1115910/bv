package dev.aaa1115910.biliapi.entity.ugc.region

import dev.aaa1115910.biliapi.entity.CarouselData
import dev.aaa1115910.biliapi.entity.ugc.UgcItem

data class UgcRegionData(
    val carouselData: CarouselData?,
    val items: List<UgcItem>,
    val next: UgcRegionPage
) {
    companion object {
        fun fromRegionDynamic(data: dev.aaa1115910.biliapi.http.entity.region.RegionDynamic): UgcRegionData {
            return UgcRegionData(
                carouselData = data.banner?.let { CarouselData.fromUgcRegionDynamicBanner(it) },
                items = data.new.map { UgcItem.fromRegionDynamicListItem(it) },
                next = UgcRegionPage(data.cBottom)
            )
        }
    }
}