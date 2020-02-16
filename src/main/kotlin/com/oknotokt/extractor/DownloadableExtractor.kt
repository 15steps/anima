package com.oknotokt.extractor

interface DownloadableExtractor {
    fun extract(url: String): Downloadable?
}