import java.io.File

object AppConfiguration {
    const val appId = "dev.aaa1115910.bv"
    const val compileSdk = 35
    const val minSdk = 21
    const val targetSdk = 35
    private const val major = 0
    private const val minor = 2
    private const val patch = 10
    private const val hotFix = 0

    @Suppress("KotlinConstantConditions")
    val versionName: String by lazy {
        "$major.$minor.$patch${".$hotFix".takeIf { hotFix != 0 } ?: ""}" +
                ".r${versionCode}.${"git rev-list HEAD --abbrev-commit --max-count=1".exec()}"
    }
    val versionCode: Int by lazy { "git rev-list --count HEAD".exec().toInt() }
    const val libVLCVersion = "3.0.18"
    var googleServicesAvailable = true

    init {
        initConfigurations()
    }

    private fun initConfigurations() {
        val googleServicesJsonPath = "pwd".exec() + "/app/google-services.json"
        val googleServicesJsonFile = File(googleServicesJsonPath)
        googleServicesAvailable =
            googleServicesJsonFile.exists() && googleServicesJsonFile.readText().let {
                it.contains(appId) && it.contains("$appId.r8test") && it.contains("$appId.debug")
            }
    }
}

fun String.exec() = String(Runtime.getRuntime().exec(this).inputStream.readBytes()).trim()