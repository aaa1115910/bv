package dev.aaa1115910.bv.player.entity

interface VideoListItem

open class VideoListItemData(
    open val aid: Long,
    open val cid: Long,
    open val epid: Int? = null,
    open val seasonId: Int? = null,
    open val title: String,
    open val partTitle: String = "",
    open val index: Int,
) : VideoListItem

data class VideoListPart(
    override val aid: Long,
    override val cid: Long,
    override val epid: Int? = null,
    override val seasonId: Int? = null,
    override val title: String,
    override val partTitle: String = "",
    override val index: Int,
) : VideoListItemData(aid, cid, epid, seasonId, title, partTitle, index)

data class VideoListUgcEpisode(
    override val aid: Long,
    override val cid: Long,
    override val epid: Int? = null,
    override val seasonId: Int? = null,
    override val title: String,
    override val partTitle: String = "",
    override val index: Int,
) : VideoListItemData(aid, cid, epid, seasonId, title, partTitle, index)

data class VideoListUgcEpisodeTitle(
    val index: Int,
    val title: String
) : VideoListItem

data class VideoListPgcEpisode(
    override val aid: Long,
    override val cid: Long,
    override val epid: Int? = null,
    override val seasonId: Int? = null,
    override val title: String,
    override val partTitle: String = "",
    override val index: Int,
) : VideoListItemData(aid, cid, epid, seasonId, title, partTitle, index)