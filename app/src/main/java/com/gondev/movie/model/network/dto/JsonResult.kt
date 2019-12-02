package com.gondev.movie.model.network.dto


data class JsonResult<T> (
	var message: String?, //":"movie readMovieList 성공",
	var code: Int, //":200,
	var resultType: String?, //":"list",
	var result: T //":[]
)