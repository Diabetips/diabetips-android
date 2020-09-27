package com.epitech.diabetips.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.NutritionalAdapter
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.textWatchers.NumberWatcher
import com.epitech.diabetips.textWatchers.TextChangedWatcher
import kotlinx.android.synthetic.main.dialog_change_picture.view.*
import kotlinx.android.synthetic.main.dialog_confirm.view.*
import kotlinx.android.synthetic.main.dialog_save_change.view.*
import kotlinx.android.synthetic.main.dialog_select_quantity.view.*

class DialogHandler {

    companion object {

        fun createDialog(context: Context, layoutInflater: LayoutInflater, layoutId: Int, viewCallback: ((View, AlertDialog) -> Unit)) {
            val view = layoutInflater.inflate(layoutId, null)
            MaterialHandler.handleTextInputLayoutSize(view as ViewGroup)
            val dialog = AlertDialog.Builder(context).setView(view).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            viewCallback.invoke(view, dialog)
            dialog.show()
        }

        fun dialogConfirm(context: Context, layoutInflater: LayoutInflater, messageId: Int, callback: (() -> Unit)) {
            createDialog(context, layoutInflater, R.layout.dialog_confirm) { view, dialog ->
                view.dialogConfirmText.setText(messageId)
                view.dialogConfirmNegativeButton.setOnClickListener {
                    dialog.dismiss()
                }
                view.dialogConfirmPositiveButton.setOnClickListener {
                    dialog.dismiss()
                    callback.invoke()
                }
            }
        }

        fun dialogSaveChange(context: Context, layoutInflater: LayoutInflater, positiveCallback: (() -> Unit), negativeCallback: (() -> Unit) ) {
            createDialog(context, layoutInflater, R.layout.dialog_save_change) { view, dialog ->
                view.saveChangeNegativeButton.setOnClickListener {
                    negativeCallback.invoke()
                    dialog.dismiss()
                }
                view.saveChangePositiveButton.setOnClickListener {
                    positiveCallback.invoke()
                    dialog.dismiss()
                }
            }
        }

        fun dialogChangePicture(context: Context, layoutInflater: LayoutInflater, activity: Activity, deleteCallback: (() -> Unit)) {
            createDialog(context, layoutInflater, R.layout.dialog_change_picture) { view, dialog ->
                view.newPictureButton.setOnClickListener {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    ActivityCompat.startActivityForResult(activity, intent, RequestCode.GET_PHOTO.ordinal, null)
                    dialog.dismiss()
                }
                view.pictureGalleryButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    ActivityCompat.startActivityForResult(activity, intent, RequestCode.GET_IMAGE.ordinal, null)
                    dialog.dismiss()
                }
                view.deletePictureButton.setOnClickListener {
                    deleteCallback.invoke()
                    dialog.dismiss()
                }
            }
        }

        fun dialogSelectQuantity(context: Context, layoutInflater: LayoutInflater, ingredientObject: IngredientObject, callback: ((IngredientObject) -> Unit)) {
            createDialog(context, layoutInflater, R.layout.dialog_select_quantity) { view, dialog ->
                view.selectQuantityInputLayout.hint = "${view.selectQuantityInputLayout.hint} (${ingredientObject.food.unit})"
                if (ingredientObject.quantity > 0) {
                    view.selectQuantityInput.setText(ingredientObject.quantity.toBigDecimal().stripTrailingZeros().toPlainString())
                }
                view.selectQuantityNutritionalList.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = NutritionalAdapter(ingredientObject.getNutritionalValues(), ingredientObject.quantity)
                }
                view.selectQuantityInput.addTextChangedListener(NumberWatcher(context, view.selectQuantityInputLayout, R.string.quantity_null, 0f))
                view.selectQuantityInput.addTextChangedListener(TextChangedWatcher {
                    val quantity = it.toString().toFloatOrNull() ?: 0f
                    (view.selectQuantityNutritionalList.adapter as NutritionalAdapter).setNutritions(ingredientObject.getNutritionalValues(quantity), quantity)
                })
                view.selectQuantityNegativeButton.setOnClickListener {
                    dialog.dismiss()
                }
                view.selectQuantityPositiveButton.setOnClickListener {
                    view.selectQuantityInput.text = view.selectQuantityInput.text
                    if (view.selectQuantityInputLayout.error == null) {
                        ingredientObject.quantity = view.selectQuantityInput.text.toString().toFloatOrNull() ?: 0f
                        callback.invoke(ingredientObject)
                        dialog.dismiss()
                    }
                }
            }
        }

        fun datePickerDialog(context: Context, fragmentManager: FragmentManager, time: String, dateDisplay: TextView? = null, callback: (String) -> Unit) {
            TimeHandler.instance.getDatePickerDialog(context,
                DatePickerHandler { year, monthOfYear, dayOfMonth ->
                    val newTime = TimeHandler.instance.changeFormatDate(time, context.getString(R.string.format_time_api), year, monthOfYear, dayOfMonth)
                    callback.invoke(newTime)
                    TimeHandler.instance.updateTimeDisplay(context, newTime, dateDisplay, null)
                }, time).show(fragmentManager, "DatePickerDialog")
        }

        fun timePickerDialog(context: Context, fragmentManager: FragmentManager, time: String, timeDisplay: TextView? = null, callback: (String) -> Unit) {
            TimeHandler.instance.getTimePickerDialog(context,
                TimePickerHandler { hourOfDay, minute, _ ->
                    val newTime = TimeHandler.instance.changeFormatTime(time, context.getString(R.string.format_time_api), hourOfDay, minute)
                    callback.invoke(newTime)
                    TimeHandler.instance.updateTimeDisplay(context, newTime, null, timeDisplay)
                }, time).show(fragmentManager, "TimePickerDialog")
        }

    }
}