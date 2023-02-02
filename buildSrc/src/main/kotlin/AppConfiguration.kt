object AppConfiguration {
    const val appId = "dev.aaa1115910.bv"
    const val compileSdk = 33
    const val minSdk = 21
    const val targetSdk = 33
    private const val major = 0
    private const val minor = 1
    private const val patch = 6
    val versionName: String by lazy {
        "$major.$minor.$patch.r${versionCode}.${"git rev-list HEAD --abbrev-commit --max-count=1".exec()}"
    }
    val versionCode: Int by lazy { "git rev-list --count HEAD".exec().toInt() }
}

fun String.exec() = String(Runtime.getRuntime().exec(this).inputStream.readBytes()).trim()