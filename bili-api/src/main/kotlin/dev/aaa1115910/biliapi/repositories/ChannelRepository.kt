package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.grpc.utils.generateChannel
import io.grpc.ManagedChannel

class ChannelRepository {
    // grpc.biliapi.net
    var defaultChannel: ManagedChannel? = null

    fun initDefaultChannel(accessKey: String, buvid: String) {
        defaultChannel = generateChannel(accessKey, buvid)
    }
}
