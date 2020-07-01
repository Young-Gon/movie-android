package com.gondev.movie.util

import android.app.Activity
import androidx.lifecycle.Observer

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class ActivityDispatcher(private val content: Activity.() -> Unit) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): (Activity.() -> Unit)? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): Activity.() -> Unit = content
}

/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class ActivityDispatcherObserver(private val onEventUnhandledContent: (( Activity.() -> Unit) -> Unit)) : Observer<ActivityDispatcher> {
    override fun onChanged(activityDispatcher: ActivityDispatcher?) {
        activityDispatcher?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}
