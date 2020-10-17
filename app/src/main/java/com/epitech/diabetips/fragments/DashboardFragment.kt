package com.epitech.diabetips.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.AverageGlucoseActivity
import com.epitech.diabetips.activities.EventNotebookActivity
import com.epitech.diabetips.activities.HbA1cActivity
import com.epitech.diabetips.utils.ANavigationFragment
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DashboardFragment : ANavigationFragment(FragmentType.DASHBOARD) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = createFragmentView(R.layout.fragment_dashboard, inflater, container)
        view.dashboardEventNotebookCard.setOnClickListener {
            startActivity(Intent(requireContext(), EventNotebookActivity::class.java))
        }
        view.dashboardGlucoseCurveCard.setOnClickListener {
            startActivity(Intent(requireContext(), AverageGlucoseActivity::class.java))
        }
        view.dashboardGlucoseChartCard.setOnClickListener {
            startActivity(Intent(requireContext(), AverageGlucoseActivity::class.java))
        }
        view.dashboardHemoglobinCard.setOnClickListener {
            startActivity(Intent(requireContext(), HbA1cActivity::class.java))
        }

        return view
    }

    override fun isLoading(): Boolean {
        return false
    }
}