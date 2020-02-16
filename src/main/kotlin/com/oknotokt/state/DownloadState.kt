package com.oknotokt.state

import java.util.*

enum class DownloadStatus {
    DOWNLOADING, PENDING, FINISHED, FAILED
}

data class DownloadState(
    val id: String = UUID.randomUUID().toString(),
    val pageURL: String,
    var status: DownloadStatus,
    var fileName: String? = null,
    var path: String? = null,
    var downloadableLink: String? = null,
    var retried: Boolean = false
)