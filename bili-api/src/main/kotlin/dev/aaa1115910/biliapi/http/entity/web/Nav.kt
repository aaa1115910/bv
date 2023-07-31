package dev.aaa1115910.biliapi.http.entity.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NavResponseData(
    val isLogin: Boolean,
    @SerialName("wbi_img")
    val wbiImg: WbiImg
) {
    @Serializable
    data class WbiImg(
        @SerialName("img_url")
        val imgUrl: String,
        @SerialName("sub_url")
        val subUrl: String
    ) {
        fun getImgKey(): String = imgUrl.split("/").last().split(".").first()
        fun getSubKey(): String = subUrl.split("/").last().split(".").first()
    }
}
