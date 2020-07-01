package com.gondev.movie.ui.comments

import androidx.lifecycle.*
import com.gondev.movie.R
import com.gondev.movie.model.database.AppDatabase
import com.gondev.movie.model.database.dao.CommentDao
import com.gondev.movie.model.database.entity.Comment
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.model.network.api.MovieAPI
import com.gondev.movie.model.util.Result
import com.gondev.movie.ui.detail.ClickRecommend
import com.gondev.movie.ui.detail.IClickRecommend
import com.gondev.movie.util.Event
import timber.log.Timber

/**
 * @see CommentListActivity
 */
class CommentListViewModel(
	db: AppDatabase,
	private val api: MovieAPI,
	private val commentDao: CommentDao,
	val movie: Movie
):ViewModel(), IClickRecommend by ClickRecommend(commentDao, api)  {

	val commentList: LiveData<Result<List<Comment>>> = liveData {
		val disposable = emitSource(commentDao.findById(movie.id).map {
			Result.loading(it)
		})

		try {
			val result=api.getCommentList(movie.id)
			disposable.dispose()
			commentDao.insert(result)
			emitSource(commentDao.findById(movie.id).map {
				Result.success(it)
			})
		} catch (e: Exception) {
			Timber.e(e)
			emitSource(commentDao.findById(movie.id).map {
				Result.error(e, it)
			})
		}
	}

	val grade=
		when (movie.grade){
			12 -> R.drawable.ic_12
			15 -> R.drawable.ic_15
			19 -> R.drawable.ic_19
			else -> R.drawable.ic_all
		}

	val requestWriteActivity = MutableLiveData<Event<Movie>>()
	fun onClickWriteComment(){
		requestWriteActivity.value = Event(movie)
	}
}