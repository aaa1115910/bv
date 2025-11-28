package dev.aaa1115910.biliapi.http.plugins

import dev.aaa1115910.biliapi.http.util.BiliAppConf
import dev.aaa1115910.biliapi.http.util.BiliWebConf
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.client.request.host
import io.ktor.http.HttpHeaders
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.utils.io.KtorDsl

private val LOGGER = KtorSimpleLogger("dev.aaa1115910.biliapi.http.plugins.BiliUserAgent")

@KtorDsl
class BiliUserAgentConfig(
    var version: String = BiliAppConf.APP_VERSION_NAME,
    var buildCode: Int = BiliAppConf.APP_BUILD_CODE,
    var channel: String = BiliAppConf.CHANNEL,
    var platform: String = BiliAppConf.PLATFORM,
    var mobiApp: String = BiliAppConf.MOBI_APP,
    var model: String = BiliAppConf.model,
    var osVersion: String = BiliAppConf.osVersion,
    var network: Int = BiliAppConf.NETWORK,
    var webViewVersion: Int = BiliWebConf.webViewVersion
) {
    var appUserAgent = ""
        private set
    var webUserAgent = ""
        private set

    fun buildUserAgents() {
        appUserAgent =
            "Mozilla/5.0 BiliDroid/$version (bbcallen@gmail.com) os/$platform model/$model mobi_app/$mobiApp build/$buildCode channel/$channel innerVer/$buildCode osVer/$osVersion network/$network"
        webUserAgent =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/$webViewVersion.0.0.0 Safari/537.36"
    }
}

val BiliUserAgent: ClientPlugin<BiliUserAgentConfig> =
    createClientPlugin("BiliUserAgent", ::BiliUserAgentConfig) {
        pluginConfig.buildUserAgents()
        val appUserAgent = pluginConfig.appUserAgent
        val webUserAgent = pluginConfig.webUserAgent
        onRequest { request, _ ->
            val userAgent =
                if (request.host == "app.bilibili.com" || request.host == "passport.bilibili.com") {
                    appUserAgent
                } else {
                    webUserAgent
                }
            LOGGER.trace("Adding User-Agent header: agent \"${userAgent}\" for ${request.url}")
            request.header(HttpHeaders.UserAgent, userAgent)
        }
    }

@Suppress("FunctionName")
fun HttpClientConfig<*>.BiliUserAgent() {
    install(BiliUserAgent) {

    }
}