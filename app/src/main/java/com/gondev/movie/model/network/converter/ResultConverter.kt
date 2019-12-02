package com.gondev.movie.model.network.converter

import com.gondev.movie.BuildConfig
import com.gondev.movie.model.network.dto.JsonResult
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException

class ResultConverter(
	private val delegate: Converter<ResponseBody, JsonResult<Any>>?
): Converter<ResponseBody, Any> {

	override fun convert(value: ResponseBody): Any? {
		val graphQLDataModel = delegate?.convert(value)

		graphQLDataModel?.let {
			if(it.code!=200)
			throw HttpException(
				retrofit2.Response.error<ResponseBody>(
					value,
					Response.Builder()
						.code(it.code)
						.message(it.message?:"")
						.protocol(Protocol.HTTP_1_1)
						.request(Request.Builder().url(BuildConfig.url_base).build())
						.build()
				)
			)
		}

		return graphQLDataModel?.result
	}
}