package com.gondev.recyclerviewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.*
import java.lang.IllegalArgumentException
import kotlin.math.min
import kotlin.math.max


const val BINDING_VARIABLE_ID = -1

open class MultiViewDataBindingAdapter(
    protected val lifecycleOwner: LifecycleOwner,
    vararg param: Pair<Int, List<Pair<Int, Any>>>
) : ListAdapter<ItemType, RecyclerViewHolder<ItemType>>(object : DiffUtil.ItemCallback<ItemType>() {
    override fun areItemsTheSame(oldItem: ItemType, newItem: ItemType) =
        oldItem.layoutResId == newItem.layoutResId && oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ItemType, newItem: ItemType) =
        oldItem == newItem
}) {

    private val layoutData = mapOf(*param)

    override fun getItemViewType(position: Int) = getItem(position).layoutResId

    override fun onCreateViewHolder(
        parent: ViewGroup,
        layoutResId: Int
    ): RecyclerViewHolder<ItemType> {
        var bindingVariableId = 0
        return RecyclerViewHolder(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutResId,
                parent,
                false
            ) as ViewDataBinding).apply {
                layoutData[layoutResId]?.forEach {
                    if (it.second == BINDING_VARIABLE_ID)
                        bindingVariableId = it.first
                    else
                        setVariable(it.first, it.second)
                }
                lifecycleOwner = this@MultiViewDataBindingAdapter.lifecycleOwner
            }, bindingVariableId
        )
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder<ItemType>, position: Int) =
        holder.onBindViewHolder(getItem(position))
}

interface ItemType {
    val layoutResId: Int
    val id: Long
    override fun equals(other: Any?): Boolean
}

class GridDataBindingAdapter(
    lifecycleOwner: LifecycleOwner,
    val spanCount: Int,
    vararg param: Pair<Int, List<Pair<Int, Any>>>
) : MultiViewDataBindingAdapter(lifecycleOwner, *param) {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val layoutManager = recyclerView.layoutManager
        if (layoutManager == null || layoutManager !is GridLayoutManager)
            throw IllegalArgumentException("GridLayoutManager가 필요 합니다")

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = getItem(position)
                if (item is GridItemType)
                    return min(layoutManager.spanCount, max(1, item.spanSize))

                return 1
            }
        }
    }
}

/**
 * @see SpanSizeLookup
 */
interface GridItemType : ItemType {
    val spanSize: Int
}