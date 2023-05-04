package dev.aaa1115910.bv

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import de.schnettler.datastore.manager.DataStoreManager
import dev.aaa1115910.bv.dao.AppDatabase
import dev.aaa1115910.bv.entity.PlayerType
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.util.LibVLCUtil
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.fWarn
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.LoginViewModel
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import dev.aaa1115910.bv.viewmodel.TagViewModel
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import dev.aaa1115910.bv.viewmodel.home.AnimeViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.search.SearchInputViewModel
import dev.aaa1115910.bv.viewmodel.search.SearchResultViewModel
import dev.aaa1115910.bv.viewmodel.user.FavoriteViewModel
import dev.aaa1115910.bv.viewmodel.user.FollowViewModel
import dev.aaa1115910.bv.viewmodel.user.FollowingSeasonViewModel
import dev.aaa1115910.bv.viewmodel.user.HistoryViewModel
import dev.aaa1115910.bv.viewmodel.user.UpInfoViewModel
import mu.KotlinLogging
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.videolan.libvlc.LibVLC

class BVApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var dataStoreManager: DataStoreManager
        lateinit var koinApplication: KoinApplication
        lateinit var firebaseAnalytics: FirebaseAnalytics

        private val logger = KotlinLogging.logger { }

        fun getAppDatabase(context: Context = this.context) = AppDatabase.getDatabase(context)
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        dataStoreManager = DataStoreManager(applicationContext.dataStore)
        koinApplication = startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BVApp)
            modules(appModule)
        }
        firebaseAnalytics = Firebase.analytics
        checkLibVLC()
    }

    private fun checkLibVLC() {
        val clearLibs = {
            Prefs.playerType = PlayerType.Media3
            LibVLCUtil.clearLibs()
        }

        logger.fInfo { "Current play type: ${Prefs.playerType.name}" }
        if (Prefs.playerType == PlayerType.LibVLC) {
            if (!LibVLCUtil.existLibs()) {
                logger.fInfo { "LibVLC libs not exist" }
                clearLibs()
                "LibVLC 文件不存在，播放器切换回 Media3".toast(this)
                return
            }
            runCatching {
                LibVLC.loadLibraries(this)
            }.onFailure {
                Prefs.playerType = PlayerType.Media3
                logger.fWarn { "Load LibVLC failed: ${it.stackTraceToString()}" }
                "加载 LibVLC 失败，播放器切换回 Media3".toast(this)
            }
            runCatching {
                val localVersion = LibVLCUtil.getVersion()
                val requiredVersion = dev.aaa1115910.bv.player.BuildConfig.libVLCVersion
                if (!localVersion.startsWith(requiredVersion)) {
                    clearLibs()
                    logger.fWarn { "Local LibVLC version $localVersion is not match required version $requiredVersion" }
                    "LibVLC 版本不匹配，已移除 LibVLC 相关文件".toast(this)
                }
            }.onFailure {
                Prefs.playerType = PlayerType.Media3
                logger.fWarn { "Check LibVLC version failed: ${it.stackTraceToString()}" }
                "检查本地 LibVLC 版本失败，播放器切换回 Media3".toast(this)
            }
        }
    }
}

val appModule = module {
    single { UserRepository() }
    single { VideoInfoRepository() }
    viewModel { DynamicViewModel(get()) }
    viewModel { PopularViewModel() }
    viewModel { LoginViewModel(get()) }
    viewModel { PlayerViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { FavoriteViewModel() }
    viewModel { UpInfoViewModel() }
    viewModel { FollowViewModel() }
    viewModel { SearchInputViewModel() }
    viewModel { SearchResultViewModel() }
    viewModel { AnimeViewModel() }
    viewModel { FollowingSeasonViewModel() }
    viewModel { TagViewModel() }
    viewModel { VideoPlayerV3ViewModel(get()) }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")