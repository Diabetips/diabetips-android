package com.epitech.diabetips.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.NewMealActivity
import com.epitech.diabetips.holders.DashboardItemViewHolder
import com.epitech.diabetips.services.InsulinService
import com.epitech.diabetips.services.NoteService
import com.epitech.diabetips.storages.EntryObject
import com.epitech.diabetips.storages.InsulinObject
import com.epitech.diabetips.storages.MealObject
import com.epitech.diabetips.storages.NoteObject
import com.epitech.diabetips.utils.*
import kotlinx.android.synthetic.main.dialog_change_comment.view.*
import kotlinx.android.synthetic.main.dialog_change_insulin.view.*

class DashboardItemsAdapter(
        val context: Context,
        private val fragmentManager: FragmentManager,
        private val items: ArrayList<EntryObject> = arrayListOf(),
        private val onItemClickListener: ((EntryObject) -> Unit)? = null)
    : RecyclerView.Adapter<DashboardItemViewHolder>() {

    var mitems = items

    fun setItems(itemList: Array<EntryObject>) {
        items.clear()
        items.addAll(itemList)
        notifyDataSetChanged()
    }

    fun setItem(item: EntryObject, position: Int) {
        items[position] = item
        notifyItemChanged(position)
    }

    fun addItem(item: EntryObject) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun addItems(itemList: Array<EntryObject>) {
        items.addAll(itemList)
        notifyItemRangeInserted(items.size - itemList.size, itemList.size)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyDataSetChanged()
    }

    fun updateItem(item: EntryObject) {
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
        holder.bind(context, items[position], onItemClickListener)

        holder.itemView.setOnClickListener {
            when (items[position].type) {
                    EntryObject.Type.MEAL -> launchMeal(items[position])
                    EntryObject.Type.COMMENT -> changeComment(items[position], position)
                    EntryObject.Type.INSULIN -> changeInsulin(items[position], position)
                    EntryObject.Type.INSULIN_SLOW -> changeInsulin(items[position], position)
                    EntryObject.Type.INSULIN_FAST -> changeInsulin(items[position], position)
                    EntryObject.Type.SUGAR -> TODO()
            }
        }
    }

    private fun launchMeal(item: EntryObject) {
        val intent = Intent(context, NewMealActivity::class.java)
            .putExtra(
                    context.getString(R.string.param_meal),
                    item.orignal as MealObject
            )
        context.startActivity(intent)
    }

    private fun changeInsulin(item: EntryObject, position: Int) {
        DialogHandler.createDialog(context, LayoutInflater.from(context), R.layout.dialog_change_insulin) { view, dialog ->
            val insulinObject = (item.orignal as InsulinObject).copy()
            view.changeInsulinInputLayout.hint = "${view.changeInsulinInputLayout.hint} (${context.getString(R.string.unit_units)})"
            view.changeInsulinInput.setText(insulinObject.quantity.toString())
            TimeHandler.instance.updateTimeDisplay(context, insulinObject.time, view.changeInsulinTimeDate, view.changeInsulinTimeHour)
            view.changeInsulinTimeDate.setOnClickListener {
                DialogHandler.datePickerDialog(context, fragmentManager, insulinObject.time , view.changeInsulinTimeDate) { time ->
                    insulinObject.time = time
                }
            }
            view.changeInsulinTimeHour.setOnClickListener {
                DialogHandler.timePickerDialog(context, fragmentManager, insulinObject.time, view.changeInsulinTimeHour) { time ->
                    insulinObject.time = time
                }
            }
            view.changeInsulinNegativeButton.setOnClickListener {
                dialog.dismiss()
            }
            view.changeInsulinPositiveButton.setOnClickListener {
                val quantity: Float? = view.changeInsulinInput.text.toString().toFloatOrNull()
                if (quantity == null || quantity <= 0) {
                    view.changeInsulinInputLayout.error = context.getString(R.string.quantity_null)
                } else {
                    insulinObject.quantity = quantity.toInt()
                    InsulinService.instance.createOrUpdate(insulinObject, insulinObject.id).doOnSuccess {
                        if (it.second.component2() == null) {
                            setItem(EntryObject(insulinObject, context), position)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }.subscribe()
                }
            }
            view.changeInsulinDeleteButton.setOnClickListener {
                DialogHandler.dialogConfirm(context, LayoutInflater.from(context), R.string.insulin_delete) {
                    InsulinService.instance.remove<InsulinObject>(insulinObject.id).doOnSuccess {
                        if (it.second.component2() == null) {
                            Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                            removeItem(position)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }.subscribe()
                }
            }
        }
    }

    private fun changeComment(item: EntryObject, position: Int) {
        DialogHandler.createDialog(context, LayoutInflater.from(context), R.layout.dialog_change_comment) { view, dialog ->
            val noteObject = (item.orignal as NoteObject).copy()
            view.changeCommentInput.setText(noteObject.description)
            TimeHandler.instance.updateTimeDisplay(context, noteObject.time, view.changeCommentTimeDate, view.changeCommentTimeHour)
            view.changeCommentTimeDate.setOnClickListener {
                DialogHandler.datePickerDialog(context, fragmentManager, noteObject.time , view.changeCommentTimeDate) { time ->
                    noteObject.time = time
                }
            }
            view.changeCommentTimeHour.setOnClickListener {
                DialogHandler.timePickerDialog(context, fragmentManager, noteObject.time, view.changeCommentTimeHour) { time ->
                    noteObject.time = time
                }
            }
            view.changeCommentNegativeButton.setOnClickListener {
                dialog.dismiss()
            }
            view.changeCommentPositiveButton.setOnClickListener {
                val text: String = view.changeCommentInput.text.toString()
                if (text.isBlank()) {
                    view.changeCommentInputLayout.error = context.getString(R.string.empty_field_error)
                } else {
                    noteObject.description = text
                    NoteService.instance.createOrUpdate(noteObject, noteObject.id).doOnSuccess {
                        if (it.second.component2() == null) {
                            setItem(EntryObject(noteObject, context), position)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }.subscribe()
                }
            }
            view.changeCommentDeleteButton.setOnClickListener {
                DialogHandler.dialogConfirm(context, LayoutInflater.from(context), R.string.comment_delete) {
                    NoteService.instance.remove<NoteObject>(noteObject.id).doOnSuccess {
                        if (it.second.component2() == null) {
                            Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                            removeItem(position)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }.subscribe()
                }
            }
        }
    }

}