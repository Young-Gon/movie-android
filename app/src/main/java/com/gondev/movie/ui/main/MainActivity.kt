package com.gondev.movie.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.gondev.movie.BR
import com.gondev.movie.R
import com.gondev.movie.databinding.MainActivityBinding
import com.gondev.movie.databinding.MovieViewpagerItemBinding
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.model.network.api.OrderType
import com.gondev.movie.ui.detail.startMovieDetailActivity
import com.gondev.movie.ui.util.BindingActivityDelegate
import com.gondev.movie.ui.util.IBindingActivityDelegate
import com.gondev.movie.util.EventObserver
import com.gondev.movie.util.dpToPx
import com.gondev.recyclerviewadapter.RecyclerViewHolder
import com.gondev.recyclerviewadapter.RecyclerViewListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * 메인 엑티비티 입니다
 *
 * 내부에 [ViewPager2]를 갖고 있으며, [RecyclerViewListAdapter]를 이용하여 코드 제사용성을 증가 시켰습니다
 * 대리자(Delegate) 패턴을 사용하여 [MainActivityBinding]에 바인딩합니다
 *
 * @see MainViewModel
 * @see IBindingActivityDelegate
 */
class MainActivity : AppCompatActivity(),
	IBindingActivityDelegate<MainActivityBinding> by BindingActivityDelegate() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(this, R.layout.activity_main)

		// 뷰 모델 생성 및 바인딩
		binding.vm=getViewModel()
		binding.lifecycleOwner=this
		setSupportActionBar(toolbar)

		// ViewPager 좌우에 다음 무비의 포스터가 보이게끔 마진 조절
		viewPager.offscreenPageLimit=2
		val pageMarginPx = 10.dpToPx(resources.displayMetrics)
		val marginTransformer = MarginPageTransformer(pageMarginPx)
		viewPager.setPageTransformer(marginTransformer)

		// 아답터 생성 RecyclerViewListAdapter를 사용하면 아답터 구현 없이 간편하게 사용 가능하다
		viewPager.adapter=object : RecyclerViewListAdapter<Movie>(
			R.layout.item_movie_viewpager,
			BR.movie,
			object : DiffUtil.ItemCallback<Movie>() {
				override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
					oldItem.id == newItem.id

				override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
					oldItem == newItem
			},
			this,
			BR.vm to binding.vm!!
		) {
			override fun onBindViewHolder(
				holder: RecyclerViewHolder<Movie>,
				position: Int
			) {
				super.onBindViewHolder(holder, position)
				// 포스터 트랙지션을 위하여 onBindViewHolder를 변경하였다
				holder.binding.root.transitionName = getItem(position).id.toString()
			}
		}.apply {
			// 아이템 위치가 변경되면 recyclerView의 기본 아이템 변경 에니메이션이 호출된다
			// 아이템 이동일 경우 화면 첫번째 아이템이 이동한 위치로 스크롤 된다
			// 여기서는 ViewPager를 사용하므로 이렬경우, 리스트가 뒤죽 박죽되는 경험을 하게 된다
			// 이걸 막고자 AdapterDataObserver를 달고 스크롤이 되면 원위치로 다시 스크롤 해주자
			registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
				override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
					val currentItem=viewPager.currentItem
					viewPager.getInnerRecyclerView().scrollToPosition(currentItem)
				}
			})
		}

		// 상세버튼 클릭 이벤트
		binding.vm?.onClickMovieDetailEvent?.observe(this, EventObserver{ movie ->
			val innerRecyclerView = viewPager.getInnerRecyclerView()
			val view=(innerRecyclerView.layoutManager as LinearLayoutManager).findViewByPosition(viewPager.currentItem)?:
				throw NullPointerException()

			startMovieDetailActivity(movie, view)
		})
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		binding.vm?.movieListOrderType?.value = when(item.itemId){
			R.id.menu_curation -> OrderType.CURATION
			R.id.menu_reservation -> OrderType.RESERVATION
			R.id.menu_schedule -> OrderType.SCHEDULED
			else -> return false
		}
		return true
	}
}

/**
 * [ViewPager2]는 내부적으로 [RecyclerView]를 갖고 있고 대부분의 구현을 이 RecyclerView가 하고 있다
 * ViewPager2가 재공하지 못하는 기능들은 내부 RecyclerView를 받아와서 처리해아 한다
 */
fun ViewPager2.getInnerRecyclerView() =
	getChildAt(0) as RecyclerView