package com.gondev.movie.ui.write

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.gondev.movie.R
import com.gondev.movie.databinding.WriteCommentActivityBinding
import com.gondev.movie.model.database.entity.Movie
import com.gondev.movie.ui.comments.CommentListActivity
import com.gondev.movie.ui.util.BindingActivityDelegate
import com.gondev.movie.ui.util.IBindingActivityDelegate
import com.gondev.movie.util.EventObserver
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

fun Context.startWriteCommentActivity(movie: Movie){
	startActivity(Intent(this, WriteCommentActivity::class.java).apply {
		putExtra("movie", movie)
	})
}

/**
 * @see WriteCommentViewModel
 */
class WriteCommentActivity : AppCompatActivity(),
	IBindingActivityDelegate<WriteCommentActivityBinding> by BindingActivityDelegate(){

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(this, R.layout.activity_write_comment)

		binding.vm=getViewModel {
			parametersOf(intent.getParcelableExtra("movie")
				?: throw IllegalArgumentException("movie 데이터가 없습니다"))
		}
		binding.lifecycleOwner=this

		binding.vm?.requestFinish?.observe(this, EventObserver{
			finish()
		})
	}
}
