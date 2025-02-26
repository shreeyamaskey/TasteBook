package com.sm.tastebook.book.data.network

import com.sm.tastebook.book.data.dto.BookWorkDto
import com.sm.tastebook.book.data.dto.SearchResponseDto
import com.sm.tastebook.core.domain.DataError
import com.sm.tastebook.core.domain.Result

interface RemoteBookDataSource {
    suspend fun searchBooks(
        query: String,
        resultLimit: Int? = null
    ): Result<SearchResponseDto, DataError.Remote>

    suspend fun getBookDetails(bookWorkId: String): Result<BookWorkDto, DataError.Remote>
}