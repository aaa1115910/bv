package dev.aaa1115910.biliapi.entity.ugc.region

import dev.aaa1115910.biliapi.entity.ugc.UgcItem

data class UgcRegionListData(
    val items: List<UgcItem>,
    val next: UgcRegionPage
) {
    companion object {
        fun fromRegionDynamicList(data: dev.aaa1115910.biliapi.http.entity.region.RegionDynamicList): UgcRegionListData {
            return UgcRegionListData(
                items = data.new.map { UgcItem.fromRegionDynamicListItem(it) },
                next = UgcRegionPage(data.cBottom)
            )
        }
    }
}