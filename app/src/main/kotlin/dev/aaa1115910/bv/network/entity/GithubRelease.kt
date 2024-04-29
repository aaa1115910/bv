package dev.aaa1115910.bv.network.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
    val assets: List<Asset>,
    @SerialName("assets_url")
    val assetsUrl: String,
    val author: User,
    val body: String,
    @SerialName("created_at")
    val createdAt: String,
    val draft: Boolean,
    @SerialName("html_url")
    val htmlUrl: String,
    val id: Int,
    val name: String,
    @SerialName("node_id")
    val nodeId: String,
    val prerelease: Boolean,
    @SerialName("published_at")
    val publishedAt: String,
    val reactions: Reactions? = null,
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("tarball_url")
    val tarballUrl: String,
    @SerialName("target_commitish")
    val targetCommitish: String,
    @SerialName("upload_url")
    val uploadUrl: String,
    val url: String,
    @SerialName("zipball_url")
    val zipballUrl: String
) {
    val isPreRelease = prerelease
    val isRelease = !prerelease

    @Serializable
    data class Asset(
        @SerialName("browser_download_url")
        val browserDownloadUrl: String,
        @SerialName("content_type")
        val contentType: String,
        @SerialName("created_at")
        val createdAt: String,
        @SerialName("download_count")
        val downloadCount: Int,
        val id: Int,
        val label: String,
        val name: String,
        @SerialName("node_id")
        val nodeId: String,
        val size: Int,
        val state: String,
        @SerialName("updated_at")
        val updatedAt: String,
        val uploader: User,
        val url: String
    )

    @Serializable
    data class User(
        @SerialName("avatar_url")
        val avatarUrl: String,
        @SerialName("events_url")
        val eventsUrl: String,
        @SerialName("followers_url")
        val followersUrl: String,
        @SerialName("following_url")
        val followingUrl: String,
        @SerialName("gists_url")
        val gistsUrl: String,
        @SerialName("gravatar_id")
        val gravatarId: String,
        @SerialName("html_url")
        val htmlUrl: String,
        val id: Int,
        val login: String,
        @SerialName("node_id")
        val nodeId: String,
        @SerialName("organizations_url")
        val organizationsUrl: String,
        @SerialName("received_events_url")
        val receivedEventsUrl: String,
        @SerialName("repos_url")
        val reposUrl: String,
        @SerialName("site_admin")
        val siteAdmin: Boolean,
        @SerialName("starred_url")
        val starredUrl: String,
        @SerialName("subscriptions_url")
        val subscriptionsUrl: String,
        val type: String,
        val url: String
    )

    @Serializable
    data class Reactions(
        @SerialName("+1")
        val like: Int,
        @SerialName("-1")
        val dislike: Int,
        val laugh: Int,
        val hooray: Int,
        val confused: Int,
        val heart: Int,
        val rocket: Int,
        val eyes: Int
    )
}
