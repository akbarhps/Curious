package com.charuniverse.curious.util

import com.charuniverse.curious.BuildConfig

object Constant {

    const val GOOGLE_OAUTH_KEY = BuildConfig.GOOGLE_OAUTH_KEY

    const val MINUTE_IN_SECONDS = 60
    const val HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60
    const val DAY_IN_SECONDS = HOUR_IN_SECONDS * 24
    const val WEEK_IN_SECONDS = DAY_IN_SECONDS * 7
    const val MONTH_IN_SECONDS = WEEK_IN_SECONDS * 4
    const val YEAR_IN_SECONDS = MONTH_IN_SECONDS * 12

}