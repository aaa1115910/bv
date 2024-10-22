package dev.aaa1115910.bv

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import de.schnettler.datastore.manager.DataStoreManager
import dev.aaa1115910.biliapi.http.BiliHttpProxyApi
import dev.aaa1115910.biliapi.repositories.AuthRepository
import dev.aaa1115910.biliapi.repositories.ChannelRepository
import dev.aaa1115910.biliapi.repositories.FavoriteRepository
import dev.aaa1115910.biliapi.repositories.HistoryRepository
import dev.aaa1115910.biliapi.repositories.IndexRepository
import dev.aaa1115910.biliapi.repositories.LoginRepository
import dev.aaa1115910.biliapi.repositories.PgcRepository
import dev.aaa1115910.biliapi.repositories.RecommendVideoRepository
import dev.aaa1115910.biliapi.repositories.SearchRepository
import dev.aaa1115910.biliapi.repositories.SeasonRepository
import dev.aaa1115910.biliapi.repositories.VideoDetailRepository
import dev.aaa1115910.biliapi.repositories.VideoPlayRepository
import dev.aaa1115910.bv.dao.AppDatabase
import dev.aaa1115910.bv.entity.AuthData
import dev.aaa1115910.bv.entity.db.UserDB
import dev.aaa1115910.bv.network.HttpServer
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.screen.user.UserSwitchViewModel
import dev.aaa1115910.bv.util.FirebaseUtil
import dev.aaa1115910.bv.util.LogCatcherUtil
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import dev.aaa1115910.bv.viewmodel.TagViewModel
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import dev.aaa1115910.bv.viewmodel.index.AnimeIndexViewModel
import dev.aaa1115910.bv.viewmodel.login.AppQrLoginViewModel
import dev.aaa1115910.bv.viewmodel.login.SmsLoginViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcAnimeViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcDocumentaryViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcGuoChuangViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcMovieViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcTvViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcVarietyViewModel
import dev.aaa1115910.bv.viewmodel.search.SearchInputViewModel
import dev.aaa1115910.bv.viewmodel.search.SearchResultViewModel
import dev.aaa1115910.bv.viewmodel.user.FavoriteViewModel
import dev.aaa1115910.bv.viewmodel.user.FollowViewModel
import dev.aaa1115910.bv.viewmodel.user.FollowingSeasonViewModel
import dev.aaa1115910.bv.viewmodel.user.HistoryViewModel
import dev.aaa1115910.bv.viewmodel.user.UpInfoViewModel
import dev.aaa1115910.bv.viewmodel.video.VideoDetailViewModel
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.slf4j.impl.HandroidLoggerAdapter

class BVApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var dataStoreManager: DataStoreManager
        lateinit var koinApplication: KoinApplication
        var instance: BVApp? = null

        fun getAppDatabase(context: Context = this.context) = AppDatabase.getDatabase(context)
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG
        dataStoreManager = DataStoreManager(applicationContext.dataStore)
        koinApplication = startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@BVApp)
            modules(appModule)
        }
        FirebaseUtil.init(applicationContext)
        LogCatcherUtil.installLogCatcher()
        initRepository()
        initProxy()
        instance = this
        updateMigration()
        HttpServer.startServer()
    }

    fun initRepository() {
        val channelRepository by koinApplication.koin.inject<ChannelRepository>()
        channelRepository.initDefaultChannel(Prefs.accessToken, Prefs.buvid)

        val authRepository by koinApplication.koin.inject<AuthRepository>()
        authRepository.sessionData = Prefs.sessData.takeIf { it.isNotEmpty() }
        authRepository.biliJct = Prefs.biliJct.takeIf { it.isNotEmpty() }
        authRepository.accessToken = Prefs.accessToken.takeIf { it.isNotEmpty() }
        authRepository.mid = Prefs.uid.takeIf { it != 0L }
        authRepository.buvid3 = Prefs.buvid3
        authRepository.buvid = Prefs.buvid
    }

    fun initProxy() {
        if (Prefs.enableProxy) {
            BiliHttpProxyApi.createClient(Prefs.proxyHttpServer)

            val channelRepository by koinApplication.koin.inject<ChannelRepository>()
            runCatching {
                channelRepository.initProxyChannel(
                    Prefs.accessToken,
                    Prefs.buvid,
                    Prefs.proxyGRPCServer
                )
            }
        }
    }

    private fun updateMigration() {
        val lastVersionCode = Prefs.lastVersionCode
        if (lastVersionCode >= BuildConfig.VERSION_CODE) return
        Log.i("BVApp", "updateMigration from $lastVersionCode")
        if (lastVersionCode < 576) {
            // 从 Prefs 中读取登录数据写入 UserDB
            if (Prefs.isLogin) {
                runBlocking {
                    val existedUser = getAppDatabase().userDao().findUserByUid(Prefs.uid)
                    if (existedUser == null) {
                        val user = UserDB(
                            uid = Prefs.uid,
                            username = "Unknown",
                            avatar = "",
                            auth = AuthData.fromPrefs().toJson()
                        )
                        getAppDatabase().userDao().insert(user)
                    }
                }
            }
        }
        Prefs.lastVersionCode = BuildConfig.VERSION_CODE
    }
}

val appModule = module {
    single { AuthRepository() }
    single { UserRepository(get()) }
    single { LoginRepository() }
    single { VideoInfoRepository() }
    single { ChannelRepository() }
    single { FavoriteRepository(get()) }
    single { HistoryRepository(get(), get()) }
    single { SearchRepository(get(), get()) }
    single { VideoPlayRepository(get(), get()) }
    single { RecommendVideoRepository(get(), get()) }
    single { VideoDetailRepository(get(), get(), get()) }
    single { SeasonRepository(get()) }
    single { dev.aaa1115910.biliapi.repositories.UserRepository(get(), get()) }
    single { IndexRepository() }
    single { PgcRepository() }
    viewModel { DynamicViewModel(get(), get()) }
    viewModel { RecommendViewModel(get()) }
    viewModel { PopularViewModel(get()) }
    viewModel { AppQrLoginViewModel(get(), get()) }
    viewModel { SmsLoginViewModel(get(), get()) }
    viewModel { PlayerViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { HistoryViewModel(get(), get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { UpInfoViewModel(get()) }
    viewModel { FollowViewModel(get()) }
    viewModel { SearchInputViewModel(get()) }
    viewModel { SearchResultViewModel(get()) }
    viewModel { FollowingSeasonViewModel(get()) }
    viewModel { TagViewModel() }
    viewModel { VideoPlayerV3ViewModel(get(), get()) }
    viewModel { VideoDetailViewModel(get()) }
    viewModel { UserSwitchViewModel(get()) }
    viewModel { AnimeIndexViewModel(get()) }
    viewModel { PgcAnimeViewModel(get()) }
    viewModel { PgcGuoChuangViewModel(get()) }
    viewModel { PgcDocumentaryViewModel(get()) }
    viewModel { PgcMovieViewModel(get()) }
    viewModel { PgcTvViewModel(get()) }
    viewModel { PgcVarietyViewModel(get()) }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")
