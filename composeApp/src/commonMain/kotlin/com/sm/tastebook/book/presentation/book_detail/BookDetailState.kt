package com.sm.tastebook.book.presentation.book_detail

import com.sm.tastebook.book.domain.Book

data class BookDetailState(
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val book: Book? = null
)
