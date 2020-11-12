package com.gondev.recyclerviewadapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

@BindingAdapter("adapter")
fun <T> RecyclerView.setAdapterBinding(adapter: ListAdapter<T, *>) {
    if(this.adapter==null)
        this.adapter = adapter
}

@BindingAdapter("items")
fun <T> RecyclerView.setItems(items: List<T>?) {
    if(layoutManager==null)
        throw NullPointerException("layoutManager가 없습니다")

    (this.adapter as? ListAdapter<T,*>)?.run {
        submitList(items)
    }
}

@BindingAdapter("snapHelper")
fun RecyclerView.setSnapHelper(snapHelper: SnapHelper) {
    snapHelper.attachToRecyclerView(this)
}

@BindingAdapter("itemMargin")
fun RecyclerView.setItemMargin(@DimenRes margin: Int) =
    addItemDecoration(MarginItemDecoration(resources.getDimension(margin).toInt(),
        (layoutManager as LinearLayoutManager).orientation))

@BindingAdapter("itemMargin")
fun RecyclerView.setItemMargin(margin: Double) =
    addItemDecoration(MarginItemDecoration(context.dpToPx(margin.toFloat()),
        (layoutManager as LinearLayoutManager).orientation))


fun Context.dpToPx(dp: Float) = resources.displayMetrics.let {
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,it).toInt()
}

@BindingAdapter("hasFixedSize")
fun RecyclerView.hasFixedSize(fix: Boolean) {
    setHasFixedSize(fix)
}

@BindingAdapter("selected")
fun View.select(selected: Boolean) {
    isSelected = selected
}
