package com.gondev.movie.model.network.api

import androidx.annotation.IntDef
import com.gondev.movie.model.database.entity.Comment
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.model.network.api.OrderType.Companion.CURATION
import com.gondev.movie.model.network.api.OrderType.Companion.RESERVATION
import com.gondev.movie.model.network.api.OrderType.Companion.SCHEDULED
import retrofit2.http.*


@IntDef(RESERVATION, CURATION, SCHEDULED)
@Retention(AnnotationRetention.SOURCE)
annotation class OrderType{
	companion object{
		const val RESERVATION=1
		const val CURATION=2
		const val SCHEDULED=3
	}
}

/**
 * 무비 서버 RESTFull API 목록
 */
interface MovieAPI {

	/**
	 * 무비 목록
	 */
	@GET("movie")
	suspend fun getMovieList(@Query("type") @OrderType type: Int): List<Movie>

	/**
	 * 무비 상세
	 */
	@GET("movie/{id}")
	suspend fun getMovieListById(@Path("id") id: Long): Movie

	/**
	 * 무비 상세 (이름으로 찾기)
	 */
	@GET("movie/search")
	suspend fun getMovieListByName(@Query("name") name: String): List<Movie>

	/**
	 * 무비의 좋아요 등록
	 */
	@PUT("movie/{id}/increaseLike")
	suspend fun increaseLike(@Path("id") id: Long, @Query("likeyn") likeyn: String)

	/**
	 * 무비의 싫어요 등록
	 */
	@PUT("movie/{id}/increaseDislike")
	suspend fun increaseDislike(@Path("id") id: Long, @Query("dislikeyn") dislikeyn: String)

	/**
	 * 무비 한줄평 목록
	 */
	@GET("movie/{id}/comment")
	suspend fun getCommentList(@Path("id") movieId: Long, @Query("limit") limit: Int? = null): List<Comment>

	/**
	 * 한줄평 추천 등록
	 */
	@GET("comment/{id}/recommend")
	suspend fun increaseRecommend(@Path("id") commentId: Long, @Query("writer") userId: String)

	/**
	 * 한줄평 쓰기
	 */
	@POST("movie/{moviId}/comment")
	suspend fun writeComment(@Path("moviId") moviId: Long, @Query("writer") writer: String, @Query("rating") rating: Float, @Query("contents") contents: String): Long

	@POST("movie/{moviId}/comment")
	suspend fun writeComment(@Path("moviId") moviId: Long, @Body comment: Comment): Long
}