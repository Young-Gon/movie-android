package com.gondev.movie.model.database

import android.content.Context
import android.graphics.Region
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gondev.movie.model.database.AppDatabase.Companion.DB_VERSION
import com.gondev.movie.model.database.dao.CommentDao
import com.gondev.movie.model.database.dao.MovieDAO
import com.gondev.movie.model.database.entity.Comment
import com.gondev.movie.model.database.entity.Movie


@Database(
	entities = [
		Movie::class,
		Comment::class
	],
	version = DB_VERSION,
	exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
	abstract fun getMovieDao(): MovieDAO
	abstract fun getCommentDao(): CommentDao

	companion object {
		const val DB_VERSION = 1
		private const val DB_NAME = "movie_model.db"
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun getInstance(context: Context): AppDatabase =
			INSTANCE ?: synchronized(this) {
				INSTANCE
					?: build(context).also { INSTANCE = it }
			}

		private fun build(context: Context) =
			Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java,
				DB_NAME
			).build()
	}
}