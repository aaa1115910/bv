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
import dev.aaa1115910.biliapi.repositories.ChannelRepository
import dev.aaa1115910.biliapi.repositories.LoginRepository
import dev.aaa1115910.biliapi.repositories.VideoPlayRepository
import dev.aaa1115910.bv.dao.AppDatabase
import dev.aaa1115910.bv.repository.UserRepository
import dev.aaa1115910.bv.repository.VideoInfoRepository
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.viewmodel.PlayerViewModel
import dev.aaa1115910.bv.viewmodel.TagViewModel
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.VideoPlayerV3ViewModel
import dev.aaa1115910.bv.viewmodel.home.AnimeViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.login.AppQrLoginViewModel
import dev.aaa1115910.bv.viewmodel.login.SmsLoginViewModel
import dev.aaa1115910.bv.viewmodel.login.WebQrLoginViewModel
import dev.aaa1115910.bv.viewmodel.search.SearchInputViewModel
import dev.aaa1115910.bv.viewmodel.search.SearchResultViewModel
import dev.aaa1115910.bv.viewmodel.user.FavoriteViewModel
import dev.aaa1115910.bv.viewmodel.user.FollowViewModel
import dev.aaa1115910.bv.viewmodel.user.FollowingSeasonViewModel
import dev.aaa1115910.bv.viewmodel.user.HistoryViewModel
import dev.aaa1115910.bv.viewmodel.user.UpInfoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class BVApp : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var dataStoreManager: DataStoreManager
        lateinit var koinApplication: KoinApplication
        lateinit var firebaseAnalytics: FirebaseAnalytics

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
        initChannelRepository()
    }

    private fun initChannelRepository() {
        val channelRepository by koinApplication.koin.inject<ChannelRepository>()
        channelRepository.initDefaultChannel(Prefs.accessToken, Prefs.buvid)
        channelRepository.sessionData = Prefs.sessData
    }
}

val appModule = module {
    single { UserRepository() }
    single { LoginRepository() }
    single { VideoInfoRepository() }
    single { ChannelRepository() }
    single { VideoPlayRepository(get()) }
    viewModel { DynamicViewModel(get()) }
    viewModel { PopularViewModel() }
    viewModel { WebQrLoginViewModel(get(), get()) }
    viewModel { AppQrLoginViewModel(get(), get()) }
    viewModel { SmsLoginViewModel(get(), get()) }
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
    viewModel { VideoPlayerV3ViewModel(get(), get()) }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "Settings")