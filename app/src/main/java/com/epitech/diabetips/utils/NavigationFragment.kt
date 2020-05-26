package com.epitech.diabetips.utils

import androidx.fragment.app.Fragment

abstract class NavigationFragment(val fragmentType: FragmentType) : Fragment() {

    enum class FragmentType {HOME, PROFILE, SETTINGS, RECIPES, DASHBOARD}

    abstract fun isLoading(): Boolean

}