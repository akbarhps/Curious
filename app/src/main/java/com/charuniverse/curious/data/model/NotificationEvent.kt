package com.charuniverse.curious.data.model

import com.charuniverse.curious.R

object NotificationEvent {

    const val POST_LOVE = "POST_LOVE"
    const val POST_COMMENT = "POST_COMMENT"

    fun getEventDestination(event: String): Int = when (event) {
        POST_LOVE -> R.id.postDetailFragment
        POST_COMMENT -> R.id.postDetailFragment
        else -> R.id.postFeedFragment
    }

    fun getEventValue(event: String): String = when (event) {
        POST_LOVE -> "loved your post"
        POST_COMMENT -> "comment on your post"
        else -> ""
    }

    fun getEventIcon(event: String): Int = when (event) {
        POST_LOVE -> R.drawable.ic_baseline_favorite_24
        POST_COMMENT -> R.drawable.ic_baseline_comment_24
        else -> R.drawable.ic_baseline_person_24
    }
}