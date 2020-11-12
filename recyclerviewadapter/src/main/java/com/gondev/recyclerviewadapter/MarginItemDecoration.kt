package com.gondev.recyclerviewadapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

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

class ItemDividerDecoration private constructor(
	private val dividerThicknessInDP: Float
) : RecyclerView.ItemDecoration() {

	constructor(dividerThicknessInDP: Float, dividerColor: String) : this(dividerThicknessInDP) {
		this.dividerColor = Color.parseColor(dividerColor)
	}

	constructor(dividerThicknessInDP: Float, @ColorRes dividerColorRes: Int) : this(dividerThicknessInDP) {
		this.dividerColorRes = dividerColorRes
	}

	//그릴 divider의 높이와 색상을 받는다
	private var dividerHeight: Int = -1

	private var dividerColorRes: Int? = null
	private var dividerColor: Int = -1

	//c.drawRect 에서 사용될 변수 선언
	private val paint = Paint()

	// recyclerView 보다 먼저 그려지는 함수
	override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
		if (dividerColorRes != null && dividerColor == -1)
			dividerColor = parent.context.resources.getColor(dividerColorRes!!)

		if (dividerHeight == -1)
			dividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerThicknessInDP, parent.context.resources.displayMetrics).toInt()

		myDivider(c, parent, color = dividerColor)
	}

	private fun myDivider(c: Canvas, parent: RecyclerView, color: Int) {
		paint.color = color

		for (i in 0 until parent.childCount) {
			val child = parent.getChildAt(i)
			val param = child.layoutParams as RecyclerView.LayoutParams

			val dividerTop = child.bottom + param.bottomMargin + dividerHeight

			c.drawRect(
				child.left.toFloat(),
				dividerTop.toFloat(),
				child.right.toFloat(),
				child.bottom.toFloat(),
				paint
			)
		}
	}

	//recyclerView의 측정된 자식 성격의 메소드 들을 통해 호출되고 커스텀하지 않는경우 크기가 없는 rect를 반환한다.
	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: RecyclerView.State
	) {
		val orientation= when (val layoutManager= parent.layoutManager!!){
			is LinearLayoutManager -> layoutManager.orientation
			is GridLayoutManager -> layoutManager.orientation
			is StaggeredGridLayoutManager -> layoutManager.orientation
			else -> throw UnsupportedOperationException("알 수 없는 레이아웃 메니저 입니다")
		}
		with(outRect) {
			if (parent.getChildAdapterPosition(view) > 0) {
				if(orientation==RecyclerView.HORIZONTAL)
					left = dividerHeight
				else
					top = dividerHeight
			}
		}
	}
}