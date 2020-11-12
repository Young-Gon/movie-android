package com.gondev.movie.ui.detail

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.gondev.movie.R
import com.gondev.movie.model.database.AppDatabase
import com.gondev.movie.model.database.dao.CommentDao
import com.gondev.movie.model.database.entity.*
import com.gondev.movie.model.network.api.MovieAPI
import com.gondev.movie.model.network.dto.Photo
import com.gondev.movie.model.util.Result
import com.gondev.movie.util.Event
import com.gondev.movie.util.isNotEmpty
import com.gondev.movie.util.orElse
import com.gondev.recyclerviewadapter.ItemType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

/**
 * 한줄평 추천 기능은 무비 상세 와 한줄평 목록에서 공통으로 쓰인다
 *
 * view(xml)에서 VM의 맴버 함수로 직접 접근할 수 있도록
 * 공통을 뽑아서 위임 페턴으로 구현 하자
 *
 * 확장 함수로 구현할 경우 파라미터가 많아서 view에서 호출이 힘들어 진다
 */
interface IClickRecommend {
    fun onClickRecommend(comment: IComment)
}

class ClickRecommend(
    private val commentDao: CommentDao,
    private val api: MovieAPI
) : IClickRecommend {

    /**
     * 한줄평 추천 등록
     * @param comment 추천할 한줄평
     */
    override fun onClickRecommend(comment: IComment) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 로그인 시스템이 없으니 일단 하드코딩을 하자
                // 구글 로그인을 사용해도 좋을 것 같다
                api.increaseRecommend(comment.id, "yg1028")
                //commentDao.insert(comment.copy(recommend = comment.recommend + 1))
                commentDao.updateRecommend(comment.id, comment.recommend + 1)
            } catch (e: java.lang.Exception) {
                Timber.e(e)
            }
        }
    }
}

/**
 * 상세화면 ViewModel
 * @see MovieDetailActivity
 */
class MovieDetailViewModel(
    private val applicationContext: Context,
    db: AppDatabase,
    private val commentDao: CommentDao,
    private val api: MovieAPI,
    movie: Movie
) : ViewModel(), IClickRecommend by ClickRecommend(commentDao, api) {
    private val movieDao = db.getMovieDao()

    val movie = liveData {
        val disposable = emitSource(movieDao.findById(movie.id))
        try {
            val detail = api.getMovieListById(movie.id)
            disposable.dispose()
            movieDao.insert(detail.copy(vote_like = movie.vote_like))
            emitSource(movieDao.findById(movie.id))
        } catch (e: java.lang.Exception) {
            Timber.e(e)
            emitSource(movieDao.findById(movie.id))
        }
    }

    val photos = this.movie.map {
        parseList(it.photos, false) + parseList(it.videos, true)
    }

    private fun parseList(listString: String?, isVideo: Boolean) =
        listString?.let {
            ArrayList<Photo>().apply {
                val tokenizer = StringTokenizer(it, ",")
                while (tokenizer.hasMoreTokens()) {
                    val url = tokenizer.nextToken()
                    if (isVideo)
                        add(
                            Photo(
                                "https://img.youtube.com/vi/${getYoutubeId(url)}/default.jpg",
                                isVideo,
                                url
                            )
                        )
                    else
                        add(Photo(url, isVideo))
                }
            }
        } ?: emptyList<Photo>()

    private fun getYoutubeId(url: String): String {
        val index = url.indexOf("=")
        return if (index != -1) {
            url.substring(index + 1)
        } else {
            url.substring(url.lastIndexOf("/") + 1)
        }
    }

    val grade = this.movie.map {
        when (it.grade) {
            12 -> R.drawable.ic_12
            15 -> R.drawable.ic_15
            19 -> R.drawable.ic_19
            else -> R.drawable.ic_all
        }
    }

    val commentList = liveData<Result<List<CommentAndItemType>>> {
        val disposable = emitSource(commentDao.findByIdLimit2(movie.id).map {
            Result.loading(it)
        })

        try {
            val result = api.getCommentList(movie.id, 2)
            disposable.dispose()
            commentDao.insert(result)
            emitSource(commentDao.findByIdLimit2(movie.id).map {
                Result.success(it)
            })
        } catch (e: java.lang.Exception) {
            Timber.e(e)
            emitSource(commentDao.findByIdLimit2(movie.id).map {
                Result.error(e, it)
            })
        }
    }

    val movieAndCommentList = MediatorLiveData<List<ItemType>>().apply {
        addSource(this@MovieDetailViewModel.movie) {
            val list = mutableListOf<ItemType>()
            list.add(it)
            commentList.value?.data.isNotEmpty {
                list.addAll(it)
            } orElse {
                list.add(EmptyCommentItemType())
            }
            list.add(MovieAndTail(it.movie))
            value = list
        }

        addSource(commentList) {
            val list = mutableListOf<ItemType>()
            it.data.isNotEmpty {
                list.addAll(it)
            } orElse {
                list.add(EmptyCommentItemType())
            }
            this@MovieDetailViewModel.movie.value?.let {
                list.add(0, it)
                list.add(MovieAndTail(it.movie))
            }
            value = list
        }
    }

    val enableLikeButton = MutableLiveData(true)

    fun onClickLike() {
        enableLikeButton.value = false
        viewModelScope.launch {
            try {
                movie.value?.movie?.let { movie ->
                    when (movie.vote_like) {
                        // 미표시 -> 좋아요~
                        Vote.NONE -> {
                            api.increaseLike(movie.id, "Y")

                            movie.copy(like = movie.like + 1, vote_like = Vote.LIKE)
                        }
                        // 좋아요~ -> 취소
                        Vote.LIKE -> {
                            api.increaseLike(movie.id, "N")

                            movie.copy(like = movie.like - 1, vote_like = Vote.NONE)
                        }
                        // 싫어요~ -> 취소, 좋아요~
                        Vote.DISLIKE -> {
                            withContext(Dispatchers.Default) { api.increaseDislike(movie.id, "N") }
                            withContext(Dispatchers.Default) { api.increaseLike(movie.id, "Y") }

                            movie.copy(
                                like = movie.like + 1,
                                dislike = movie.dislike - 1,
                                vote_like = Vote.LIKE
                            )
                        }
                        else -> throw IllegalArgumentException("잘못된 접근입니다")
                    }
                    movieDao.insert(movie)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "네트워크 통신에 실패 하였습니다. 잠시후에 다시 시도해 주세요",
                    Toast.LENGTH_LONG
                ).show()
            }
            enableLikeButton.value = true
        }
    }

    fun onClickDislike() {
        enableLikeButton.value = false
        viewModelScope.launch {
            try {
                movie.value?.movie?.let { movie ->
                    when (movie.vote_like) {
                        // 미표시 -> 싫어요~
                        Vote.NONE -> {
                            api.increaseDislike(movie.id, "Y")

                            movie.copy(dislike = movie.dislike + 1, vote_like = Vote.DISLIKE)
                        }
                        // 싫어요~ -> 취소
                        Vote.DISLIKE -> {
                            api.increaseDislike(movie.id, "N")

                            movie.copy(dislike = movie.dislike - 1, vote_like = Vote.NONE)
                        }
                        // 좋아요~ -> 취소, 싫어요~
                        Vote.LIKE -> {
                            withContext(Dispatchers.Default) { api.increaseLike(movie.id, "N") }
                            withContext(Dispatchers.Default) { api.increaseDislike(movie.id, "Y") }

                            movie.copy(
                                dislike = movie.dislike + 1,
                                like = movie.like - 1,
                                vote_like = Vote.DISLIKE
                            )
                        }
                        else -> throw IllegalArgumentException("잘못된 접근입니다")
                    }
                    movieDao.insert(movie)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    applicationContext,
                    "네트워크 통신에 실패 하였습니다. 잠시후에 다시 시도해 주세요",
                    Toast.LENGTH_LONG
                ).show()
            }
            enableLikeButton.value = true
        }
    }

    val requestWriteActivity = MutableLiveData<Event<Movie>>()
    fun onClickWrite() {
        requestWriteActivity.value = Event(movie.value?.movie!!)
    }

    val clickShowAll = MutableLiveData<Event<Movie>>()
    fun onClickShowAll() {
        clickShowAll.value = Event(movie.value?.movie!!)
    }

    val requestOpenGallery = MutableLiveData<Event<Photo>>()
    fun onClickPhoto(photo: Photo) {
        requestOpenGallery.value = Event(photo)
    }
}