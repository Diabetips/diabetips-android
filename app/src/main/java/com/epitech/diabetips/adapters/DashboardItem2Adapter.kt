package com.epitech.diabetips.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.NewMealActivity
import com.epitech.diabetips.holders.DashboardItemViewHolder
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.storages.DashboardItemObject
import com.epitech.diabetips.storages.InsulinObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.dialog_select_quantity.view.*

class DashboardItem2Adapter(val context: Context,
                            private val items: ArrayList<DashboardItemObject> = arrayListOf(),
                            private val onItemClickListener: ((DashboardItemObject) -> Unit)? = null)
        : RecyclerView.Adapter<DashboardItemViewHolder>() {

    var mitems = items;

    fun setItems(itemList: Array<DashboardItemObject>) {
        items.clear()
        items.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setItem(item: DashboardItemObject, position: Int) {
        items[position] = item
        notifyItemChanged(position)
    }

    fun addItem(item: DashboardItemObject) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun addItems(itemList: Array<DashboardItemObject>) {
        items.addAll(itemList)
        notifyItemRangeInserted(items.size - itemList.size, itemList.size)
    }

    fun updateItem(item: DashboardItemObject) {
        items.forEachIndexed { index, DashboardItemObject ->
            if (DashboardItemObject.id == item.id) {
                items[index] = item
                notifyItemChanged(index)
                return
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return DashboardItemViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DashboardItemViewHolder, position: Int) {
        holder.bind(items[position], onItemClickListener)

        holder.itemView.setOnClickListener {
            when (items[position].type) {
                DashboardItemObject.Type.MEAL -> launchMeal(items[position])
                DashboardItemObject.Type.COMMENT -> TODO()
                DashboardItemObject.Type.INSULIN_SLOW -> changeInsulin(items[position], position)
                DashboardItemObject.Type.INSULIN_FAST -> changeInsulin(items[position], position)
                DashboardItemObject.Type.SUGAR -> TODO()
            }
        }
    }

    private fun launchMeal(item: DashboardItemObject) {
        val intent = Intent(context, NewMealActivity::class.java)
            .putExtra(
                context.getString(R.string.param_meal),
                item.orignal as MealObject
            )
        context.startActivity(intent)
    }

    private fun changeInsulin(item: DashboardItemObject, position: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_select_quantity, null)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        val dialog = AlertDialog.Builder(context).setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val insulinObject = item.orignal as InsulinObject
        view.selectQuantityInputLayout.hint = view.selectQuantityInputLayout.hint.toString() + " (" + context.getString(R.string.unit_units) + ")"
        view.selectQuantityInput.setText(insulinObject.quantity.toString())
        view.selectQuantityNegativeButton.setOnClickListener {
            dialog.dismiss()
        }
        view.selectQuantityPositiveButton.setOnClickListener {
            val quantity: Float? = view.selectQuantityInput.text.toString().toFloatOrNull()
            if (quantity == null || quantity <= 0) {
                view.selectQuantityInputLayout.error = context.getString(R.string.quantity_null)
            } else {
                insulinObject.quantity = quantity.toInt()
                InsulinService.instance.createOrUpdateUserInsulin(insulinObject).doOnSuccess {
                    if (it.second.component2() == null) {
                        setItem(DashboardItemObject(insulinObject, context), position)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.subscribe()
            }
        }
        dialog.show()
    }

}