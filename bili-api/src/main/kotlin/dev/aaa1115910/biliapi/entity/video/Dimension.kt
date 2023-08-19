package dev.aaa1115910.biliapi.entity.video

data class Dimension(
    val width: Int,
    val height: Int,
    val isVertical: Boolean = width < height
) {
    companion object {
        fun fromDimension(dimension: bilibili.app.archive.v1.Dimension) = Dimension(
            width = dimension.width.toInt(),
            height = dimension.height.toInt()
        )

        fun fromDimension(dimension: dev.aaa1115910.biliapi.http.entity.video.Dimension) =
            Dimension(
                width = dimension.width,
                height = dimension.height
            )
    }
}
