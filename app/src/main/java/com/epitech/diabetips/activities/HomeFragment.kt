package com.epitech.diabetips.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.utils.ChartHandler
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import com.epitech.diabetips.utils.TimeHandler
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : NavigationFragment(FragmentType.HOME) {

    lateinit var entriesManager: EntriesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entriesManager = EntriesManager(context = requireContext()) {
                items, reset -> itemsUpdateTrigger(reset, items)
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)

        view.newEntryButton.setOnClickListener {
            startActivity(Intent(context, NewEntryActivity::class.java))
        }
        view.viewRecipeButton.setOnClickListener {
            startActivity(Intent(context, RecipeActivity::class.java)
                .putExtra(getString(R.string.param_mode), RecipeActivity.ActivityMode.UPDATE))
        }
        view.openDashboardButton.setOnClickListener {
            startActivity(Intent(context, DashboardActivity::class.java))
        }


        ChartHandler.instance.handleLineChartStyle(view.sugarLineChart, requireContext())
        //Draw empty chart
        val interval: Pair<Long, Long> = Pair(entriesManager.getPage()!!.start, entriesManager.getPage()!!.end)
        ChartHandler.instance.updateChartData(listOf(), interval, view.sugarLineChart, requireContext())
        //Call Api to update chart
        updateChart()

        (activity as NavigationActivity).nfcReader
        return view
    }

    private fun updateChart() {
        val cur = TimeHandler.instance.currentTimeSecond();
        entriesManager.getPage()?.setInterval(cur - TimeHandler.instance.dayInSecond, cur)
        entriesManager.updatePages()
        entriesManager.getItems()
    }

    private fun itemsUpdateTrigger(reset: Boolean, items: Array<EntryObject>) {
        val interval: Pair<Long, Long> = Pair(entriesManager.getPage()!!.start, entriesManager.getPage()!!.end)
        view?.sugarLineChart?.let { ChartHandler.instance.updateChartData(items.sortedBy{ it.time }, interval, it,  requireContext()) }
    }

    override fun isLoading(): Boolean {
        return false
    }
}
