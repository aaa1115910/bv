package dev.aaa1115910.bv.network

import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.util.AbiUtil
import dev.aaa1115910.bv.util.Prefs
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.content.ProgressListener
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

private const val OWNER_NAME = "aaa1115910-gmail.com"
private const val APP_NAME = "bv"

object AppCenterApi {
    private var endPoint = "install.appcenter.ms"
    private lateinit var client: HttpClient
    private val logger = KotlinLogging.logger { }

    init {
        createClient()
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            BrowserUserAgent()
            install(ContentNegotiation) {
                json(Json {
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = endPoint
                }
            }
        }
    }

    suspend fun getPackageList(
        ownerName: String,
        appName: String,
        distributionGroupName: String,
        scope: String? = null,
        top: Int? = null
    ): List<ReleasePackage> =
        client.get("/api/v0.1/apps/$ownerName/$appName/distribution_groups/$distributionGroupName/public_releases") {
            scope?.let { parameter("scope", it) }
            top?.let { parameter("top", it) }
        }.body()

    suspend fun getPackageInfo(
        ownerName: String,
        appName: String,
        distributionGroupName: String,
        releaseId: Int,
        isInstallPage: Boolean? = null
    ): PackageInfo =
        client.get("/api/v0.1/apps/$ownerName/$appName/distribution_groups/$distributionGroupName/releases/$releaseId") {
            isInstallPage?.let { parameter("is_install_page", it) }
        }.body()

    suspend fun sendInstallAnalytics(
        ownerName: String,
        appName: String,
        distributionGroupId: String,
        releaseId: Int,
        userId: String = UUID.randomUUID().toString()
    ) {
        val result = client.post("/api/v0.1/public/apps/$ownerName/$appName/install_analytics") {
            contentType(ContentType.Application.Json)
            setBody(InstallAnalytics.create(releaseId, distributionGroupId, userId))
        }
        require("${result.status}".startsWith("2")) { "Send install analytics failed" }
    }

    suspend fun downloadFile(
        downloadUrl: String,
        //fileOutputStream: FileOutputStream,
        file: File,
        downloadListener: ProgressListener
    ) {
        //client.get(downloadUrl).bodyAsChannel().toInputStream().copyTo(fileOutputStream)
        client.prepareRequest {
            url(downloadUrl)
            onDownload(downloadListener)
        }.execute { response ->
            response.bodyAsChannel().copyAndClose(file.writeChannel())
        }
    }

    private fun getGroupName() = GroupData(BuildConfig.FLAVOR, getAbiType()).getGroupName()
    private fun getGroupId() = GroupData(BuildConfig.FLAVOR, getAbiType()).getGroupId()

    private fun getAbiType(): String {
        val abiSet = AbiUtil.getApkSupportedAbiSet()

        return when {
            abiSet.size > 1 || abiSet.isEmpty() -> "universal"
            else -> abiSet.first()
        }
    }

    suspend fun getPackageList() = getPackageList(
        ownerName = OWNER_NAME,
        appName = APP_NAME,
        distributionGroupName = getGroupName()
    )

    suspend fun getPackageInfo(releaseId: Int) = getPackageInfo(
        ownerName = OWNER_NAME,
        appName = APP_NAME,
        distributionGroupName = getGroupName(),
        releaseId = releaseId,
    )

    suspend fun sendInstallAnalytics(releaseId: Int) {
        sendInstallAnalytics(
            ownerName = OWNER_NAME,
            appName = APP_NAME,
            distributionGroupId = getGroupId(),
            releaseId = releaseId
        )
    }

    suspend fun getLatestVersion(): Pair<Int, String> {
        val list = getPackageList(OWNER_NAME, APP_NAME, getGroupName())
        return Pair(list.first().version.toInt(), list.first().shortVersion)
    }
}

/**
 * 已发布的版本
 *
 * @param id
 * @param shortVersion 版本名称
 * @param version 版本号
 * @param origin 来源
 * @param uploadedAt 发布时间
 * @param mandatoryUpdate
 * @param enabled
 * @param isExternalBuild
 */
@Serializable
data class ReleasePackage(
    val id: Int,
    @SerialName("short_version")
    val shortVersion: String,
    val version: String,
    val origin: String,
    @SerialName("uploaded_at")
    @Serializable(with = DateSerializer::class)
    val uploadedAt: Date? = null,
    @SerialName("mandatory_update")
    val mandatoryUpdate: Boolean,
    val enabled: Boolean,
    @SerialName("is_external_build")
    val isExternalBuild: Boolean
)

/**
 * 安装包信息
 *
 * @param appName 应用名称
 * @param appDisplayName 应用显示名称
 * @param appOs 应用支持的系统
 * @param owner 开发者
 * @param isExternalBuild
 * @param origin 来源
 * @param id
 * @param version 版本号
 * @param shortVersion 版本名称
 * @param size 应用大小
 * @param minOs 支持的最低系统版本
 * @param androidMinApiLevel 支持的最低 Android API 版本
 * @param deviceFamily
 * @param bundleIdentifier 应用包名
 * @param fingerprint MD5
 * @param uploadedAt 发布时间
 * @param downloadUrl 下载链接
 * @param installUrl 安装链接
 * @param mandatoryUpdate
 * @param enabled
 * @param fileExtension 文件类型
 * @param isLatest 是否为最新版
 * @param releaseNotes 更新日志
 * @param isUdidProvisioned
 * @param canResign
 * @param packageHashes
 * @param destinationType
 * @param status
 * @param distributionGroupId
 * @param distributionGroups
 */
@Serializable
data class PackageInfo(
    @SerialName("app_name")
    val appName: String,
    @SerialName("app_display_name")
    val appDisplayName: String,
    @SerialName("app_os")
    val appOs: String,
    val owner: Owner,
    @SerialName("is_external_build")
    val isExternalBuild: Boolean,
    val origin: String,
    val id: Int,
    val version: String,
    @SerialName("short_version")
    val shortVersion: String,
    val size: Long,
    @SerialName("min_os")
    val minOs: String,
    @SerialName("android_min_api_level")
    val androidMinApiLevel: String,
    @SerialName("device_family")
    val deviceFamily: JsonElement? = null,
    @SerialName("bundle_identifier")
    val bundleIdentifier: String,
    val fingerprint: String,
    @SerialName("uploaded_at")
    @Serializable(with = DateSerializer::class)
    val uploadedAt: Date? = null,
    @SerialName("download_url")
    val downloadUrl: String,
    @SerialName("install_url")
    val installUrl: String,
    @SerialName("mandatory_update")
    val mandatoryUpdate: Boolean,
    val enabled: Boolean,
    val fileExtension: String,
    @SerialName("is_latest")
    val isLatest: Boolean,
    @SerialName("release_notes")
    val releaseNotes: String,
    @SerialName("is_udid_provisioned")
    val isUdidProvisioned: JsonElement? = null,
    @SerialName("can_resign")
    val canResign: JsonElement? = null,
    @SerialName("package_hashes")
    val packageHashes: List<String> = emptyList(),
    @SerialName("destination_type")
    val destinationType: String,
    val status: String,
    @SerialName("distribution_group_id")
    val distributionGroupId: String,
    @SerialName("distribution_groups")
    val distributionGroups: List<DistributionGroups> = emptyList()
) {
    /**
     * 开发者
     *
     * @param name 用户名称
     * @param display_name 用户显示名称
     */
    @Serializable
    data class Owner(
        val name: String,
        @SerialName("display_name")
        val display_name: String
    )

    /**
     * 分发用户组
     *
     * @param id
     * @param name 用户组名称
     * @param origin 来源
     * @param displayName 用户组显示名称
     * @param isPublic 是否公开用户组
     */
    @Serializable
    data class DistributionGroups(
        val id: String,
        val name: String,
        val origin: String,
        @SerialName("display_name")
        val displayName: String,
        @SerialName("is_public")
        val isPublic: Boolean
    )
}

@Serializable
data class InstallAnalytics(
    val releases: List<InstallAnalyticsItem> = emptyList()
) {
    companion object {
        fun create(
            releaseId: Int,
            distributionGroupId: String,
            userId: String
        ): InstallAnalytics {
            return InstallAnalytics(
                releases = listOf(InstallAnalyticsItem(releaseId, distributionGroupId, userId))
            )
        }
    }

    /**
     * @param releaseId id
     * @param distributionGroupId 分发用户组id [UUID]
     * @param userId 用户id [UUID]
     */
    @Serializable
    data class InstallAnalyticsItem(
        @SerialName("release_id")
        val releaseId: Int,
        @SerialName("distribution_group_id")
        val distributionGroupId: String,
        @SerialName("user_id")
        val userId: String
    )
}

object DateSerializer : KSerializer<Date?> {
    @Suppress("SpellCheckingInspection")
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }
    override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date? {
        return sdf.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Date?) {
        encoder.encodeString(sdf.format(value ?: Date(0)))
    }
}

private data class GroupData(
    val flavor: String,
    val abiType: String
) {
    fun getGroupName(): String {
        val prefix = if (Prefs.updateAlpha) "Alpha" else "Public"
        return if (flavor == "lite") prefix else "$prefix-$abiType"
    }

    fun getGroupId(): String {
        return if (Prefs.updateAlpha) {
            if (flavor == "lite") {
                "d867ff94-66f2-4338-8aa2-ce86e2acd649"
            } else {
                when (abiType) {
                    "armeabi-v7a" -> "8796ecb5-a6f0-4194-a7d9-73ada5be7f66"
                    "arm64-v8a" -> "136d3a56-e052-449b-b493-a8ba5c024711"
                    "x86" -> "e62422ae-1439-462e-ac41-cd83fe17fa14"
                    "x86_64" -> "147899f9-8d07-4414-96de-0dd52cfed7e7"
                    else -> "9954160d-6a3d-4ea3-965c-7245832dfb8d"
                }
            }
        } else {
            if (flavor == "lite") {
                "9259f371-d475-4088-b9fe-e5adfac1b563"
            } else {
                when (abiType) {
                    "armeabi-v7a" -> "8cc6402a-4129-4009-84d2-16a1fe2ef039"
                    "arm64-v8a" -> "b4fc8b5b-735c-4e71-aa70-cd3b36b68adc"
                    "x86" -> "c63e16ca-88d1-48fb-bf2b-6b614b14bc09"
                    "x86_64" -> "e9a1ae2f-3158-4074-9c97-59627f8d67ec"
                    else -> "a450d9a9-44c9-40d3-94e4-ab2e1f1cb26f"
                }
            }
        }
    }
}