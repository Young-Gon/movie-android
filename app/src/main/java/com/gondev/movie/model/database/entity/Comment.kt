package com.gondev.movie.model.database.entity

import androidx.room.*
import com.gondev.movie.model.database.converter.LocalDateTimeConverter
import com.gondev.recyclerviewadapter.ItemType
import com.gondev.movie.R
import java.time.Duration
import java.time.LocalDateTime

interface IComment {
	val id: Long
	val movieId: Long
	val writer: String
	val writer_image: String?
	val createAt: LocalDateTime?
	val modifyAt: LocalDateTime?
	val rating: Float
	val contents: String
	val recommend: Int //":0
}

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
	@PrimaryKey
	override val id: Long,
	override val movieId: Long,
	override val writer: String,
	override val writer_image: String?,
	override val createAt: LocalDateTime?, //":"2018-07-20 10:30:30",
	override val modifyAt: LocalDateTime?, // :1532050229,
	override val rating: Float, //":7.0,
	override val contents: String, //":"ㅅㅅㅅㅅ",
	override val recommend: Int //":0
) : IComment

data class CommentAndItemType(
	@Embedded
	val comment: Comment
): IComment by comment, ItemType{
	@Ignore
	override val layoutResId: Int = R.layout.item_oneline_comment
}

fun getDuration(from: LocalDateTime?) = from?.let {
	Duration.between(it, LocalDateTime.now()).let { duration ->
		when {
			duration.toDays() > 0 -> "${duration.toDays()}일 전"
			duration.toHours() > 0 -> "${duration.toHours()}시간 전"
			duration.toMinutes() > 0 -> "${duration.toMinutes()}분 전"
			else -> "방금"
		}
	} }