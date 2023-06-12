package dev.aaa1115910.bv.component.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import dev.aaa1115910.biliapi.entity.user.favorite.UserFavoriteFoldersData
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.component.Button
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.swapList

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    userFavoriteFolders: List<UserFavoriteFoldersData.UserFavoriteFolder> = emptyList(),
    favoriteFolderIds: List<Long> = emptyList(),
    onAddToDefaultFavoriteFolder: () -> Unit,
    onUpdateFavoriteFolders: (List<Long>) -> Unit
) {
    var showFavoriteDialog by remember { mutableStateOf(false) }

    Button(
        modifier = modifier,
        icon = {
            if (isFavorite) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        text = stringResource(R.string.favorite_button_text),
        onClick = {
            if (showFavoriteDialog) return@Button
            if (isFavorite) {
                showFavoriteDialog = true
            } else onAddToDefaultFavoriteFolder()
        }
    )

    FavoriteDialog(
        show = showFavoriteDialog,
        onHideDialog = { showFavoriteDialog = false },
        userFavoriteFolders = userFavoriteFolders,
        favoriteFolderIds = favoriteFolderIds,
        onUpdateFavoriteFolders = onUpdateFavoriteFolders
    )
}

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
private fun FavoriteDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideDialog: () -> Unit,
    userFavoriteFolders: List<UserFavoriteFoldersData.UserFavoriteFolder> = emptyList(),
    favoriteFolderIds: List<Long> = emptyList(),
    onUpdateFavoriteFolders: (List<Long>) -> Unit
) {
    val selectedFavoriteFolderIds = remember { mutableStateListOf<Long>() }
    val defaultFocusRequester = remember { FocusRequester() }

    LaunchedEffect(show) {
        if (show) {
            selectedFavoriteFolderIds.swapList(favoriteFolderIds)
            defaultFocusRequester.requestFocus()
        }
    }

    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onHideDialog,
            confirmButton = {},
            title = { Text(text = stringResource(R.string.favorite_dialog_title)) },
            text = {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    userFavoriteFolders.forEachIndexed { index, userFavoriteFolder ->
                        val selected = selectedFavoriteFolderIds.contains(userFavoriteFolder.id)
                        var hasFocus by remember { mutableStateOf(false) }

                        val itemModifier =
                            if (index == 0) Modifier.focusRequester(defaultFocusRequester)
                            else Modifier

                        FilterChip(
                            modifier = itemModifier.onFocusChanged { hasFocus = it.hasFocus },
                            selected = selected,
                            onClick = {
                                if (selectedFavoriteFolderIds.contains(userFavoriteFolder.id)) {
                                    selectedFavoriteFolderIds.remove(userFavoriteFolder.id)
                                } else {
                                    selectedFavoriteFolderIds.add(userFavoriteFolder.id)
                                }
                                onUpdateFavoriteFolders(selectedFavoriteFolderIds)
                            },
                            border = if (hasFocus) FilterChipDefaults.filterChipBorder(
                                borderColor = Color.White,
                                borderWidth = 2.dp,
                                selectedBorderColor = Color.White,
                                selectedBorderWidth = 2.dp
                            ) else FilterChipDefaults.filterChipBorder(),
                            label = {
                                Text(text = userFavoriteFolder.title)
                            },
                            leadingIcon = {
                                Row {
                                    AnimatedVisibility(visible = selected) {
                                        Icon(
                                            imageVector = Icons.Rounded.Done,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun FavoriteButtonEnablePreview() {
    BVTheme {
        FavoriteButton(
            isFavorite = true,
            onAddToDefaultFavoriteFolder = {},
            onUpdateFavoriteFolders = {}
        )
    }
}

@Preview
@Composable
fun FavoriteButtonDisablePreview() {
    BVTheme {
        FavoriteButton(
            isFavorite = false,
            onAddToDefaultFavoriteFolder = {},
            onUpdateFavoriteFolders = {}
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun FavoriteDialogPreview() {
    val userFavoriteFolders = listOf(
        UserFavoriteFoldersData.UserFavoriteFolder(0, 0, 0, 0, "收藏夹1", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(1, 1, 0, 0, "收藏夹2", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(2, 2, 0, 0, "收藏夹3", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(3, 3, 0, 0, "收藏夹4", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(4, 4, 0, 0, "收藏夹5", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(5, 5, 0, 0, "收藏夹6", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(6, 6, 0, 0, "收藏夹7", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(7, 7, 0, 0, "收藏夹8", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(8, 8, 0, 0, "收藏夹9", 0, 0),
        UserFavoriteFoldersData.UserFavoriteFolder(9, 9, 0, 0, "收藏夹10", 0, 0),
    )
    BVTheme {
        FavoriteDialog(
            show = true,
            onHideDialog = {},
            userFavoriteFolders = userFavoriteFolders,
            onUpdateFavoriteFolders = {}
        )
    }
}