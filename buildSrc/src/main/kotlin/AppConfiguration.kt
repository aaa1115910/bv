object AppConfiguration {
    const val appId = "dev.aaa1115910.bv"
    const val compileSdk = 34
    const val minSdk = 21
    const val targetSdk = 34
    private const val major = 0
    private const val minor = 2
    private const val patch = 1
    val versionName: String by lazy {
        "$major.$minor.$patch.r${versionCode}.${"git rev-list HEAD --abbrev-commit --max-count=1".exec()}"
    }
    val versionCode: Int by lazy { "git rev-list --count HEAD".exec().toInt() }
    const val libVLCVersion = "3.0.18"
}

fun String.exec() = String(Runtime.getRuntime().exec(this).inputStream.readBytes()).trim()