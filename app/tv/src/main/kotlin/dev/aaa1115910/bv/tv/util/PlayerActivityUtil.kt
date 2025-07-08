package dev.aaa1115910.bv.tv.util

import android.content.Context
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.tv.activities.video.RemoteControllerPanelDemoActivity
import dev.aaa1115910.bv.tv.activities.video.VideoPlayerV3Activity
import dev.aaa1115910.bv.util.Prefs

fun launchPlayerActivity(
    context: Context,
    avid: Long,
    cid: Long,
    bvid: String? = null,
    title: String,
    partTitle: String,
    played: Int,
    fromSeason: Boolean,
    subType: Int? = null,
    epid: Int? = null,
    seasonId: Int? = null,
    isVerticalVideo: Boolean = false,
    proxyArea: ProxyArea = ProxyArea.MainLand,
    playerIconIdle: String = "",
    playerIconMoving: String = ""
) {
    if (Prefs.showedRemoteControllerPanelDemo) {
        VideoPlayerV3Activity.actionStart(
            context = context,
            avid = avid,
            cid = cid,
            bvid = bvid,
            title = title,
            partTitle = partTitle,
            played = played,
            fromSeason = fromSeason,
            subType = subType,
            epid = epid,
            seasonId = seasonId,
            isVerticalVideo = isVerticalVideo,
            proxyArea = proxyArea,
            playerIconIdle = playerIconIdle,
            playerIconMoving = playerIconMoving
        )
    } else {
        RemoteControllerPanelDemoActivity.actionStart(
            context = context,
            avid = avid,
            cid = cid,
            bvid = bvid,
            title = title,
            partTitle = partTitle,
            played = played,
            fromSeason = fromSeason,
            subType = subType,
            epid = epid,
            seasonId = seasonId,
            isVerticalVideo = isVerticalVideo,
            proxyArea = proxyArea,
            playerIconIdle = playerIconIdle,
            playerIconMoving = playerIconMoving
        )
    }
}