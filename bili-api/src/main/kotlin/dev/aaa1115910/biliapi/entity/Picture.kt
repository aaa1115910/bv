package dev.aaa1115910.biliapi.entity

import java.util.UUID

/**
 * 评论图片
 *
 * @param url 图片链接
 * @param width 图片宽度
 * @param height 图片高度
 * @param key 使用 [com.origeek.imageViewer.previewer.TransformImageView] [com.origeek.imageViewer.previewer.ImagePreviewer] 浏览图片缩放时需要用到的 key
 */
data class Picture(
    val url: String,
    val width: Int,
    val height: Int,
    val key: String
) {
    companion object {
        fun fromPicture(picture: dev.aaa1115910.biliapi.http.entity.reply.CommentData.Reply.Content.Picture): Picture {
            return Picture(
                url = picture.imgSrc,
                width = picture.imgWidth,
                height = picture.imgHeight,
                key = UUID.randomUUID().toString()
            )
        }

        fun fromPicture(picture: bilibili.main.community.reply.v1.Picture): Picture {
            return Picture(
                url = picture.imgSrc,
                width = picture.imgWidth.toInt(),
                height = picture.imgHeight.toInt(),
                key = UUID.randomUUID().toString()
            )
        }

        fun fromPicture(picture: dev.aaa1115910.biliapi.http.entity.dynamic.DynamicItem.Modules.Dynamic.Major.Draw.Pic): Picture {
            return Picture(
                url = picture.src,
                width = picture.width,
                height = picture.height,
                key = UUID.randomUUID().toString()
            )
        }

        fun fromPicture(picture: bilibili.app.dynamic.v2.MdlDynDrawItem): Picture {
            return Picture(
                url = picture.src,
                width = picture.width.toInt(),
                height = picture.height.toInt(),
                key = UUID.randomUUID().toString()
            )
        }
    }
}
