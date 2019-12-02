package com.gondev.recyclerviewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


/**
 * MVVM 페턴으로 제작하고, 리스트 및 [ViewHolder][RecyclerView.ViewHolder]를
 * 내장한 범용 [adapter][RecyclerView.adapter]
 *
 * 데이터 바인딩을 사용할 경우, [ViewHolder][RecyclerView.ViewHolder]가 수행하는 뷰바인딩 역할을
 * 데이터 바인딩으로 떠넘기고, [ViewHolder][RecyclerView.ViewHolder]의 역할을 축소 시킴으로서
 * [ViewHolder][RecyclerView.ViewHolder]의 생성 및 사용을 내부적으로 자동화 하였다
 *
 * 결론적으로, [ViewHolder][RecyclerView.ViewHolder] 및 [adapter][RecyclerView.adapter]의
 * 상속 및 구현없이 재사용이 가능한 [adapter][RecyclerView.adapter]를 구현하였다
 *
 * [RecyclerView] 내의 Item layout과 Item 객체를 Databinding하여 재사용성을 증가 시키는 것이 목적이다
 *
 * DataBinding을 사용하고, [ViewModel][androidx.lifecycle.ViewModel]을 사용할 경우, 다음과 같이 사용한다
 *
 * ```
 * binding.vm = getViewModel() // binding 객체에 VM 전달
 * binding.lifecycleOwner = this  // 데이터 변화를 관찰 하기 위해 binding 객체에 lifecycleOwner 등록
 * binding.Recyclerview.adapter =
 *     RecyclerViewListAdapter<Item, ItemBinding>(
 *         R.layout.item,
 *         BR.item,
 *         object : DiffUtil.ItemCallback<Item>() {
 *             override fun areItemsTheSame(oldItem: Item, newItem: Item) =
 *                 oldItem.id == newItem.id
 *
 *             override fun areContentsTheSame(oldItem: Item, newItem: Item) =
 *                 oldItem == newItem
 *
 *             },
 *             this, // lifecycleOwner 를 전달하여 View 내부의 데이터 변화 관찰
 *             BR.vm to binding.vm!! // 추가적인 파리미터 전달
 *         )
 * ```
 * R.layout.item은 다음과 같이 되어 있다
 * ```
 * <layout xmlns:android="http://schemas.android.com/apk/res/android"
 *         xmlns:app="http://schemas.android.com/apk/res-auto"
 *         xmlns:tools="http://schemas.android.com/tools">
 *     <data class="ItemBinding">
 *         <variable
 *             name="item"
 *             type="com.sample.data.Item"/>
 *
 *         <variable
 *             name="vm"
 *             type="com.sample.viewmodel.ViewModel"/>
 *
 *     </data>
 * </layout>
 * ```
 * @param T 리스트의 아이템(Model) 타입
 * @param V 아이템 레이아웃(View)이 생성한 ViewDataBinding 클래스
 * @param layoutResId 아이템 레이아웃(View)의 리소스 ID
 * @param bindingVariableId 레이아웃의 data 영역에서 바인딩될 아이템(Model)의 변수(variable)의 ID
 * @param diffCallback 아이템의 변경 사항을 구분하기 위해 변경여부를 알려주기 위한 콜백 클래스
 * @param param OPTIONAL (가변 인자) 레이아웃의 data 영역에 추가로 바인딩 될 변수의 ID와 Value 쌍
 * @see RecyclerViewHolder
 * @see MultiRecyclerViewListAdapter
 */
open class RecyclerViewListAdapter<T, V : ViewDataBinding>(
	@LayoutRes private val layoutResId: Int,
	protected val bindingVariableId: Int? = null,
	diffCallback: DiffUtil.ItemCallback<T>,
	protected val lifecycleOwner: LifecycleOwner?=null,
	protected vararg val param: Pair<Int, Any>
) : ListAdapter<T, RecyclerViewHolder<T, V>>(diffCallback) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		RecyclerViewHolder<T, V>(
			(DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				layoutResId,
				parent,
				false
			) as V).apply {
				param.forEach {
					setVariable(it.first, it.second)
				}
				this@RecyclerViewListAdapter.lifecycleOwner?.let {
					lifecycleOwner = it
				}
			}, bindingVariableId
		)

	override fun onBindViewHolder(holder: RecyclerViewHolder<T, V>, position: Int) =
		holder.onBindViewHolder(getItem(position))
}

/**
 *[RecyclerViewListAdapter]에서 사용하는 [ViewHolder][RecyclerView.ViewHolder]
 *
 * @param T 아이템 타입
 * @param V 아이템 레이아웃(View)이 생성한 ViewDataBinding 클래스
 * @param binding 아이템의 ViewDataBinding 인스턴스
 * @param bindingVariableId 아이템의 ViewDataBinding 인스턴스에서 사용하는 아이템의 BR
 * @see RecyclerViewListAdapter
 */
class RecyclerViewHolder<in T, out V : ViewDataBinding>(
	val binding: V,
	private val bindingVariableId: Int? = null
) : RecyclerView.ViewHolder(binding.root) {

	fun onBindViewHolder(item: T?) {
		try {
			bindingVariableId?.let {
				binding.setVariable(it, item)
				binding.executePendingBindings();
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}
