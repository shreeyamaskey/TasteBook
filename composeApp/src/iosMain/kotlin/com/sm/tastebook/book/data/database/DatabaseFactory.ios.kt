@file:OptIn(ExperimentalForeignApi::class)

package com.sm.tastebook.book.data.database

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

class DatabaseFactory {
    fun create() {
        // Since FavoriteBookDatabase is gone, we'll remove that reference.
        val dbFile = documentDirectory() + "/mydatabase.db"
        // If you need to use Room on Android, that code should be in your Android source set.
        println("Creating database at: $dbFile")
    }

    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory?.path)
    }
}