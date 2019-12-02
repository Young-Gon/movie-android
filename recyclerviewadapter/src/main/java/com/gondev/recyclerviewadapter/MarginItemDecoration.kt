package com.gondev.recyclerviewadapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
	private val space: Int,

	@RecyclerView.Orientation
	private val orientation: Int
) : RecyclerView.ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View,
	                            parent: RecyclerView, state: RecyclerView.State) =
		with(outRect) {
			if (parent.getChildAdapterPosition(view) == 0) {
				if(orientation==RecyclerView.HORIZONTAL)
					left = space
				else
					top = space
			}
			if(orientation==RecyclerView.HORIZONTAL)
				right = space
			else
				bottom = space
		}
}