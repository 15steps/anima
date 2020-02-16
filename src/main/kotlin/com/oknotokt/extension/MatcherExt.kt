package com.oknotokt.extension

import java.util.regex.Matcher

fun Matcher.groupOrNull(group: Int): String? =
    if (this.find()) {
        this.group(group)
    } else {
        null
    }