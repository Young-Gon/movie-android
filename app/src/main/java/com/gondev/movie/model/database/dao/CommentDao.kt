package com.gondev.movie.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gondev.movie.model.database.entity.Comment
import com.gondev.movie.model.database.entity.MovieWithCommentList

@Dao
interface CommentDao {

	@Query("SELECT * FROM comment WHERE movieId=:movieId ORDER BY modifyAt DESC LIMIT 2")
	fun findByIdLimit2(movieId: Long): LiveData<List<Comment>>

	@Transaction
	@Query("SELECT * FROM Movie WHERE id=:movieId")
	fun findMovieAndCommentList(movieId: Long): LiveData<MovieWithCommentList>

	@Query("SELECT * FROM comment WHERE movieId=:movieId ORDER BY modifyAt DESC")
	fun findById(movieId: Long): LiveData<List<Comment>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(comments: List<Comment>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(comment: Comment)
}