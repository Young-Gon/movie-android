package com.gondev.movie.model.network.converter

import com.gondev.movie.model.network.dto.JsonResult
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ResultConverterFactory(val json: Json) : Converter.Factory() {

	@ExperimentalSerializationApi
	override fun responseBodyConverter(
		type: Type,
		annotations: Array<Annotation>,
		retrofit: Retrofit
	): Converter<ResponseBody, *>? = try {
		val resultSerializer=json.serializersModule.serializer(type)
		val serializer=JsonResult.serializer(resultSerializer)

		ResultConverter(json, serializer)
	} catch (e: Exception) {
		null
	}
}