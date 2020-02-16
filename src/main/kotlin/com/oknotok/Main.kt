package com.oknotok

import com.oknotok.downloader.Downloader
import com.oknotok.extractor.ZippyshareExtractor
import com.oknotok.resource.ResourceLoader
import com.oknotok.state.StateHandler
import com.oknotok.state.DownloadStatus
import com.oknotok.util.timedProgressBar
import org.apache.log4j.BasicConfigurator
import java.io.File

fun main() {
    BasicConfigurator.configure()
    val resources = ResourceLoader().load("resources.txt")
    val stateHandler = StateHandler(resources)
    while (stateHandler.hasPendingDownloads()) {
        val downloadState = stateHandler.nextDownload()!!
        when (downloadState.status) {
            DownloadStatus.PENDING -> {
                stateHandler.update(downloadState) {
                    status = DownloadStatus.DOWNLOADING
                }
                val downloader = Downloader(
                    pageURL = downloadState.pageURL,
                    extractor = ZippyshareExtractor(),
                    progressFun = timedProgressBar(downloadState)
                )
                downloader.download { downloadable ->
                    if (downloadable == null) {
                        stateHandler.update(downloadState) {
                            status = DownloadStatus.FAILED
                            downloadableLink = null
                            fileName = null
                        }
                    } else {
                        stateHandler.update(downloadState) {
                            status = DownloadStatus.DOWNLOADING
                            downloadableLink = downloadable.link
                            fileName = downloadable.fileName
                            path = downloader.getFile(downloadable.fileName).absolutePath
                        }
                    }
                }
                stateHandler.update(downloadState) {
                    status = DownloadStatus.FINISHED
                    println("Download finished. fileName=$fileName")
                }
            }
            DownloadStatus.DOWNLOADING -> {
                println("[WARN] Found a download that didn't finish, possibly because it was interrupted. Will try again. downloadState=$downloadState")
                stateHandler.update(downloadState) {
                    path?.let {
                        val file = File(it)
                        if (file.exists()) {
                            file.delete()
                            println("[WARN] Interrupted download deleted. path=$it")
                        }
                    }
                    status = DownloadStatus.PENDING
                }
            }
            DownloadStatus.FAILED -> {
                println("[ERROR] Download failed. downloadState=$downloadState")
                stateHandler.update(downloadState) {
                    status = DownloadStatus.PENDING
                    retried = true
                }
            }
            else -> {
                println("Skipping download=$downloadState")
            }
        }
    }
}