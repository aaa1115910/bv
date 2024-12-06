package dev.aaa1115910.bv.util

import android.content.Context
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.bv.R

fun PgcType.getDisplayName(context: Context) = when (this) {
    PgcType.Anime -> R.string.pgc_type_anime
    PgcType.GuoChuang -> R.string.pgc_type_guochuang
    PgcType.Movie -> R.string.pgc_type_movie
    PgcType.Documentary -> R.string.pgc_type_documentary
    PgcType.Tv -> R.string.pgc_type_tv
    PgcType.Variety -> R.string.pgc_type_variety
}.stringRes(context)