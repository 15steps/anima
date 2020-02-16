package com.oknotok.extractor

interface DownloadableExtractor {
    fun extract(url: String): Downloadable?
}