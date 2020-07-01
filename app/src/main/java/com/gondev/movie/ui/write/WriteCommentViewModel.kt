package com.gondev.movie.ui.write

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gondev.movie.R
import com.gondev.movie.model.database.AppDatabase
import com.gondev.movie.model.database.entity.Comment
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.model.network.api.MovieAPI
import com.gondev.movie.util.Event
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @see WriteCommentActivity
 */
class WriteCommentViewModel(
	db: AppDatabase,
	private val api: MovieAPI,
	val movie: Movie
):ViewModel() {
	private val commentDao= db.getCommentDao()

	val grade=
		when (movie.grade){
			12 -> R.drawable.ic_12
			15 -> R.drawable.ic_15
			19 -> R.drawable.ic_19
			else -> R.drawable.ic_all
		}

	val rating= MutableLiveData(0f)

	val textComment= MutableLiveData("")

	val readyToSave = MutableLiveData(true)

	val requestFinish = MutableLiveData<Event<Int>>()

	fun onClickButtonSave() {
		readyToSave.value= false

		viewModelScope.launch{
			try {
				//api.writeComment(movie.id, "yg1028", rating.value!!, textComment.value!!)
				api.writeComment(movie.id,
					Comment(0,movie.id,"yg1028",null,null,null, rating.value!!,textComment.value!!,0)
				)
				val result= api.getCommentList(movie.id)
				commentDao.insert(result)
				requestFinish.value=Event(0)
			} catch (e: Exception) {
				Timber.e(e)
				readyToSave.value = true
			}
		}
	}

	fun onClickButtonCancel() {
		requestFinish.value=Event(0)
	}
}
