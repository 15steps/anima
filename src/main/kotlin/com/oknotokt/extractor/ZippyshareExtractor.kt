package com.oknotokt.extractor

import com.oknotokt.extension.groupOrNull
import com.udojava.evalex.Expression
import org.apache.log4j.Logger
import java.lang.RuntimeException
import java.net.URL

class ZippyshareExtractor(
    private val baseURL: String = "https://www97.zippyshare.com"
) : DownloadableExtractor {
    private val logger = Logger.getLogger(javaClass)
    private val mkvPattern = """\('dlbutton'\)\.href=(.*);""".toPattern()

    /**
     * Link is composed as follows:
     * baseURL + first part + math exp + file name
     * example: https://www97.zippyshare.com/d/2y0ABioH/1249/Legend_of_the_Galactic_Heroes_009_ANSK-Anbient.mkv
     */
    override fun extract(url: String): Downloadable? {
        println("[ZippyShareExtractor] Attempting to find downloadable link on $url")
        val noSpaceHTML = URL(url)
            .readText()
            .replace(" ", "")
        val linkMatch = mkvPattern.matcher(noSpaceHTML).groupOrNull(1)
        val cleanText = linkMatch?.replace("\"", "")

        return cleanText?.run {
            val firstPlus = indexOfFirst { it == '+' }
            val lastPlus = indexOfLast { it == '+' }
            if (firstPlus + lastPlus <= 0) {
                logger.error("Couldn't extract a downloadable link. Will not try again")
                throw RuntimeException("Error extracting downloadable link")
            }
            val fileName = substring(lastPlus + 1)
            val link = baseURL +
                    substring(0, firstPlus) +
                    Expression(substring(firstPlus + 1, lastPlus)).eval() +
                    fileName
            Downloadable(link, fileName.substring(1))
        }
    }
}