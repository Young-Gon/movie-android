package com.gondev.movie.model.database.entity

import androidx.room.*
import com.gondev.movie.model.database.converter.LocalDateTimeConverter
import java.time.Duration
import java.time.LocalDateTime

@TypeConverters(LocalDateTimeConverter::class)
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
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	val movieId: Long,
	val writer: String,
	val writer_image: String?,
	val createAt: LocalDateTime?, //":"2018-07-20 10:30:30",
	val modifyAt: LocalDateTime?, // :1532050229,
	val rating: Float, //":7.0,
	val contents: String, //":"ㅅㅅㅅㅅ",
	var recommend: Int //":0
)

fun getDuration(from: LocalDateTime?) = from?.let {
	Duration.between(it, LocalDateTime.now()).let { duration ->
		when {
			duration.toDays() > 0 -> "${duration.toDays()}일 전"
			duration.toHours() > 0 -> "${duration.toHours()}시간 전"
			duration.toMinutes() > 0 -> "${duration.toMinutes()}분 전"
			else -> "방금"
		}
	} }