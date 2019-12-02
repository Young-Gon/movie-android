package com.gondev.movie.ui.util

import android.app.Activity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * ## 데이터 바인딩 작업을 대신 수행 합니다
 * 위임(Delegate) 패턴을 사용하여, 엑티비티 종류에 **상관** 없이 이 인터페이스를 상속 받아 엑티비티를 레이아웃에 바인딩할 수 있습니다
 *
 * 위임 패턴은 부모 클레스로 부터 상속 받은 기능 구현을 위임함으로서, 상속의 종속화, 다중상속, 의존성 문제등을 피하게 합니다
 *
 *  [BindingActivityDelegate] 구현체가 바인딩 작업을 대신 합니다
 *
 * 이 클레스로 데이터 바인딩하실 경우 [setContentView]를 사용하여 데이터 바인딩을 수행 하세요
 *
 * 다음과 같이 사용 합니다
 * ``` kotlin
 * class YourActivity: AppCompatActivity(),
 *     IBindingActivityDelegate<DataBindingObject> by BindingActivityDelegate(){
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContentView(this, R.layout.activity_layout)
 *     }
 * }
 * ```
 * @param T: [ViewDataBinding] -  데이터 바인딩으로 만든 바인딩 객체
 */
interface IBindingActivityDelegate<T : ViewDataBinding>{
    /**
     * 바인딩된 [ViewDataBinding] 클레스에 접근할 수 있습니다
     */
    val binding: T

    /**
     * ## 데이터 바인용 setContentView입니다
     *
     * [layoutResIdRes]를 [binding]에 바인딩 합니다
     * @param activity 바인딩할 엑티비티
     * @param layoutResIdRes 바인딩할 레이아웃 리소스
     * @see binding
     */
    fun setContentView(activity: Activity, @LayoutRes layoutResIdRes: Int)
}

/**
 * 바인딩 인터페이스 구현체
 */
class BindingActivityDelegate<T : ViewDataBinding>: IBindingActivityDelegate<T> {

    override lateinit var binding: T
        private set

    override fun setContentView(activity: Activity, @LayoutRes  layoutResIdRes: Int) {
        binding = DataBindingUtil.setContentView(activity, layoutResIdRes)
    }
}
