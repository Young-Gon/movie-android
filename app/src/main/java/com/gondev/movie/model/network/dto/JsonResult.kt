package com.gondev.movie.model.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("JsonResult")
data class JsonResult<T> (
	var message: String?, //":"movie readMovieList 성공",
	var code: Int, //":200,
	var resultType: String?, //":"list",
	var result: T //":[]
)