package com.gondev.movie.model.database.entity

import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.room.*
import com.gondev.movie.R
import com.gondev.movie.model.database.entity.Vote.Companion.DISLIKE
import com.gondev.movie.model.database.entity.Vote.Companion.LIKE
import com.gondev.movie.model.database.entity.Vote.Companion.NONE
import com.gondev.recyclerviewadapter.ItemType
import kotlinx.android.parcel.Parcelize


@IntDef(LIKE, NONE, DISLIKE)
@Retention(AnnotationRetention.SOURCE)
annotation class Vote{
	companion object{
		const val LIKE=1
		const val NONE=0
		const val DISLIKE=-1
	}
}

interface IMovie{

	val id: Long
	/**
	 * 제목
	 */
	val title: String?
	/**
	 * 영문 제목
	 */
	val title_eng: String?
	/**
	 * 개봉연월일 (yyyy-MM-dd)
	 */
	val date: String?
	/**
	 * 사용자 평점
	 */
	val user_rating: Float
	/**
	 * 관람객 평점
	 */
	val audience_rating: Float
	/**
	 * 기자, 평론가 평점
	 */
	val reviewer_rating: Float
	/**
	 * 예매율
	 */
	val reservation_rate: Float
	/**
	 * 예매율 순위
	 */
	val reservation_grade: Int
	/**
	 * 관람등급
	 */
	val grade: Int
	/**
	 * 썸네일 이미지 링크
	 */
	val thumb: String?
	/**
	 * 원본 이미지 링크
	 */
	val image: String?
	/**
	 * 포토
	 */
	val photos: String?
	/**
	 * 비디오
	 */
	val videos: String?
	/**
	 * 장르
	 */
	val genre: String?
	/**
	 * 러닝 타임
	 */
	val duration: Int
	/**
	 * 누적 관객수
	 */
	val audience: Long
	/**
	 * 줄거리
	 */
	val synopsis: String?
	/**
	 * 감독
	 */
	val director: String?
	/**
	 * 배우
	 */
	val actor: String?
	/**
	 * 좋아요
	 */
	val like: Long
	/**
	 * 싫어요
	 */
	val dislike: Long
	/**
	 * 좋아요 표시 여부 (LIKE:좋아요, NONE: 미표시, DISLIKE: 싫어요)
	 */
	@Vote
	val vote_like: Int
}

@Parcelize
@Entity
data class Movie (
	@PrimaryKey
	override val id: Long ,
	override val title: String,
	override val title_eng: String?,
	override val date: String,
	override val user_rating: Float,
	override val audience_rating: Float,
	override val reviewer_rating: Float,
	override val reservation_rate: Float,
	override val reservation_grade: Int,
	override val grade: Int,
	override val thumb: String?,
	override val image: String?,
	override val photos: String?,
	override val videos: String?,
	override val genre: String?,
	override val duration: Int,
	override val audience: Long,
	override val synopsis: String?,
	override val director: String?,
	override val actor: String?,
	override val like: Long,
	override val dislike: Long,
	@Vote
	override val vote_like: Int
):IMovie, Parcelable

data class MovieAndItemType(
	@Embedded
	val movie: Movie
): IMovie by movie, ItemType{
	@Ignore
	override val layoutResId: Int = R.layout.item_movie_detail_header
}

data class MovieAndTail(
	@Embedded
	val movie: Movie
): IMovie by movie, ItemType{
	@Ignore
	override val layoutResId: Int = R.layout.item_movie_detail_tail
}

class MovieAndComment(
	@Embedded
	val movie: Movie,

	@Embedded
	val comment: Comment
): IMovie by movie

class MovieWithCommentList(
	@Embedded
	val movie: Movie,

	@Relation(entity = Comment::class, parentColumn = "id", entityColumn = "movieId")
	val commentList: List<Comment>
): IMovie by movie