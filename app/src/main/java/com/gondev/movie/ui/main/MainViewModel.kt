package com.gondev.movie.ui.main

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.*
import com.gondev.movie.model.database.AppDatabase
import com.gondev.movie.model.database.dao.findMovie
import com.gondev.movie.model.database.dao.update
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.model.network.api.MovieAPI
import com.gondev.movie.model.network.api.OrderType
import com.gondev.movie.model.util.Result
import com.gondev.movie.util.Event
import timber.log.Timber

/**
 * [MainActivity]와 연결된 [ViewModel] 무비의 목록을 관리한다
 *@see MainActivity
 */
class MainViewModel(
	db: AppDatabase,
	private val api: MovieAPI
):ViewModel() {
	private val dao=db.getMovieDao()

	/**
	 * 무비 정렬 순서
	 *
	 * @see OrderType
	 */
	val movieListOrderType = MutableLiveData<@OrderType Int>(OrderType.RESERVATION)

	/**
	 * 무비 정렬 순서가 변경되면 변경된 정렬 순서대로 정렬 한다
	 */
	val movieList= movieListOrderType.switchMap { orderType ->
		// 코루틴스콥을 생성하여 코루틴을 수행하고 그 결과를 LiveData에 저장(emit)한다
		liveData<Result<List<Movie>>> {
			// 1. 디비 조회(LiveData -> LiveData) Loading으로 emit(LiveData에 저장)한다
			val disposable= emitSource(dao.findMovie(orderType).map{
				Result.loading(it)
			})

			try {
				// 2. 서버 콜
				val movieList= api.getMovieList(orderType)
				// loading으로 dispatch되는 것을 막는다
				disposable.dispose()

				try {
					// 디테일이 업데이트 되고 나서 리스트로 돌아 오면
					// 리스트의 데이터가 다시 삽입 되어 디테일의 데이터가 없어지는 현상이 발생
					// 이를 막기 위해 ConflictStrategy를 Abort로 설정하여
					// 중복 키 삽입시 실패 하도록 한다
					dao.insertAllOrAbort(movieList)
				} catch (exception: SQLiteConstraintException)
				{
					// 그리고 필요한 항목만 업데이트 한다
					dao.update(movieList)
				}
				// 3. 디비 업데이트 후 success로 emit한다
				// 이후 success로 데이터가 변경 되는 것을 확인할 수 있다
				emitSource(dao.findMovie(orderType).map{
					Result.success(it)
				})
			}catch (e: Exception){
				Timber.e(e)

				// 'emit' 콜은 이전 dispatch를 dispose 한다
				// 그래서 여기서 dispose할 필요가 없다
				emitSource(dao.findMovie(orderType).map{
					Result.error(e, it)
				})
			}
		}
	}

	/**
	 * ViewPager의 현재 페이지
	 */
	val currentViewPage= MutableLiveData(0)
	fun onPageSelected(position: Int){
		currentViewPage.value=position
	}

	/**
	 * 상세 버튼 클릭
	 * 상세 화면 시작에 필요한 데이터를 수집하여 [MainActivity]로 보낸다
	 */
	val onClickMovieDetailEvent= MutableLiveData<Event<Movie>>()
	fun onClickDetail(){
		movieList.value?.data?.let { list ->
			onClickMovieDetailEvent.value=Event(list[currentViewPage.value?:0])
		}
	}
}