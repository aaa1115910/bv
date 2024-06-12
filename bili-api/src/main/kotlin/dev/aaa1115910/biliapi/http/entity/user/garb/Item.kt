package dev.aaa1115910.biliapi.http.entity.user.garb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Item(
    @SerialName("item_id")
    val itemId: Int,
    val name: String,
    val state: String,
    @SerialName("tab_id")
    val tabId: Int,
    val properties: Properties,
    @SerialName("current_activity")
    val currentActivity: JsonObject? = null,
    @SerialName("next_activity")
    val nextActivity: JsonObject? = null,
    @SerialName("current_sources")
    val currentSources: JsonObject? = null,
    @SerialName("next_sources")
    val nextSources: JsonObject? = null,
    @SerialName("sale_left_time")
    val saleLeftTime: Long,
    @SerialName("sale_time_end")
    val saleTimeEnd: Long,
    @SerialName("sale_surplus")
    val saleSurplus: Int
) {
    /**
     * 用户装扮属性
     *
     * @param dragIcon 进度条动画 - 拖拽 (lottie)
     * @param dragIconHash 进度条动画 - 拖拽 hash
     * @param dragLeftPng 进度条动画 - 向左拖拽 (png)
     * @param dragRightPng 进度条动画 - 向右拖拽 (png)
     * @param fanNoColor
     * @param fansImage
     * @param fansMaterialId
     * @param garbAvatar 头像框 静态示例
     * @param goodsType
     * @param grayRule
     * @param grayRuleType all
     * @param hot
     * @param icon 进度条动画 - 静态 (lottie)
     * @param iconHash 进度条动画 - idle hash
     * @param image 预览图片
     * @param imageAni 预览图片 (点赞) zlib compressed protobuf file
     * @param imageAniCut 预览图片 (点赞) zlib compressed protobuf file
     * @param imageAvatar 头像框 示例头像（不包含头像框）
     * @param imageEnhance 预览图片 动态
     * @param imageEnhanceFrame 预览图片 帧
     * @param imagePreview 预览图片
     * @param imagePreviewSmall 预览图片 (小)
     * @param loadingFrameUrl 加载动画 帧
     * @param loadingUrl 加载动画 动图
     * @param middlePng 进度条动画 - idle (png)
     * @param realnameAuth
     * @param saleType other vip_suit suit
     * @param squaredImage 圆角预览图片
     * @param staticIconImage 预览图片
     * @param ver
     */
    @Serializable
    data class Properties(
        @SerialName("drag_icon")
        val dragIcon: String? = null,
        @SerialName("drag_icon_hash")
        val dragIconHash: String? = null,
        @SerialName("drag_left_png")
        val dragLeftPng: String? = null,
        @SerialName("drag_right_png")
        val dragRightPng: String? = null,
        @SerialName("fan_no_color")
        val fanNoColor: String? = null,
        @SerialName("fans_image")
        val fansImage: String? = null,
        @SerialName("fans_material_id")
        val fansMaterialId: String? = null,
        @SerialName("garb_avatar")
        val garbAvatar: String? = null,
        @SerialName("goods_type")
        val goodsType: String? = null,
        @SerialName("gray_rule")
        val grayRule: String? = null,
        @SerialName("gray_rule_type")
        val grayRuleType: String? = null,
        val hot: String? = null,
        val icon: String? = null,
        @SerialName("icon_hash")
        val iconHash: String? = null,
        val image: String? = null,
        @SerialName("image_ani")
        val imageAni: String? = null,
        @SerialName("image_ani_cut")
        val imageAniCut: String? = null,
        @SerialName("image_avatar")
        val imageAvatar: String? = null,
        @SerialName("image_enhance")
        val imageEnhance: String? = null,
        @SerialName("image_enhance_frame")
        val imageEnhanceFrame: String? = null,
        @SerialName("image_preview")
        val imagePreview: String? = null,
        @SerialName("image_preview_small")
        val imagePreviewSmall: String? = null,
        @SerialName("loading_frame_url")
        val loadingFrameUrl: String? = null,
        @SerialName("loading_url")
        val loadingUrl: String? = null,
        @SerialName("middle_png")
        val middlePng: String? = null,
        @SerialName("realname_auth")
        val realnameAuth: String? = null,
        @SerialName("sale_type")
        val saleType: String? = null,
        @SerialName("squared_image")
        val squaredImage: String? = null,
        @SerialName("static_icon_image")
        val staticIconImage: String? = null,
        val ver: String? = null
    ) {
        fun isLottieDragIcon() = dragIcon != null
    }
}
