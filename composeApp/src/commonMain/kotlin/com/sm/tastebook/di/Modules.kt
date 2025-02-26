package com.sm.tastebook.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sm.tastebook.book.data.database.DatabaseFactory
import com.sm.tastebook.book.data.database.FavoriteBookDatabase
import com.sm.tastebook.book.data.network.KtorRemoteBookDataSource
import com.sm.tastebook.book.data.network.RemoteBookDataSource
import com.sm.tastebook.book.data.repository.DefaultBookRepository
import com.sm.tastebook.book.domain.BookRepository
import com.sm.tastebook.book.presentation.SelectedBookViewModel
import com.sm.tastebook.book.presentation.book_detail.BookDetailViewModel
import com.sm.tastebook.book.presentation.book_list.BookListViewModel
import com.sm.tastebook.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::DefaultBookRepository).bind<BookRepository>()

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<FavoriteBookDatabase>().favoriteBookDao }

    viewModelOf(::BookListViewModel)
    viewModelOf(::BookDetailViewModel)
    viewModelOf(::SelectedBookViewModel)
}