package com.epitech.diabetips.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class ANavigationFragment(val fragmentType: FragmentType) : Fragment() {

    enum class FragmentType {HOME, DASHBOARD, RECIPES, PROFILE, SETTINGS}

    protected fun createFragmentView(layoutId: Int, inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(layoutId, container, false)
        MaterialHandler.handleTextInputLayoutSize(view as ViewGroup)
        return view;
    }

    abstract fun isLoading(): Boolean

}