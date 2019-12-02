package com.gondev.movie.di

import com.gondev.movie.model.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


/**
 * 룸 데이터베이스 관련 모듈 등록 변수 입니다
 */
val roomModule = module {
	single { AppDatabase.getInstance(androidApplication()) }
	single {  get<AppDatabase>().getMovieDao() }
	single {  get<AppDatabase>().getCommentDao() }
}