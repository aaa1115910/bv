package dev.aaa1115910.bv.mobile.screen

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.aaa1115910.biliapi.entity.FavoriteFolderMetadata
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.entity.carddata.VideoCardData
import dev.aaa1115910.bv.mobile.activities.VideoPlayerActivity
import dev.aaa1115910.bv.mobile.component.videocard.SmallVideoCard
import dev.aaa1115910.bv.mobile.theme.BVMobileTheme
import dev.aaa1115910.bv.util.OnBottomReached
import dev.aaa1115910.bv.util.calculateWindowSizeClassInPreview
import dev.aaa1115910.bv.viewmodel.user.FavoriteViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    favoriteViewModel: FavoriteViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val listState = rememberLazyGridState()

    val currentTabIndex by remember {
        derivedStateOf {
            favoriteViewModel.favoriteFolderMetadataList.indexOf(favoriteViewModel.currentFavoriteFolderMetadata)
        }
    }

    if (favoriteViewModel.favoriteFolderMetadataList.isNotEmpty() && favoriteViewModel.favorites.isNotEmpty()) {
        listState.OnBottomReached(
            loading = favoriteViewModel.updatingFolderItems,
        ) {
            favoriteViewModel.updateFolderItems()
        }
    }

    LaunchedEffect(currentTabIndex) {
        favoriteViewModel.favorites.clear()
        favoriteViewModel.updateFolderItems(force = true)
    }

    FavoriteContent(
        modifier = modifier,
        listState = listState,
        windowSize = windowSize,
        selectedTabIndex = currentTabIndex,
        favoriteFolders = favoriteViewModel.favoriteFolderMetadataList,
        favorites = favoriteViewModel.favorites,
        onClickTab = { folderMetadata ->
            favoriteViewModel.currentFavoriteFolderMetadata = folderMetadata
        },
        onClickVideo = { videoCardData ->
            VideoPlayerActivity.actionStart(
                context = context,
                aid = videoCardData.avid
            )
        },
        onBack = { (context as Activity).finish() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteContent(
    modifier: Modifier = Modifier,
    listState: LazyGridState = rememberLazyGridState(),
    windowSize: WindowSizeClass,
    selectedTabIndex: Int,
    favoriteFolders: List<FavoriteFolderMetadata>,
    favorites: List<VideoCardData>,
    onClickTab: (FavoriteFolderMetadata) -> Unit,
    onClickVideo: (VideoCardData) -> Unit,
    onBack: () -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text(text = stringResource(id = R.string.title_mobile_activity_favorite)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )

                if (favoriteFolders.isNotEmpty()) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        divider = { },
                    ) {
                        favoriteFolders.forEachIndexed { index, folderMetadata ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { onClickTab(folderMetadata) }
                            ) {
                                Box(
                                    modifier = Modifier.height(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        text = folderMetadata.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }

                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
            state = listState,
            columns = GridCells.Adaptive(180.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(items = favorites) { index, data ->
                SmallVideoCard(
                    data = data,
                    onClick = { onClickVideo(data) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(device = "spec:width=411dp,height=891dp")
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun FavoriteContentPreview() {
    val windowSize = calculateWindowSizeClassInPreview()
    val favoriteFolderSize = 10
    var currentFavoriteFolderMetadata by remember { mutableStateOf<FavoriteFolderMetadata?>(null) }

    val favoriteFolderMetadataList = (1..favoriteFolderSize).map {
        FavoriteFolderMetadata(
            id = it.toLong(),
            fid = it.toLong(),
            mid = 0,
            title = "folder$it",
            cover = null,
            videoInThisFav = false,
            mediaCount = (30..50).random()
        )
    }

    val generateFavorites: (Long) -> List<VideoCardData> = { folderId ->
        (1..(currentFavoriteFolderMetadata?.mediaCount ?: 50)).map {
            VideoCardData(
                avid = it.toLong(),
                title = "folder$folderId video$it",
                cover = "",
                play = it * 1000,
                danmaku = it * 100,
                upName = "upName$it",
                time = it * 1000L
            )
        }
    }

    val currentTabIndex by remember {
        derivedStateOf {
            favoriteFolderMetadataList.indexOf(currentFavoriteFolderMetadata)
                .takeIf { it != -1 } ?: 0
        }
    }
    val favorites by remember {
        derivedStateOf { generateFavorites(currentFavoriteFolderMetadata?.id ?: 0) }
    }

    BVMobileTheme {
        FavoriteContent(
            windowSize = windowSize,
            selectedTabIndex = currentTabIndex,
            favoriteFolders = favoriteFolderMetadataList,
            favorites = favorites,
            onClickTab = { currentFavoriteFolderMetadata = it },
            onClickVideo = {},
            onBack = {}
        )
    }
}
