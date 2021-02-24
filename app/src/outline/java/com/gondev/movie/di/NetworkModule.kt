package com.gondev.movie.di

import com.gondev.movie.BuildConfig
import com.gondev.movie.model.network.api.MovieAPI
import com.gondev.movie.model.network.converter.ResultConverterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


private const val CONNECT_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L

/**
 * 네트워크 관련 모듈 등록 변수 입니다.
 */
@ExperimentalSerializationApi
val networkModule = module {
	single { Cache(androidApplication().cacheDir, 10 * 1024 * 1024) }

	/*single {
		Interceptor { chain ->
			chain.proceed(chain.request().newBuilder().build())
		}
	}*/

	single {
		OkHttpClient.Builder().apply {
			cache(get())
			connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
			writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
			readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
			retryOnConnectionFailure(true)
			//addInterceptor(get<Interceptor>())
			addNetworkInterceptor(HttpLoggingInterceptor().apply {
				level = if (BuildConfig.DEBUG) {
					HttpLoggingInterceptor.Level.BODY
				} else {
					HttpLoggingInterceptor.Level.NONE
				}
			})
		}.build()
	}

	/*single {
		GsonBuilder()
			.setExclusionStrategies(AnnotationBasedExclusionStrategy())
			.create()
	}*/

	single {
		val json= Json{
			if(!BuildConfig.DEBUG)
				ignoreUnknownKeys = true
		}

		Retrofit.Builder()
			.baseUrl(BuildConfig.url_base)
			.addConverterFactory(ResultConverterFactory(json))
			.addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
			.client(get())
			.build()
	}

	single { get<Retrofit>().create(MovieAPI::class.java) }
}
