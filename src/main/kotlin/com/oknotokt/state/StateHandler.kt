package com.oknotokt.state

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.oknotokt.resource.Resource
import java.io.File

class StateHandler(
    resources: List<Resource>
) {
    companion object {
        const val STATE_FILE_NAME = "state.json"
        val stateFile = File(STATE_FILE_NAME)
    }
    private var state: List<DownloadState>
    private val mapper = jacksonObjectMapper()

    init {
        state = if (stateFile.exists()) {
            loadFromFile()
        } else {
            resources.map {
                DownloadState(
                    pageURL = it.resourceLink,
                    status = DownloadStatus.PENDING
                )
            }
        }
    }

    fun nextDownload(): DownloadState? =
        state.find { it.status != DownloadStatus.FINISHED }

    fun hasPendingDownloads(): Boolean =
        state.find { it.status != DownloadStatus.FINISHED } != null


    fun update(download: DownloadState, updateFun: DownloadState.() -> Unit) {
        update(download.apply { updateFun(this) })
    }

    private fun update(download: DownloadState) {
        state = state.map {
            if (download.pageURL == it.pageURL) {
                download
            } else {
                it
            }
        }
        saveSnapshot()
    }

    private fun loadFromFile(): List<DownloadState> {
        val content = File(STATE_FILE_NAME).readText()
        return mapper.readValue(content)
    }

    private fun saveSnapshot() {
        mapper.writerWithDefaultPrettyPrinter().writeValue(stateFile, state)
    }
}