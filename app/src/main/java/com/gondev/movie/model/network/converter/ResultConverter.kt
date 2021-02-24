package com.gondev.movie.model.network.converter

import com.gondev.movie.BuildConfig
import com.gondev.movie.model.network.dto.JsonResult
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException

class ResultConverter(
	private val json: Json,
	private val serializer: KSerializer<JsonResult<Any>>
) : Converter<ResponseBody, Any> {
	override fun convert(value: ResponseBody): Any {
		val graphQLDataModel = json.decodeFromString(serializer, value.string())

		if(graphQLDataModel.code!=200)
			throw HttpException(
				retrofit2.Response.error<ResponseBody>(
					value,
					Response.Builder()
						.code(graphQLDataModel.code)
						.message(graphQLDataModel.message?:"")
						.protocol(Protocol.HTTP_1_1)
						.request(Request.Builder().url(BuildConfig.url_base).build())
						.build()
				)
			)

		return graphQLDataModel.result
	}

}