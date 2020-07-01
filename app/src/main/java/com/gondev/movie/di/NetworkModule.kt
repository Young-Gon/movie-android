package com.gondev.movie.di

import com.gondev.movie.BuildConfig
import com.gondev.movie.model.network.api.MovieAPI
import com.google.gson.*
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


private const val CONNECT_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L

/**
 * 네트워크 관련 모귤 등록 변수 입니다.
 */
val networkModule = module {
	single { Cache(androidApplication().cacheDir, 10 * 1024 * 1024) }

	single {
		Interceptor { chain ->
			chain.proceed(chain.request().newBuilder().build())
		}
	}

	single {
		OkHttpClient.Builder().apply {
			cache(get())
			connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
			writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
			readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
			retryOnConnectionFailure(true)
			addInterceptor(get<Interceptor>())
			addInterceptor(HttpLoggingInterceptor().apply {
				if (BuildConfig.DEBUG) {
					level = HttpLoggingInterceptor.Level.BODY
				}
			})
		}.build()
	}

	single {
		GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
				override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
					return LocalDateTime.parse(json.asString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
				}
			})
			.setExclusionStrategies(AnnotationBasedExclusionStrategy())
			.create()
	}

	single {
		Retrofit.Builder()
			.baseUrl(BuildConfig.url_base)
			.addConverterFactory(GsonConverterFactory.create(get()))
			.client(get())
			.build()
	}

	single { get<Retrofit>().create(MovieAPI::class.java) }
}

/**
 * GSon 필드 제외 어노테이션
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY)
annotation class Exclude

class AnnotationBasedExclusionStrategy : ExclusionStrategy {
	override fun shouldSkipField(f: FieldAttributes): Boolean {
		return f.getAnnotation(Exclude::class.java) != null
	}

	override fun shouldSkipClass(clazz: Class<*>): Boolean {
		return false
	}
}