package com.epitech.diabetips.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.HbA1cAdapter
import com.epitech.diabetips.services.HbA1cService
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.storages.HbA1cObject
import com.epitech.diabetips.storages.PaginationObject
import com.epitech.diabetips.textWatchers.NumberWatcher
import com.epitech.diabetips.utils.*
import kotlinx.android.synthetic.main.activity_hba1c.*
import kotlinx.android.synthetic.main.dialog_hba1c.view.*

class HbA1cActivity : ADiabetipsActivity(R.layout.activity_hba1c) {

    private lateinit var page: PaginationObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = PaginationObject(resources.getInteger(R.integer.pagination_size), resources.getInteger(R.integer.pagination_default))
        closeHba1cButton.setOnClickListener { finish() }
        hba1cSwipeRefresh.setOnRefreshListener {
            getHbA1c()
        }
        newHba1cButton.setOnClickListener {
            openHbA1cPopup()
        }
        initRecyclerView()
        getHbA1c()
    }

    private fun initRecyclerView() {
        hba1cRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = HbA1cAdapter { hba1c : HbA1cObject ->
                openHbA1cPopup(hba1c)
            }
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@HbA1cActivity, R.drawable.list_divider)!!))
        }
        hba1cRecyclerView.addOnScrollListener(object : PaginationScrollListener(hba1cRecyclerView.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return page.isLast()
            }
            override fun isLoading(): Boolean {
                return hba1cSwipeRefresh.isRefreshing
            }
            override fun loadMoreItems() {
                getHbA1c(false)
            }
        })
    }

    private fun getHbA1c(resetPage: Boolean = true) {
        hba1cSwipeRefresh.isRefreshing = true
        if (resetPage)
            page.reset()
        else
            page.nextPage()
        HbA1cService.instance.getAll<HbA1cObject>(page).doOnSuccess {
            if (it.second.component2() == null) {
                page.updateFromHeader(it.first.headers[getString(R.string.pagination_header)]?.get(0))
                if (resetPage)
                    (hba1cRecyclerView.adapter as HbA1cAdapter).setHbA1c(it.second.component1()!!)
                else
                    (hba1cRecyclerView.adapter as HbA1cAdapter).addHbA1c(it.second.component1()!!)
            }
            hba1cSwipeRefresh.isRefreshing = false
        }.subscribe()
    }

    private fun openHbA1cPopup(hba1c: HbA1cObject = HbA1cObject()) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_hba1c, null)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.hba1cInput.addTextChangedListener(NumberWatcher(this, view.hba1cInputLayout, R.string.percentage_error, 0f, 100f))
        if (hba1c.id > 0) {
            view.hba1cTitle.setText(R.string.change_hba1c)
            view.hba1cInput.setText(hba1c.value.toString())
        } else {
            view.hba1cTitle.setText(R.string.add_hba1c)
            view.hba1cDeleteButton.visibility = View.INVISIBLE
            hba1c.time = TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api))
        }
        TimeHandler.instance.updateTimeDisplay(this, hba1c.time, view.hba1cTimeDate, view.hba1cTimeHour)
        view.hba1cTimeDate.setOnClickListener {
            TimeHandler.instance.getDatePickerDialog(this, DatePickerHandler { year, monthOfYear, dayOfMonth ->
                hba1c.time = TimeHandler.instance.changeFormatDate(hba1c.time, getString(R.string.format_time_api), year, monthOfYear, dayOfMonth)
                TimeHandler.instance.updateTimeDisplay(this, hba1c.time, view.hba1cTimeDate, view.hba1cTimeHour)
            }, hba1c.time).show(supportFragmentManager, "DatePickerDialog")
        }
        view.hba1cTimeHour.setOnClickListener {
            TimeHandler.instance.getTimePickerDialog(this, TimePickerHandler { hourOfDay, minute, _ ->
                hba1c.time = TimeHandler.instance.changeFormatTime(hba1c.time, getString(R.string.format_time_api), hourOfDay, minute)
                TimeHandler.instance.updateTimeDisplay(this, hba1c.time, view.hba1cTimeDate, view.hba1cTimeHour)
            }, hba1c.time).show(supportFragmentManager, "TimePickerDialog")
        }
        view.hba1cNegativeButton.setOnClickListener {
            dialog.dismiss()
        }
        view.hba1cPositiveButton.setOnClickListener {
            view.hba1cInput.text = view.hba1cInput.text
            if (view.hba1cInputLayout.error == null) {
                hba1c.value = view.hba1cInput.text.toString().toFloatOrNull() ?: 0f
                HbA1cService.instance.createOrUpdate(hba1c, hba1c.id).doOnSuccess {
                    if (it.second.component2() == null) {
                        getHbA1c()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.subscribe()
            }
        }
        view.hba1cDeleteButton.setOnClickListener {
            HbA1cService.instance.remove<HbA1cObject>(hba1c.id).doOnSuccess {
                if (it.second.component2() == null) {
                    Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                    getHbA1c()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
            }.subscribe()
        }
        dialog.show()
    }
}