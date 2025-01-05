package dev.aaa1115910.biliapi.http.entity.reply

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class CommentData(
    val assist: Int,
    val blacklist: Int,
    val callbacks: JsonElement? = null,
    @SerialName("cm_info")
    val cmInfo: CmInfo? = null,
    val config: Config,
    val control: Control,
    val cursor: Cursor,
    val effects: Effects,
    @SerialName("esports_grade_card")
    val esportsGradeCard: JsonElement? = null,
    val note: Int,
    val replies: List<Reply> = emptyList(),
    val top: Top,
    @SerialName("top_replies")
    val topReplies: JsonElement? = null,
    @SerialName("up_selection")
    val upSelection: UpSelection,
    val upper: Upper,
    val vote: Int
) {
    /**
     * 广告控制
     */
    @Serializable
    data class CmInfo(
        val ads: JsonElement
    )

    /**
     * 游标信息
     */
    @Serializable
    data class Cursor(
        @SerialName("all_count")
        val allCount: Int? = null,
        @SerialName("is_begin")
        val isBegin: Boolean,
        @SerialName("is_end")
        val isEnd: Boolean,
        val mode: Int,
        @SerialName("mode_text")
        val modeText: String,
        val name: String,
        val next: Int,
        @SerialName("pagination_reply")
        val paginationReply: PaginationReply? = null,
        val prev: Int,
        @SerialName("session_id")
        val sessionId: String,
        @SerialName("support_mode")
        val supportMode: List<Int> = emptyList()
    ) {
        @Serializable
        data class PaginationReply(
            @SerialName("next_offset")
            val nextOffset: String? = null
        )
    }

    @Serializable
    data class Effects(
        val preloading: String
    )

    @Serializable
    data class Reply(
        val action: Int,
        val assist: Int,
        val attr: Long,
        val content: Content,
        val count: Int,
        val ctime: Int,
        val dialog: Long,
        @SerialName("dynamic_id_str")
        val dynamicIdStr: String,
        val fansgrade: Int,
        val folder: Folder,
        val invisible: Boolean,
        val like: Int,
        val member: Member,
        val mid: Long,
        val oid: Long,
        val parent: Long,
        @SerialName("parent_str")
        val parentStr: String,
        val rcount: Int,
        val replies: List<Reply> = emptyList(),
        @SerialName("reply_control")
        val replyControl: ReplyControl,
        val root: Long,
        @SerialName("root_str")
        val rootStr: String,
        val rpid: Long,
        @SerialName("rpid_str")
        val rpidStr: String,
        val state: Int,
        val type: Long,
        @SerialName("up_action")
        val upAction: UpAction
    ) {
        /**
         * 评论内容
         */
        @Serializable
        data class Content(
            @SerialName("at_name_to_mid")
            val atNameToMid: Map<String, Long> = emptyMap(),
            val emote: Map<String, Emote> = emptyMap(),
            @SerialName("jump_url")
            val jumpUrl: Map<String, JumpUrl> = emptyMap(),
            @SerialName("max_line")
            val maxLine: Int,
            val members: List<Member> = emptyList(),
            val message: String,
            val pictures: List<Picture> = emptyList(),
            @SerialName("picture_scale")
            val pictureScale: Float = 1f
        ) {
            /**
             * 评论中的表情
             */
            @Serializable
            data class Emote(
                val attr: Int,
                val id: Int,
                @SerialName("jump_title")
                val jumpTitle: String,
                val meta: Meta,
                val mtime: Int,
                @SerialName("package_id")
                val packageId: Int,
                val state: Int,
                val text: String,
                val type: Int,
                val url: String
            ) {
                @Serializable
                data class Meta(
                    val size: Int
                )
            }

            /**
             * 超链接跳转，例如关键词之类的
             */
            @Serializable
            data class JumpUrl(
                @SerialName("app_name")
                val appName: String,
                @SerialName("app_package_name")
                val appPackageName: String,
                @SerialName("app_url_schema")
                val appUrlSchema: String,
                @SerialName("click_report")
                val clickReport: String,
                @SerialName("exposure_report")
                val exposureReport: String,
                val extra: Extra? = null,
                @SerialName("icon_position")
                val iconPosition: Int,
                @SerialName("is_half_screen")
                val isHalfScreen: Boolean,
                @SerialName("match_once")
                val matchOnce: Boolean,
                @SerialName("pc_url")
                val pcUrl: String,
                @SerialName("prefix_icon")
                val prefixIcon: String,
                val state: Int,
                val title: String,
                val underline: Boolean
            ) {
                @Serializable
                data class Extra(
                    @SerialName("goods_click_report")
                    val goodsClickReport: String,
                    @SerialName("goods_cm_control")
                    val goodsCmControl: Int,
                    @SerialName("goods_exposure_report")
                    val goodsExposureReport: String,
                    @SerialName("goods_show_type")
                    val goodsShowType: Int,
                    @SerialName("is_word_search")
                    val isWordSearch: Boolean
                )
            }

            @Serializable
            data class Picture(
                @SerialName("img_src")
                val imgSrc: String,
                @SerialName("img_width")
                val imgWidth: Int,
                @SerialName("img_height")
                val imgHeight: Int,
                @SerialName("img_size")
                val imgSize: Float,
                @SerialName("top_right_icon")
                val topRightIcon: String? = null,
                @SerialName("play_gif_thumbnail")
                val playGifThumbnail: Boolean? = null
            )
        }
    }

    /**
     * 评论折叠信息
     */
    @Serializable
    data class Folder(
        @SerialName("has_folded")
        val hasFolded: Boolean,
        @SerialName("is_folded")
        val isFolded: Boolean,
        val rule: String
    )

    @Serializable
    data class Member(
        val avatar: String,
        @SerialName("avatar_item")
        val avatarItem: AvatarItem? = null,
        @SerialName("contract_desc")
        val contractDesc: String? = null,
        @SerialName("face_nft_new")
        val faceNftNew: Int,
        @SerialName("fans_detail")
        val fansDetail: JsonElement? = null,
        @SerialName("is_contractor")
        val isContractor: Boolean? = null,
        @SerialName("is_senior_member")
        val isSeniorMember: Int,
        @SerialName("level_info")
        val levelInfo: LevelInfo,
        val mid: String,
        val nameplate: Nameplate,
        @SerialName("nft_interaction")
        val nftInteraction: NftInteraction? = null,
        @SerialName("official_verify")
        val officialVerify: OfficialVerify,
        val pendant: Pendant,
        val rank: String,
        val senior: Senior,
        val sex: String,
        val sign: String,
        val uname: String,
        @SerialName("user_sailing")
        val userSailing: UserSailing? = null,
        val vip: Vip
    ) {
        @Serializable
        data class AvatarItem(
            @SerialName("container_size")
            val containerSize: ContainerSize,
            @SerialName("fallback_layers")
            val fallbackLayers: FallbackLayers,
            val mid: String
        ) {
            @Serializable
            data class ContainerSize(
                val height: Double,
                val width: Double
            )
        }

        @Serializable
        data class LevelInfo(
            @SerialName("current_exp")
            val currentExp: Int,
            @SerialName("current_level")
            val currentLevel: Int,
            @SerialName("current_min")
            val currentMin: Int,
            @SerialName("next_exp")
            val nextExp: Int
        )

        @Serializable
        data class Nameplate(
            val condition: String,
            val image: String,
            @SerialName("image_small")
            val imageSmall: String,
            val level: String,
            val name: String,
            val nid: Int
        )

        @Serializable
        data class NftInteraction(
            val region: Region
        ) {
            @Serializable
            data class Region(
                val icon: String,
                @SerialName("show_status")
                val showStatus: Int,
                val type: Int
            )
        }

        @Serializable
        data class OfficialVerify(
            val desc: String,
            val type: Int
        )

        @Serializable
        data class Pendant(
            val expire: Int,
            val image: String,
            @SerialName("image_enhance")
            val imageEnhance: String,
            @SerialName("image_enhance_frame")
            val imageEnhanceFrame: String,
            val name: String,
            val pid: Int
        )

        @Serializable
        data class Senior(
            val status: Int? = null
        )

        @Serializable
        data class UserSailing(
            val cardbg: Cardbg? = null,
            @SerialName("cardbg_with_focus")
            val cardbgWithFocus: JsonElement? = null,
            val pendant: Pendant? = null
        ) {
            @Serializable
            data class Cardbg(
                val fan: Fan,
                val id: Long,
                val image: String,
                @SerialName("jump_url")
                val jumpUrl: String,
                val name: String,
                val type: String
            ) {
                @Serializable
                data class Fan(
                    val color: String,
                    @SerialName("is_fan")
                    val isFan: Int,
                    val name: String,
                    @SerialName("num_desc")
                    val numDesc: String,
                    val number: Int
                )
            }

            @Serializable
            data class Pendant(
                val id: Long,
                val image: String,
                @SerialName("image_enhance")
                val imageEnhance: String,
                @SerialName("image_enhance_frame")
                val imageEnhanceFrame: String,
                @SerialName("jump_url")
                val jumpUrl: String,
                val name: String,
                val type: String
            )
        }

        @Serializable
        data class Vip(
            val accessStatus: Int,
            @SerialName("avatar_subscript")
            val avatarSubscript: Int,
            val dueRemark: String,
            val label: Label,
            @SerialName("nickname_color")
            val nicknameColor: String,
            val themeType: Int,
            val vipDueDate: Long,
            val vipStatus: Int,
            val vipStatusWarn: String,
            val vipType: Int
        ) {
            @Serializable
            data class Label(
                @SerialName("bg_color")
                val bgColor: String,
                @SerialName("bg_style")
                val bgStyle: Int,
                @SerialName("border_color")
                val borderColor: String,
                @SerialName("img_label_uri_hans")
                val imgLabelUriHans: String,
                @SerialName("img_label_uri_hans_static")
                val imgLabelUriHansStatic: String,
                @SerialName("img_label_uri_hant")
                val imgLabelUriHant: String,
                @SerialName("img_label_uri_hant_static")
                val imgLabelUriHantStatic: String,
                @SerialName("label_theme")
                val labelTheme: String,
                val path: String,
                val text: String,
                @SerialName("text_color")
                val textColor: String,
                @SerialName("use_img_label")
                val useImgLabel: Boolean
            )
        }
    }

    @Serializable
    data class ReplyControl(
        val location: String? = null,
        @SerialName("max_line")
        val maxLine: Int,
        @SerialName("sub_reply_entry_text")
        val subReplyEntryText: String? = null,
        @SerialName("sub_reply_title_text")
        val subReplyTitleText: String? = null,
        @SerialName("time_desc")
        val timeDesc: String
    )

    @Serializable
    data class UpAction(
        val like: Boolean,
        val reply: Boolean
    )

    /**
     * 置顶
     */
    @Serializable
    data class Top(
        val admin: JsonElement? = null,
        val upper: JsonElement? = null,
        val vote: JsonElement? = null
    )

    @Serializable
    data class UpSelection(
        @SerialName("ignore_count")
        val ignoreCount: Int,
        @SerialName("pending_count")
        val pendingCount: Int
    )
}

/**
 * 评论区显示控制
 */
@Serializable
data class Config(
    @SerialName("read_only")
    val readOnly: Boolean,
    @SerialName("show_up_flag")
    val showUpFlag: Boolean,
    @SerialName("showtopic")
    val showtopic: Int
)

/**
 * 评论区输入属性
 */
@Serializable
data class Control(
    @SerialName("answer_guide_android_url")
    val answerGuideAndroidUrl: String,
    @SerialName("answer_guide_icon_url")
    val answerGuideIconUrl: String,
    @SerialName("answer_guide_ios_url")
    val answerGuideIosUrl: String,
    @SerialName("answer_guide_text")
    val answerGuideText: String,
    @SerialName("bg_text")
    val bgText: String,
    @SerialName("child_input_text")
    val childInputText: String,
    @SerialName("disable_jump_emote")
    val disableJumpEmote: Boolean,
    @SerialName("empty_page")
    val emptyPage: JsonElement? = null,
    @SerialName("enable_charged")
    val enableCharged: Boolean,
    @SerialName("enable_cm_biz_helper")
    val enableCmBizHelper: Boolean,
    @SerialName("giveup_input_text")
    val giveupInputText: String,
    @SerialName("input_disable")
    val inputDisable: Boolean,
    @SerialName("root_input_text")
    val rootInputText: String,
    @SerialName("screenshot_icon_state")
    val screenshotIconState: Int,
    @SerialName("show_text")
    val showText: String,
    @SerialName("show_type")
    val showType: Int,
    @SerialName("upload_picture_icon_state")
    val uploadPictureIconState: Int,
    @SerialName("web_selection")
    val webSelection: Boolean
)

@Serializable
data class Upper(
    val mid: Long
)