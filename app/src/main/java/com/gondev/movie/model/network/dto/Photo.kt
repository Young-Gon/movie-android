package com.gondev.movie.model.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class Photo (
	val url: String,
	val isVideo: Boolean,
	val link: String?=null
){
}