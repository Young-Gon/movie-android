package com.gondev.recyclerviewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil

class MultiRecyclerViewListAdapter<T, V : ViewDataBinding>(
    @LayoutRes layoutResId: Int,
    diffCallback: DiffUtil.ItemCallback<T>,
    bindingVariableId: Int? = null,
    @LayoutRes private val  headerLayoutResId: Int=0,
    @LayoutRes private val  footerLayoutResId: Int=0,
    lifecycleOwner: LifecycleOwner?=null,
    vararg param: Pair<Int, Any>
): RecyclerViewListAdapter<T, V>(layoutResId, bindingVariableId,diffCallback,lifecycleOwner,*param) {
    private val VALUE_TYPE=0
    private val HEADER_TYPE=-1
    private val FOOTER_TYPE=-2

    override fun getItemViewType(position: Int): Int {
        if(headerLayoutResId>0 && position==0)
            return HEADER_TYPE

        if(footerLayoutResId>0 && position==itemCount-1)
            return FOOTER_TYPE

        return VALUE_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<T, V> {
        if(viewType==VALUE_TYPE)
            return super.onCreateViewHolder(parent, viewType)

        val layoutResId= when (viewType) {
            HEADER_TYPE -> headerLayoutResId
            FOOTER_TYPE -> footerLayoutResId
            else -> throw IllegalAccessException("알 수 없는 뷰 타입 입니다 viteType=$viewType")
        }

        return RecyclerViewHolder<T, V>(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutResId,
                parent,
                false
            ) as V).apply {
                param.forEach {
                    setVariable(it.first, it.second)
                }
                lifecycleOwner?.let {
                    lifecycleOwner=it
                }
            }
        )
    }

    override fun submitList(list: MutableList<T>?) {
        super.submitList(addHeaderAndFooter(list))
    }

    override fun submitList(list: MutableList<T>?, commitCallback: Runnable?) {
        super.submitList(addHeaderAndFooter(list), commitCallback)
    }

    private fun addHeaderAndFooter(list: MutableList<T>?): MutableList<T>? =
        list?.toMutableList()?.apply {

            if(itemCount==0) {
                if(size == 0)
                    return@apply

                if (headerLayoutResId != 0) {
                    add(0, get(0))
                }
                if (footerLayoutResId != 0) {
                    add(last())
                }
            } else {
                if (headerLayoutResId != 0) {
                    add(0, getItem(0))
                }
                if (footerLayoutResId != 0) {
                    add(getItem(itemCount-1))
                }
            }
        }
}