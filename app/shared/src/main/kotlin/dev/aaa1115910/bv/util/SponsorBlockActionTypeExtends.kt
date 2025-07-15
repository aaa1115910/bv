package dev.aaa1115910.bv.util

import android.content.Context
import dev.aaa1115910.bv.R
import dev.aaa1115910.bv.player.entity.sponsorblock.SponsorBlockActionType

fun SponsorBlockActionType.getDisplayName(context: Context) = stringResId().stringRes(context)

fun SponsorBlockActionType.stringResId() = when (this) {
    SponsorBlockActionType.AUTO_SKIP -> R.string.sponsor_block_action_type_auto_skip
    SponsorBlockActionType.MANUAL_SKIP -> R.string.sponsor_block_action_type_manual_skip
    SponsorBlockActionType.SHOW_MARK_ONLY -> R.string.sponsor_block_action_type_show_mark_only
    SponsorBlockActionType.DO_NOTHING -> R.string.sponsor_block_action_type_nothing
}