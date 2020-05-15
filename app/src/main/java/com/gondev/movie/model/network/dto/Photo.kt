package com.gondev.movie.model.network.dto

data class Photo (
	val url: String,
	val isVideo: Boolean,
	val link: String?=null
){
}