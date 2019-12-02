package com.gondev.movie.model.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "comment",
	foreignKeys = [ForeignKey(
		entity = Movie::class,
		parentColumns = ["id"],
		childColumns = ["movieId"]
	)],
	indices = [Index("movieId")]
)
data class Comment (
	@PrimaryKey
	val id: Long,
	val movieId: Long,
	val writer: String,
	val writer_image: String? ,
	val time: String, //":"2018-07-20 10:30:30",
	val timestamp: Long, // :1532050229,
	val rating: Float, //":7.0,
	val contents: String , //":"ㅅㅅㅅㅅ",
	var recommend: Int //":0
)