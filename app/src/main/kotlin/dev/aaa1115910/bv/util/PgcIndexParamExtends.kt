package dev.aaa1115910.bv.util

import android.content.Context
import dev.aaa1115910.biliapi.entity.pgc.index.Area
import dev.aaa1115910.biliapi.entity.pgc.index.Copyright
import dev.aaa1115910.biliapi.entity.pgc.index.IndexOrder
import dev.aaa1115910.biliapi.entity.pgc.index.IndexOrderType
import dev.aaa1115910.biliapi.entity.pgc.index.IsFinish
import dev.aaa1115910.biliapi.entity.pgc.index.PgcIndexParam
import dev.aaa1115910.biliapi.entity.pgc.index.Producer
import dev.aaa1115910.biliapi.entity.pgc.index.ReleaseDate
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonMonth
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonStatus
import dev.aaa1115910.biliapi.entity.pgc.index.SeasonVersion
import dev.aaa1115910.biliapi.entity.pgc.index.SpokenLanguage
import dev.aaa1115910.biliapi.entity.pgc.index.Style
import dev.aaa1115910.biliapi.entity.pgc.index.Year
import dev.aaa1115910.bv.R

fun PgcIndexParam.getDisplayName(context: Context) = when (this) {
    is IndexOrder ->
        fromStringArray(context, R.array.pgc_index_filter_order_name, ordinal)

    is IndexOrderType ->
        fromStringArray(context, R.array.pgc_index_filter_order_type_name, ordinal)

    is SeasonVersion ->
        fromStringArray(context, R.array.pgc_index_filter_season_version_name, ordinal)

    is SpokenLanguage ->
        fromStringArray(context, R.array.pgc_index_filter_spoken_language_name, ordinal)

    is Area ->
        fromStringArray(context, R.array.pgc_index_filter_area_name, ordinal)

    is IsFinish ->
        fromStringArray(context, R.array.pgc_index_filter_is_finish_name, ordinal)

    is Copyright ->
        fromStringArray(context, R.array.pgc_index_filter_copyright_name, ordinal)

    is SeasonStatus ->
        fromStringArray(context, R.array.pgc_index_filter_season_status_name, ordinal)

    is SeasonMonth ->
        fromStringArray(context, R.array.pgc_index_filter_season_month_name, ordinal)

    is Producer ->
        fromStringArray(context, R.array.pgc_index_filter_producer_name, ordinal)

    is Year ->
        fromStringArray(context, R.array.pgc_index_filter_year_name, ordinal)

    is ReleaseDate ->
        fromStringArray(context, R.array.pgc_index_filter_release_date_name, ordinal)

    is Style ->
        fromStringArray(context, R.array.pgc_index_filter_style_name, ordinal)

    else -> ""
}

private fun fromStringArray(
    context: Context,
    arrayId: Int,
    index: Int
): String = context.resources.getStringArray(arrayId)[index]
