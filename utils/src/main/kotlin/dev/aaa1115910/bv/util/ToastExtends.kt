package dev.aaa1115910.bv.util

import android.content.Context
import android.widget.Toast

fun String.toast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, this, duration).show()
}

fun Int.toast(context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, context.getText(this), duration).show()
}