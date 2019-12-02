package com.gondev.movie.model.network.converter

import com.gondev.movie.model.network.dto.JsonResult
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ResultConverterFactory : Converter.Factory() {

	override fun responseBodyConverter(
		type: Type,
		annotations: Array<Annotation>,
		retrofit: Retrofit
	): Converter<ResponseBody, *>? = try {
		val dataType = TypeToken.getParameterized(JsonResult::class.java, type).type
		val converter: Converter<ResponseBody, JsonResult<Any>>? = retrofit.nextResponseBodyConverter(this, dataType, annotations)
		ResultConverter(converter)
	} catch (e: Exception) {
		null
	}
}