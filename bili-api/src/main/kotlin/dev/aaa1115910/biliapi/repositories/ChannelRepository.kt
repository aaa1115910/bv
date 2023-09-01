package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.grpc.utils.generateChannel
import io.grpc.ManagedChannel

class ChannelRepository {
    // grpc.biliapi.net
    var defaultChannel: ManagedChannel? = null

    // custom proxy server
    var proxyChannel: ManagedChannel? = null

    fun initDefaultChannel(accessKey: String, buvid: String) {
        defaultChannel = generateChannel(accessKey, buvid)
    }

    fun initProxyChannel(accessKey: String, buvid: String, endPoint: String) {
        proxyChannel = generateChannel(accessKey, buvid, endPoint)
        //proxyChannel = generateChannel(accessKey, buvid, "192.168.2.125", 8080, false)
    }
}
