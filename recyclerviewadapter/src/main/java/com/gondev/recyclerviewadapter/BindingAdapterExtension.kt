package com.gondev.recyclerviewadapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


@BindingAdapter("items")
fun <T> RecyclerView.setItems(items: List<T>?) {
    if(layoutManager==null)
        throw NullPointerException("layoutManager가 없습니다")

    (this.adapter as? RecyclerViewListAdapter<T, *>)?.run {
        submitList(items)
    }
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
