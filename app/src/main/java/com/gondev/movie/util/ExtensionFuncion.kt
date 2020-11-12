package com.gondev.movie.util

import android.util.DisplayMetrics

fun Int.dpToPx(displayMetrics: DisplayMetrics): Int = (this * displayMetrics.density).toInt()

fun Int.pxToDp(displayMetrics: DisplayMetrics): Int = (this / displayMetrics.density).toInt()

class HandleResult<E, T: Collection<E>>(
    val body: T?,
    val consumed: Boolean
)

fun <E, T: Collection<E>> T?.isEmpty(run: ()-> Unit): HandleResult<E, T> {
    if(this == null || this.isEmpty()){
        run()
        HandleResult(this, true)
    }
    return HandleResult(this, false)
}

fun <E, T: Collection<E>> T?.isNotEmpty(run: (T) -> Unit): HandleResult<E, T> {
    if (this != null && this.isNotEmpty()) {
        run(this)
        return HandleResult(this, true)
    }
    return HandleResult(this, false)
}

infix fun <E, T: Collection<E>> HandleResult<E, T>.orElse(run: (T?) -> Unit) {
    if(!this.consumed)
        run(this.body)
}