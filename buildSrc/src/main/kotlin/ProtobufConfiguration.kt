import java.io.File

object ProtobufConfiguration {
    private val usedProtoFiles = setOf(
        "bilibili/app/archive/middleware/v1/preload.proto",
        "bilibili/app/archive/v1/archive.proto",
        "bilibili/app/card/v1/ad.proto",
        "bilibili/app/card/v1/card.proto",
        "bilibili/app/card/v1/common.proto",
        "bilibili/app/card/v1/single.proto",
        "bilibili/app/dynamic/v2/dynamic.proto",
        "bilibili/app/interfaces/v1/history.proto",
        "bilibili/app/interfaces/v1/search.proto",
        "bilibili/app/playeronline/v1/playeronline.proto",
        "bilibili/app/playerunite/v1/playerunite.proto",
        "bilibili/app/show/popular/v1/popular.proto",
        "bilibili/app/view/v1/view.proto",
        "bilibili/community/service/dm/v1/dm.proto",
        "bilibili/dagw/component/avatar/common/common.proto",
        "bilibili/dagw/component/avatar/v1/avatar.proto",
        "bilibili/dagw/component/avatar/v1/plugin.proto",
        "bilibili/main/community/reply/v1/reply.proto",
        "bilibili/metadata/device/device.proto",
        "bilibili/metadata/locale/locale.proto",
        "bilibili/metadata/metadata.proto",
        "bilibili/metadata/network/network.proto",
        "bilibili/pagination/pagination.proto",
        "bilibili/pgc/gateway/player/v2/playurl.proto",
        "bilibili/playershared/playershared.proto",
        "bilibili/polymer/app/search/v1/search.proto",
        "bilibili/rpc/status.proto",
        "common/ErrorProto.proto",
    )
    val excludeProtoFiles = getAllProtoFiles() - usedProtoFiles

    private fun getAllProtoFiles(): Set<String> {
        val rootDir = File("bili-api-grpc/proto")
        val protoFiles = mutableSetOf<String>()
        rootDir.walk().forEach {
            if (it.isFile && it.extension == "proto") {
                val path = it.relativeTo(rootDir).invariantSeparatorsPath
                protoFiles.add(path)
            }
        }
        return protoFiles
    }
}
