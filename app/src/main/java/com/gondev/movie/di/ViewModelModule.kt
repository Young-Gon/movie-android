package com.gondev.movie.di

import com.gondev.movie.model.database.AppDatabase
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.ui.comments.CommentListViewModel
import com.gondev.movie.ui.detail.MovieDetailViewModel
import com.gondev.movie.ui.main.MainViewModel
import com.gondev.movie.ui.write.WriteCommentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 뷰모델 관련 모듈 등록 변수 입니다
 */
val viewModelModule = module {
	viewModel { MainViewModel(get(), get()) }
	viewModel { (movie: Movie) -> MovieDetailViewModel(get(), get(), get(), get(), movie) }
	viewModel { (movie: Movie) -> CommentListViewModel(get(), get(), get(), movie) }
	viewModel { (movie: Movie) -> WriteCommentViewModel(get(), get(), movie) }
}