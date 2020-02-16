package com.oknotokt.downloader

import com.oknotokt.extractor.Downloadable
import com.oknotokt.extractor.DownloadableExtractor
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.log4j.Logger
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException

class Downloader(
    private val destination: File = File("downloads"),
    private val pageURL: String,
    private val extractor: DownloadableExtractor,
    private val progressFun: ProgressFun
) {
    companion object {
        const val MAX_RETRIES = 3
    }
    private val logger = Logger.getLogger(javaClass)
    private var retries = 0
    private var fileName: String? = null
    private val client = with(OkHttpClient.Builder()) {
        addNetworkInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            val responseBody = originalResponse.body!!
            responseBody.let {
                originalResponse.newBuilder().body(
                    ProgressResponseBody(
                        it,
                        progressFun
                    )
                ).build()
            }
        }
    }.build()

    fun getFile(fileName: String): File = File("${destination.absolutePath}/$fileName")

    fun download(callback: (Downloadable?) -> Unit) {
        if (destination.absoluteFile.isFile) {
            throw IllegalArgumentException("destination has to be a directory, instead got $destination")
        }
        return try {
            val downloadable = extractor.extract(pageURL) ?: throw RuntimeException("Unable to extract URL")
            callback(downloadable)
            val (downloadableLink, fileName) = downloadable
            this.fileName = fileName
            destination.absoluteFile.mkdir()
            download(downloadableLink, getFile(fileName), fileName)
        } catch (e: Exception) {
            if (retries < MAX_RETRIES) {
                retries++
                cleanupAndRetry(e, callback)
            } else {
                callback(null)
            }
        }
    }

    private fun download(url: String, destination: File, fileName: String) {
        logger.info("Starting download from $url to ${destination.absolutePath}. fileName=$fileName")
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            val execute = client.newCall(request).execute()
            val outputStream = FileOutputStream(destination)

            val body = execute.body
            body?.let {
                with(outputStream) {
                    write(body.bytes())
                    close()
                }
            }
        } catch (e: Exception) {
            logger.error("Error downloading from $url", e)
        }
    }

    private fun cleanupAndRetry(e: Exception, callback: (Downloadable?) -> Unit) {
        println("Something went wrong while downloading $fileName, retry ${retries+1}/$MAX_RETRIES. Exception: $e")
        fileName?.let {
            val file = getFile(it)
            if (file.exists()) {
                println("Leftover file deleted. file=$file")
                file.delete()
            }
            download(callback)
        }
    }
}