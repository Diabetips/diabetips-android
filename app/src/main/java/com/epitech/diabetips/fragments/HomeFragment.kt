package com.epitech.diabetips.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.NavigationActivity
import com.epitech.diabetips.activities.NewEntryActivity
import com.epitech.diabetips.activities.RecipeActivity
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.utils.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : ANavigationFragment(FragmentType.HOME) {

    lateinit var entriesManager: EntriesManager
    var loading: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entriesManager = EntriesManager(requireContext()) { items, reset ->
            itemsUpdateTrigger(reset, items)
        }

        val view = createFragmentView(R.layout.fragment_home, inflater, container)
        view.readyToScan.text = (activity as NavigationActivity).nfcReader?.nfcStatus

        view.newEntryButton.setOnClickListener {
            startActivity(Intent(requireContext(), NewEntryActivity::class.java))
        }
        view.viewRecipeButton.setOnClickListener {
            startActivity(Intent(requireContext(), RecipeActivity::class.java)
                .putExtra(getString(R.string.param_mode), ActivityMode.UPDATE))
        }
        view.openDashboardButton.setOnClickListener {
            startActivity(Intent(requireContext(), DashboardFragment::class.java))
        }
        ChartHandler.handleLineChartStyle(view.sugarLineChart, requireContext())
        //Draw empty chart
        val interval: Pair<Long, Long> = entriesManager.getPage()!!.getTimestampInterval(requireContext())
        ChartHandler.updateChartData(listOf(), interval, view.sugarLineChart, requireContext())
        //Call Api to update chart
        updateChart()

        (activity as NavigationActivity).nfcReader
        return view
    }

    private fun updateChart() {
        loading = true
        val now = TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api))
        entriesManager.getPage()?.setInterval(TimeHandler.instance.addMinuteToFormat(now, getString(R.string.format_time_api), -TimeHandler.instance.dayInMinute), now)
        entriesManager.updatePages()
        entriesManager.getItems()
    }

    private fun itemsUpdateTrigger(reset: Boolean, items: Array<EntryObject>) {
        val interval: Pair<Long, Long> = entriesManager.getPage()!!.getTimestampInterval(requireContext())
        view?.sugarLineChart?.let {
            ChartHandler.updateChartData(items.sortedBy{ it.time }, interval, it, requireContext())
            loading = false
        }
    }

    override fun isLoading(): Boolean {
        return loading
    }
}
