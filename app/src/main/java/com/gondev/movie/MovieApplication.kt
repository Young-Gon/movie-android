package com.gondev.movie

import android.app.Application
import com.gondev.movie.di.networkModule
import com.gondev.movie.di.roomModule
import com.gondev.movie.di.viewModelModule
import com.gondev.movie.util.timber.DebugLogTree
import com.gondev.movie.util.timber.ReleaseLogTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
class MovieApplication:Application() {

	override fun onCreate() {
		super.onCreate()
		if (BuildConfig.DEBUG) {
			Timber.plant(DebugLogTree())
		}
		else{
			Timber.plant(ReleaseLogTree())
		}

		startKoin {
			androidLogger()
			androidContext(this@MovieApplication)
			modules(listOf(
				networkModule,
				roomModule,
				viewModelModule
			))
		}
	}
}