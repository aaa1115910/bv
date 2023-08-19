package dev.aaa1115910.biliapi.entity.user

data class FollowedUser(
    val mid: Long,
    val name: String,
    val avatar: String,
    val sign: String
) {
    companion object {
        fun fromHttpFollowedUser(followedUser: dev.aaa1115910.biliapi.http.entity.user.UserFollowData.FollowedUser) =
            FollowedUser(
                mid = followedUser.mid,
                name = followedUser.uname,
                avatar = followedUser.face,
                sign = followedUser.sign
            )
    }
}