package com.gondev.movie.ui.util

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.CircularProgressDrawable.LARGE
import androidx.viewpager2.widget.ViewPager2
import coil.api.load
import coil.target.Target
import com.gondev.movie.R
import timber.log.Timber


@BindingAdapter("items")
fun <T> ViewPager2.setItems(items: List<T>?) {
	(this.adapter as? ListAdapter<T, *>)?.run {
		submitList(items)
	}
}

@BindingAdapter(value = ["src", "listener"],requireAll = false)
fun ImageView.bindImage(url: String?,imageLoadListener: Target?){
	if(url==null)
		return

	 load(url){
		 crossfade(true)
		 imageLoadListener?.let{ target(imageLoadListener) }
		 //target(imageLoadListener)
		 placeholder(CircularProgressDrawable(context).apply {
			 strokeWidth = 5f
			 centerRadius = 30f
			 setColorFilter(context.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN)
			 setStyle(LARGE)

			 start()
		 })
	 }
}

@BindingAdapter("src")
fun ImageView.bindImage(url: Int?){
	if(url==null)
		return

	setImageResource(url)
}

@BindingAdapter("src", "placeholder")
fun ImageView.bindImage(url: String?, placeholder: Drawable){
	load(url){
		crossfade(true)
		placeholder(placeholder)
	}
}

@BindingAdapter("visibleGone")
fun View.showHide(show: Boolean) {
	visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("onPageScrollStateChanged")
fun ViewPager2.setOnPageScrolledListener(onPageScrollStateChangedListener: OnPageScrolledListener) {
	val onPageChangeListener= object : ViewPager2.OnPageChangeCallback(){
		override fun onPageSelected(position: Int) {
			super.onPageSelected(position)
			onPageScrollStateChangedListener.onPageSelected(position)
		}
	}
	ListenerUtil.trackListener<ViewPager2.OnPageChangeCallback>(
		this,
		onPageChangeListener, R.id.currentItemSelectedListener
	)?.let {
		unregisterOnPageChangeCallback(it)
	}

	registerOnPageChangeCallback(onPageChangeListener)
}

interface OnPageScrolledListener {
	fun onPageSelected(position: Int)
}
