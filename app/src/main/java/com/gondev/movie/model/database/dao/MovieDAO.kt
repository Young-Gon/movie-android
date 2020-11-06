package com.gondev.movie.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.model.database.entity.MovieAndItemType
import com.gondev.movie.model.network.api.OrderType

@Dao
interface MovieDAO {
	@Query("SELECT * FROM Movie ORDER BY reservation_rate DESC")
	fun findOrderByReservationRate(): LiveData<List<Movie>>

	@Query("SELECT * FROM Movie ORDER BY reviewer_rating DESC")
	fun findOrderByReviewerRating(): LiveData<List<Movie>>

	@Query("SELECT * FROM Movie ORDER BY date DESC")
	fun findOrderByDate(): LiveData<List<Movie>>

	@Query("SELECT * FROM Movie WHERE id=:movieId")
	fun findById(movieId: Long): LiveData<MovieAndItemType>

	@Insert
	suspend fun insertAllOrAbort(movies: List<Movie>)

	@Query("UPDATE Movie SET title=:title,reservation_rate=:reservation_rate,grade=:grade,date=:date,image=:image WHERE id=:id")
	suspend fun update(id: Long, title: String, reservation_rate: Float, grade: Int, date: String, image: String?)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(movie: Movie)
}


fun MovieDAO.findMovie(@OrderType orderType: Int)= when(orderType){
	OrderType.RESERVATION -> findOrderByReservationRate()
	OrderType.CURATION -> findOrderByReviewerRating()
	OrderType.SCHEDULED -> findOrderByDate()
	else -> throw IllegalArgumentException("지원 하지 않는 정렬 순서입니다 (order type=$orderType)")
}

@Transaction
suspend fun MovieDAO.update(movieList: List<Movie>){
	movieList.forEach {
		update(it.id, it.title, it.reservation_rate, it.grade, it.date, it.image)
	}
}