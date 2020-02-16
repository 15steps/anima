package com.oknotok.resource

import java.io.File
import java.lang.IllegalArgumentException

/**
 * Parse a file with links to a list of resources
 */
class ResourceLoader {
    fun load(path: String): List<Resource> {
        val file = File(path)
        if (!file.isFile || !file.exists()) {
            throw IllegalArgumentException("Resource is not a file or does not exist!")
        }
        return file.readLines()
            .map { Resource(it) }
    }
}