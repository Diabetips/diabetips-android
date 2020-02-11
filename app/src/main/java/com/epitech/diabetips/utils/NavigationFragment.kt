package com.epitech.diabetips.utils

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class NavigationFragment(val fragmentType: FragmentType) : Fragment() {

    enum class FragmentType {HOME, PROFILE, SETTINGS}

    abstract fun isLoading(): Boolean

}