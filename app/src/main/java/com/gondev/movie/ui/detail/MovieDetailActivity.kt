package com.gondev.movie.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NavUtils
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import coil.api.load
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.gondev.movie.BR
import com.gondev.movie.R
import com.gondev.movie.databinding.HorizontalGalleryItemBinding
import com.gondev.movie.databinding.MovieDetailActivityBinding
import com.gondev.movie.databinding.OneLineCommentItemBinding
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.model.network.dto.Photo
import com.gondev.movie.model.util.Result
import com.gondev.movie.ui.comments.startCommentListActivity
import com.gondev.movie.ui.main.MainActivity
import com.gondev.movie.ui.util.BindingActivityDelegate
import com.gondev.movie.ui.util.IBindingActivityDelegate
import com.gondev.movie.ui.write.startWriteCommentActivity
import com.gondev.movie.util.EventObserver
import com.gondev.recyclerviewadapter.RecyclerViewListAdapter
import kotlinx.android.synthetic.main.activity_movie_detail.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf


fun Context.startMovieDetailActivity(movie: Movie){
	startActivity(Intent(this,MovieDetailActivity::class.java).apply {
		putExtra("movie", movie)
	})
}

/**
 * 트렌지션(화면 변경)과 함께 상세화면 시작
 *
 * @param 상세화면에서 표시할 [Movie] 데이터
 * @param sharedElement 화면에 표시되어 있는 Poster View
 */
fun MainActivity.startMovieDetailActivity(movie: Movie, sharedElement:View){
	val option= ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElement,ViewCompat.getTransitionName(sharedElement)!!)
	startActivity(Intent(this,MovieDetailActivity::class.java).apply {
		putExtra("movie", movie)
	}, option.toBundle())
}

/**
 * 상세 엑티비티
 * @see MovieDetailViewModel
 */
class MovieDetailActivity : AppCompatActivity(),
	IBindingActivityDelegate<MovieDetailActivityBinding> by BindingActivityDelegate(){

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(this, R.layout.activity_movie_detail)
		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		val movie: Movie= intent.getParcelableExtra("movie")
			?: throw IllegalArgumentException("movie 데이터가 없습니다")

		// Seen Transition을 위한 이미지 리스너 등록
		imgPoster.transitionName = movie.id.toString()
		imgPoster.load(movie.image)

		// VM 생성
		binding.vm=getViewModel {
			parametersOf(movie)
		}

		binding.lifecycleOwner=this

		// photo recyclerview 아답타 등록
		horizontalRecyclerView.adapter=RecyclerViewListAdapter<Photo, HorizontalGalleryItemBinding>(
			R.layout.item_horizontal_gallery,
			BR.photo,
			object: DiffUtil.ItemCallback<Photo>(){
				override fun areItemsTheSame(oldItem: Photo, newItem: Photo) =
					oldItem == newItem

				override fun areContentsTheSame(oldItem: Photo, newItem: Photo) =
					oldItem == newItem
			},
			this,
			BR.vm to binding.vm!!
		)

		// photoRecyclerView에 아이템이 이쁘게 정렬 되도록 snapHelper 등록
		GravitySnapHelper(Gravity.START).attachToRecyclerView(horizontalRecyclerView)

		// 한줄평 불러오기 완료시 동적으로 한줄평 addView
		binding.vm?.commentList?.observe(this, Observer { result ->
			layout_oneline_comment.removeAllViews()

			// 한줄평 예외 처리
			if(result?.data == null || result.data.isEmpty()) {
				layout_oneline_comment.addView(TextView(this@MovieDetailActivity).apply {
					text = when (result) {
						null, is Result.Loading -> "한줄평을 가저 오는 중입니다..."
						is Result.Success -> "한줄평이 없습니다."
						is Result.Error -> "네트워크 오류 입니다"
					}
				})
				return@Observer
			}

			// 한줄평 동적 등록
			result.data.forEach{
				DataBindingUtil.inflate<OneLineCommentItemBinding>(
					layoutInflater,
					R.layout.item_oneline_comment,
					layout_oneline_comment,
					true
				).apply {
					vm = binding.vm
					comment = it
				}
			}
		})

		// 한줄평 쓰기
		binding.vm?.requestWriteActivity?.observe(this, EventObserver{
			startWriteCommentActivity(it)
		})

		// 한줄평 모두 보기
		binding.vm?.clickShowAll?.observe(this, EventObserver{
			startCommentListActivity(it)
		})
	}

	override fun onOptionsItemSelected(item: MenuItem) =
		when (item.itemId) {
			android.R.id.home -> {
				NavUtils.navigateUpFromSameTask(this)
				true
			}
			else -> super.onOptionsItemSelected(item)
		}

	override fun onBackPressed() {
		// 역 트렌지션 수행
		supportFinishAfterTransition()
		super.onBackPressed()
	}
}
