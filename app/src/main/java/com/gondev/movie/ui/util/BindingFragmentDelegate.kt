package com.gondev.movie.ui.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 프레그먼트 종류에 상관 없이 프레그먼트를 레이아웃에 바인딩할 수 있습니다
 * ``` kotlin
 * class YourFragment: Fragment(), IBindingFragmentDelegate<DataBindingObject> by BindingFragmentDelegate(R.layout.your_layout)
 * ```
 * @param T: [ViewDataBinding] -  데이터 바인딩으로 만든 바인딩 객체
 */
interface IBindingFragmentDelegate<T : ViewDataBinding>{
    /**
     * 바인딩된 [ViewDataBinding] 클레스에 접근할 수 있습니다
     */
    val binding: T

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
}

/**
 * 바인딩 인터페이스 구현체
 * @param layoutResIdRes 바인딩할 레이아웃 리소스 ID
 */
class BindingFragmentDelegate<T : ViewDataBinding>(
    @LayoutRes val layoutResIdRes: Int
): IBindingFragmentDelegate<T> {
    override lateinit var binding: T
        private set

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DataBindingUtil.inflate<T>(inflater, layoutResIdRes, container, false)
            .also { binding=it }.root
}
