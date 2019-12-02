package com.gondev.movie.ui.comments

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.DiffUtil
import com.gondev.movie.BR
import com.gondev.movie.R
import com.gondev.movie.databinding.CommentListActivityBinding
import com.gondev.movie.databinding.OneLineCommentItemBinding
import com.gondev.movie.model.database.entity.Comment
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.ui.detail.MovieDetailActivity
import com.gondev.movie.ui.util.BindingActivityDelegate
import com.gondev.movie.ui.util.IBindingActivityDelegate
import com.gondev.movie.ui.write.startWriteCommentActivity
import com.gondev.movie.util.EventObserver
import com.gondev.recyclerviewadapter.RecyclerViewListAdapter
import kotlinx.android.synthetic.main.activity_comment_list.*
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.activity_movie_detail.toolbar
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf


fun Context.startCommentListActivity(movie: Movie){
	startActivity(Intent(this, CommentListActivity::class.java).apply {
		putExtra("movie", movie)
	})
}

/**
 * @see CommentListViewModel
 */
class CommentListActivity : AppCompatActivity(),
	IBindingActivityDelegate<CommentListActivityBinding> by BindingActivityDelegate(){

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(this, R.layout.activity_comment_list)

		setSupportActionBar(toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		binding.vm=getViewModel {
			parametersOf(intent.getParcelableExtra("movie")
				?: throw IllegalArgumentException("movie 데이터가 없습니다"))
		}
		binding.lifecycleOwner=this

		recyclerView.adapter=RecyclerViewListAdapter<Comment, OneLineCommentItemBinding>(
			R.layout.item_oneline_comment,
			BR.comment,
			object : DiffUtil.ItemCallback<Comment>(){
				override fun areItemsTheSame(oldItem: Comment, newItem: Comment) =
					oldItem.id == newItem.id

				override fun areContentsTheSame(oldItem: Comment, newItem: Comment) =
					oldItem == newItem
			},
			this,
			BR.vm to binding.vm!!
		)

		binding.vm?.requestWriteActivity?.observe(this, EventObserver{
			startWriteCommentActivity(it)
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
}
