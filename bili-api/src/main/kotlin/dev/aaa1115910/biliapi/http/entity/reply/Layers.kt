package dev.aaa1115910.biliapi.http.entity.reply

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class FallbackLayers(
    @SerialName("is_critical_group")
    val isCriticalGroup: Boolean,
    @SerialName("layers")
    val layers: List<Layer>
) {
    @Serializable
    data class Layer(
        @SerialName("general_spec")
        val generalSpec: GeneralSpec,
        @SerialName("layer_config")
        val layerConfig: LayerConfig,
        val resource: Resource,
        val visible: Boolean
    ) {

        /**
         * @param tags AVATAR_LAYER ICON_LAYER PENDENT_LAYER
         */
        @Serializable
        data class LayerConfig(
            @SerialName("is_critical")
            val isCritical: Boolean? = null,
            @SerialName("layer_mask")
            val layerMask: LayerMask? = null,
            val tags: Map<String, JsonElement> = emptyMap()
        ) {
            @Serializable
            data class LayerMask(
                @SerialName("general_spec")
                val generalSpec: GeneralSpec,
                @SerialName("mask_src")
                val maskSrc: DrawSrc
            )
        }

        @Serializable
        data class Resource(
            @SerialName("res_animation")
            val resAnimation: ResAnimation? = null,
            @SerialName("res_image")
            val resImage: ResImage? = null,
            @SerialName("res_native_draw")
            val resNativeDraw: ResNativeDraw? = null,
            @SerialName("res_type")
            val resType: Int
        ) {
            @Serializable
            data class ResAnimation(
                @SerialName("webp_src")
                val webpSrc: ImageSrc
            )

            @Serializable
            data class ResImage(
                @SerialName("image_src")
                val imageSrc: ImageSrc
            )

            @Serializable
            data class ResNativeDraw(
                @SerialName("draw_src")
                val drawSrc: DrawSrc
            )
        }
    }
}

@Serializable
data class GeneralSpec(
    @SerialName("pos_spec")
    val posSpec: PosSpec,
    @SerialName("render_spec")
    val renderSpec: RenderSpec,
    @SerialName("size_spec")
    val sizeSpec: SizeSpec
) {
    @Serializable
    data class PosSpec(
        @SerialName("axis_x")
        val axisX: Double,
        @SerialName("axis_y")
        val axisY: Double,
        @SerialName("coordinate_pos")
        val coordinatePos: Int
    )

    @Serializable
    data class RenderSpec(
        val opacity: Int
    )

    @Serializable
    data class SizeSpec(
        val height: Double,
        val width: Double
    )
}

@Serializable
data class ImageSrc(
    val local: Int? = null,
    @SerialName("placeholder")
    val placeholder: Int? = null,
    val remote: Remote? = null,
    @SerialName("src_type")
    val srcType: Int
) {
    @Serializable
    data class Remote(
        @SerialName("bfs_style")
        val bfsStyle: String,
        val url: String? = null
    )
}

@Serializable
data class DrawSrc(
    val draw: Draw,
    @SerialName("src_type")
    val srcType: Int
) {
    @Serializable
    data class Draw(
        @SerialName("color_config")
        val colorConfig: ColorConfig,
        @SerialName("draw_type")
        val drawType: Int,
        @SerialName("fill_mode")
        val fillMode: Int
    ) {
        @Serializable
        data class ColorConfig(
            val day: DayNight,
            @SerialName("is_dark_mode_aware")
            val isDarkModeAware: Boolean = false,
            val night: DayNight? = null
        ) {
            @Serializable
            data class DayNight(
                val argb: String
            )
        }
    }
}