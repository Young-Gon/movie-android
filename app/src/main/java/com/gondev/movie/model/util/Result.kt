package com.gondev.movie.model.util

import java.io.PrintWriter
import java.io.StringWriter

sealed class Result<out T>(
	val data: T?
) {
	class Loading<T>(data: T?) : Result<T>(data)

	class Success<T>(data: T?) : Result<T>(data)

	class Error<T>(val throwable: Throwable, data: T?) : Result<T>(data){
		fun getStackTrace(): String {
			val stringWriter = StringWriter()
			throwable.printStackTrace(PrintWriter(stringWriter))
			return stringWriter.toString()
		}
	}

	companion object{
		fun <T> loading(data: T?) = Loading(data)

		fun <T> success(data: T?) = Success(data)

		fun <T> error(throwable: Throwable, data: T?) =
			Error(throwable, data)
	}
}
