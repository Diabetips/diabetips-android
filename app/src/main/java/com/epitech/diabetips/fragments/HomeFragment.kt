package com.epitech.diabetips.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.*
import com.epitech.diabetips.managers.EntriesManager
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.services.BloodSugarService
import com.epitech.diabetips.services.PredictionService
import com.epitech.diabetips.storages.BiometricObject
import com.epitech.diabetips.storages.BloodSugarObject
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.storages.PredictionObject
import com.epitech.diabetips.utils.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : ANavigationFragment(FragmentType.HOME) {

    private lateinit var entriesManager: EntriesManager
    private var loading: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        entriesManager = EntriesManager(requireContext()) { items, _ ->
            itemsUpdateTrigger(items)
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
            startActivity(Intent(requireContext(), EventNotebookActivity::class.java))
        }
        view.viewChatButton.setOnClickListener {
            NavigationActivity.setUnreadMessage(false)
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }
        updateChatIcon(view)
        handlePrediction(view)
        getLastBloodSugar()
        ChartHandler.handleLineChartStyle(view.sugarLineChart, requireContext())
        //Call Api to update chart
        updateChart()
        return view
    }

    private fun updateChart() {
        loading = true
        entriesManager.getPage()?.setInterval(requireContext(), TimeHandler.instance.getIntervalFormat(requireContext(), getString(R.string.time_range_day), getString(R.string.format_time_api)))
        entriesManager.updatePages()
        entriesManager.getItems()
    }

    private fun itemsUpdateTrigger(items: Array<EntryObject>) {
        val interval: Pair<Long, Long> = entriesManager.getPage()!!.getTimestampInterval(requireContext())
        view?.sugarLineChart?.let {
            ChartHandler.updateChartData(items.sortedBy { it.time }, interval, it, requireContext(), R.string.no_data_24)
            loading = false
        }
    }

    private fun handlePrediction(view: View? = this.view) {
        PredictionService.instance.getUserPredictionSettings().doOnSuccess {
            if (it.second.component2() == null && it.second.component1()?.enabled == true) {
                view?.homePredictionLayout?.visibility = View.VISIBLE
            }
        }.subscribe()
        view?.homePredictionButton?.setOnClickListener {
            PredictionService.instance.getUserPrediction().doOnSuccess {
                if (it.second.component2() == null) {
                    updatePrediction(it.second.component1())
                } else {
                    Toast.makeText(requireContext(), it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        updatePrediction(PredictionService.lastPrediction, view)
    }

    private fun updatePrediction(prediction: PredictionObject?, view: View? = this.view) {
        PredictionService.lastPrediction = prediction
        if ((prediction?.id ?: 0) > 0) {
            view?.homePredictionResult?.text = "${prediction!!.insulin.toBigDecimal().stripTrailingZeros().toPlainString()} ${requireContext().getString(R.string.unit_units)}"
            TimeHandler.instance.updateTimeDisplay(requireContext(), prediction.time, null, view?.homePredictionResultTime)
            view?.homePredictionResultTime?.text = "${view?.homePredictionResultTime?.text} ${requireContext().getString(R.string.last_prediction)}"
            view?.homePredictionResultLayout?.visibility = View.VISIBLE
        } else {
            view?.homePredictionResultLayout?.visibility = View.GONE
        }
    }

    private fun getLastBloodSugar() {
        BloodSugarService.instance.getLastMeasure().doOnSuccess {
            updateLastBloodSugar(it.second.component1())
        }.subscribe()
    }

    private fun updateLastBloodSugar(bloodSugar: BloodSugarObject?, view: View? = this.view) {
        if ((bloodSugar?.value ?: 0) > 0) {
            view?.lastBloodGlucose?.text = "${bloodSugar!!.value.toBigDecimal().stripTrailingZeros().toPlainString()} ${requireContext().getString(R.string.unit_glucose)}"
            TimeHandler.instance.updateTimeDisplay(requireContext(), bloodSugar.time, null, view?.lastBloodGlucoseTime)
            view?.lastBloodGlucoseTime?.text = "${view?.lastBloodGlucoseTime?.text} ${requireContext().getString(R.string.last_use)}"
            val biometric: BiometricObject = UserManager.instance.getBiometric(requireContext())
            if ((biometric.hyperglycemia ?: bloodSugar.value) < bloodSugar.value || (biometric.hypoglycemia ?: bloodSugar.value) > bloodSugar.value) {
                view?.lastBloodGlucoseLayout?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            } else {
                view?.lastBloodGlucoseLayout?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }
            view?.lastBloodGlucoseLayout?.visibility = View.VISIBLE
        } else {
            view?.lastBloodGlucoseLayout?.visibility = View.GONE
        }
    }

    fun updateChatIcon(view: View? = this.view) {
        if (UserManager.instance.getChatUser(requireContext()).uid.isNotEmpty()) {
            view?.viewChatButton?.visibility = View.VISIBLE
            if (NavigationActivity.getUnreadMessage()) {
                view?.viewChatButton?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            } else {
                view?.viewChatButton?.setColorFilter(MaterialHandler.getColorFromAttribute(requireContext(), R.attr.colorBackgroundText))
            }
        } else {
            view?.viewChatButton?.visibility = View.GONE
        }
    }

    fun onNfc() {
        getLastBloodSugar()
        entriesManager.getItems()
    }

    override fun isLoading(): Boolean {
        return loading
    }
}
