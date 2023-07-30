package dev.aaa1115910.biliapi.http.entity.dynamic

import dev.aaa1115910.biliapi.http.entity.user.Pendant
import dev.aaa1115910.biliapi.http.entity.user.Vip
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DynamicData(
    @SerialName("has_more")
    val hasMore: Boolean,
    val offset: String,
    @SerialName("update_baseline")
    val updateBaseline: String,
    @SerialName("update_num")
    val updateNum: Int,
    val items: List<DynamicItem> = emptyList()
)

@Serializable
data class DynamicItem(
    val basic: Basic,
    @SerialName("id_str")
    val idStr: String,
    val modules: Modules,
    val type: String,
    val visible: Boolean
) {
    @Serializable
    data class Basic(
        @SerialName("comment_id_str")
        val commentIdStr: String,
        @SerialName("comment_type")
        val commentType: Int,
        @SerialName("like_icon")
        val likeIcon: LikeIcon,
        @SerialName("rid_str")
        val ridStr: String
    ) {
        @Serializable
        data class LikeIcon(
            @SerialName("action_url")
            val actionUrl: String,
            @SerialName("end_url")
            val endUrl: String,
            val id: Int,
            @SerialName("start_url")
            val startUrl: String
        )
    }

    @Serializable
    data class Modules(
        @SerialName("module_author")
        val moduleAuthor: Author,
        @SerialName("module_dynamic")
        val moduleDynamic: Dynamic,
        @SerialName("module_more")
        val moduleMore: More,
        @SerialName("module_stat")
        val moduleStat: Stat
    ) {
        @Serializable
        data class Author(
            val face: String,
            @SerialName("face_nft")
            val faceNft: Boolean,
            val following: Boolean = false,
            @SerialName("jump_url")
            val jumpUrl: String,
            val label: String,
            val mid: Long,
            val name: String,
            @SerialName("official_verify")
            val officialVerify: OfficialVerify,
            val pendant: Pendant,
            @SerialName("pub_action")
            val pubAction: String,
            @SerialName("pub_location_text")
            val pubLocationText: String,
            @SerialName("pub_time")
            val pubTime: String,
            @SerialName("pub_ts")
            val pubTs: Int,
            val type: String,
            val vip: Vip
        ) {
            @Serializable
            data class OfficialVerify(
                val desc: String,
                val type: Int
            )
        }

        @Serializable
        data class Dynamic(
            val additional: Additional? = null,
            val desc: Desc? = null,
            val major: Major? = null,
            val topic: Topic? = null
        ) {
            @Serializable
            data class Additional(
                val common: Common? = null,
                val reserve: Reserve? = null,
                val type: String
            ) {
                @Serializable
                data class Common(
                    val button: Button,
                    val cover: String,
                    val desc1: String,
                    val desc2: String,
                    @SerialName("head_text")
                    val headText: String,
                    @SerialName("id_str")
                    val idStr: String,
                    @SerialName("jump_url")
                    val jumpUrl: String,
                    val style: Int,
                    @SerialName("sub_type")
                    val subType: String,
                    val title: String
                )
            }

            @Serializable
            data class Button(
                val check: ButtonItem? = null,
                val status: Int? = null,
                val type: Int,
                val uncheck: ButtonItem? = null,
                @SerialName("jump_style")
                val jumpStyle: ButtonItem? = null,
                @SerialName("jump_url")
                val jumpUrl: String? = null
            ) {
                @Serializable
                data class ButtonItem(
                    @SerialName("icon_url")
                    val iconUrl: String? = null,
                    val text: String
                )
            }

            @Serializable
            data class Reserve(
                val button: Button,
                val desc1: Desc,
                val desc2: Desc,
                @SerialName("jump_url")
                val jumpUrl: String,
                @SerialName("reserve_total")
                val reserveTotal: Int,
                val rid: Int,
                val state: Int,
                val stypc: Int,
                val title: String,
                @SerialName("up_mid")
                val upMid: Int
            ) {
                @Serializable
                data class Desc(
                    val style: Int,
                    val text: String,
                    val visible: Boolean
                )
            }

            @Serializable
            data class Desc(
                @SerialName("rich_text_nodes")
                val richTextNodes: List<RichTextNodeItem>,
                val text: String
            ) {
                @Serializable
                data class RichTextNodeItem(
                    val emoji: Emoji? = null,
                    @SerialName("orig_text")
                    val origText: String,
                    val text: String,
                    val type: String
                ) {
                    @Serializable
                    data class Emoji(
                        @SerialName("icon_url")
                        val iconUrl: String,
                        val size: Int,
                        val text: String,
                        val type: Int
                    )
                }
            }

            @Serializable
            data class Major(
                val archive: Archive? = null,
                @SerialName("live_rcmd")
                val liveRcmd: LiveRcmd? = null,
                val type: String
            ) {
                @Serializable
                data class Archive(
                    val aid: String,
                    val badge: Badge,
                    val bvid: String,
                    val cover: String,
                    val desc: String,
                    @SerialName("disable_preview")
                    val disablePreview: Int,
                    @SerialName("duration_text")
                    val durationText: String,
                    @SerialName("jump_url")
                    val jumpUrl: String,
                    val stat: Stat,
                    val title: String,
                    val type: Int
                ) {
                    @Serializable
                    data class Badge(
                        @SerialName("bg_color")
                        val bgColor: String,
                        val color: String,
                        val text: String
                    )

                    @Serializable
                    data class Stat(
                        val danmaku: String,
                        val play: String
                    )
                }

                @Serializable
                data class LiveRcmd(
                    val content: String,
                    @SerialName("reserve_type")
                    val reserveType: Int
                )
            }

            @Serializable
            data class Topic(
                val id: Int,
                @SerialName("jump_url")
                val jumpUrl: String,
                val name: String
            )
        }

        @Serializable
        data class More(
            @SerialName("three_point_items")
            val threePointItems: List<MoreItem> = emptyList()
        ) {
            @Serializable
            data class MoreItem(
                val label: String,
                val type: String
            )
        }

        @Serializable
        data class Stat(
            val comment: StatItem,
            val forward: StatItem,
            val like: StatItem
        ) {
            @Serializable
            data class StatItem(
                val count: Int,
                val forbidden: Boolean,
                val statue: Boolean = false
            )
        }
    }
}