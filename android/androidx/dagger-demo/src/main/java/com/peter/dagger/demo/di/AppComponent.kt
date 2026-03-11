package com.peter.dagger.demo.di

import com.peter.dagger.demo.MainActivity
import com.peter.dagger.demo.ui.fragment.AndroidFragment
import com.peter.dagger.demo.ui.fragment.BasicFragment
import com.peter.dagger.demo.ui.fragment.QualifierFragment
import com.peter.dagger.demo.ui.fragment.ScopeFragment
import com.peter.dagger.demo.ui.fragment.SubcomponentFragment

/**
 * AppComponent - 应用级 Dagger 组件
 *
 * 手动依赖注入示例
 * 不使用 Dagger 注解处理器，直接手动创建依赖图
 */
class AppComponent {

    // 提供给 Activity 使用的注入方法
    // 注意：在手动 DI 中，我们直接在容器中获取依赖
    fun inject(activity: MainActivity) {
        // 从容器中获取依赖
        }

    // 提供给 Fragment 使用的注入方法
    fun inject(fragment: BasicFragment) {
        // 从容器中获取依赖
    }

    fun inject(fragment: ScopeFragment) {
        // 从容器中获取依赖
    }

    fun inject(fragment: QualifierFragment) {
        // 从容器中获取依赖
    }

    fun inject(fragment: SubcomponentFragment) {
        // 从容器中获取依赖
    }

    fun inject(fragment: AndroidFragment) {
        // 从容器中获取依赖
    }
}
