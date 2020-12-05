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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.R
import com.epitech.diabetips.adapters.DropdownAdapter
import com.epitech.diabetips.adapters.NutritionalAdapter
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.services.ActivityService
import com.epitech.diabetips.services.ConnectionService
import com.epitech.diabetips.services.FoodService
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.ActivityObject
import com.epitech.diabetips.storages.IngredientObject
import com.epitech.diabetips.storages.NotificationInviteObject
import com.epitech.diabetips.storages.UserObject
import com.epitech.diabetips.textWatchers.CustomWatcher
import com.epitech.diabetips.textWatchers.InputWatcher
import com.epitech.diabetips.textWatchers.NumberWatcher
import com.epitech.diabetips.textWatchers.TextChangedWatcher
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.dialog_activity.view.*
import kotlinx.android.synthetic.main.dialog_barcode_not_found.view.*
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

        fun dialogSaveChange(context: Context, layoutInflater: LayoutInflater, positiveCallback: (() -> Unit), negativeCallback: (() -> Unit)) {
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

        fun dialogActivity(context: Context, layoutInflater: LayoutInflater, fragmentManager: FragmentManager, activity: ActivityObject = ActivityObject(), callback: ((ActivityObject?) -> Unit) = {}) {
            createDialog(context, layoutInflater, R.layout.dialog_activity) { view, dialog ->
                view.dialogActivityTypeInput.addTextChangedListener(InputWatcher(context, view.dialogActivityTypeInputLayout, true))
                view.dialogActivityTypeInput.setAdapter(DropdownAdapter(context, R.array.sports, true))
                if (activity.id > 0) {
                    view.dialogActivityTitle.text = context.getString(R.string.change_activity)
                    view.dialogActivityTypeInput.setText(activity.type)
                    TimeHandler.instance.updateTimeDisplay(context, activity.start, view.dialogActivityTimeDate, view.dialogActivityTimeHour)
                } else {
                    view.dialogActivityTitle.setText(R.string.add_activity)
                    view.dialogActivityTimeLayout.visibility = View.GONE
                    view.dialogActivityDeleteButton.visibility = View.INVISIBLE
                    activity.intensity = view.dialogActivityIntensityInput.max / 2
                }
                view.dialogActivityIntensityInput.progress = activity.intensity.coerceAtMost(view.dialogActivityIntensityInput.max)
                view.dialogActivityIntensityText.text = activity.getIntensity(context)
                view.dialogActivityDurationInput.setText(activity.getDuration(context))
                view.dialogActivityDurationInput.addTextChangedListener(CustomWatcher(view.dialogActivityDurationInputLayout) {
                    if (activity.getDurationSecond(context) > 0) null else context.getString(R.string.duration_error)
                })
                view.dialogActivityIntensityInput.setOnSeekBarChangeListener(SeekBarListener { intensity ->
                    activity.intensity = intensity
                    view.dialogActivityIntensityText.text = activity.getIntensity(context)
                })
                view.dialogActivityTimeDate.setOnClickListener {
                    datePickerDialog(context, fragmentManager, activity.start, view.dialogActivityTimeDate) { time ->
                        activity.setStart(context, time)
                    }
                }
                view.dialogActivityTimeHour.setOnClickListener {
                    timePickerDialog(context, fragmentManager, activity.start, view.dialogActivityTimeHour) { time ->
                        activity.setStart(context, time)
                    }
                }
                view.dialogActivityDurationInput.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        view.dialogActivityDurationInput.clearFocus()
                        timePickerDialog(
                            context, fragmentManager,
                            TimeHandler.instance.changeTimeFormat(view.dialogActivityDurationInput.text.toString(), context.getString(R.string.format_hour_24), context.getString(R.string.format_time_api))
                                ?: TimeHandler.instance.currentTimeFormat(context.getString(R.string.format_time_api))) { time ->
                            activity.setDuration(context, time)
                            view.dialogActivityDurationInput.setText(activity.getDuration(context))
                        }
                    }
                }
                view.dialogActivityNegativeButton.setOnClickListener {
                    dialog.dismiss()
                }
                view.dialogActivityPositiveButton.setOnClickListener {
                    view.dialogActivityTypeInput.text = view.dialogActivityTypeInput.text
                    view.dialogActivityDurationInput.text = view.dialogActivityDurationInput.text
                    if (view.dialogActivityTypeInputLayout.error == null && view.dialogActivityDurationInputLayout.error == null) {
                        activity.type = view.dialogActivityTypeInput.text.toString()
                        activity.intensity = view.dialogActivityIntensityInput.progress
                        ActivityService.instance.createOrUpdate(activity, activity.id).doOnSuccess {
                            if (it.second.component2() == null) {
                                callback.invoke(it.second.component1())
                                dialog.dismiss()
                            } else {
                                Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                            }
                        }.subscribe()
                    }
                }
                view.dialogActivityDeleteButton.setOnClickListener {
                    dialogConfirm(context, layoutInflater, R.string.activity_delete) {
                        ActivityService.instance.remove<ActivityObject>(activity.id).doOnSuccess {
                            if (it.second.component2() == null) {
                                callback.invoke(null)
                                Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            } else {
                                Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                            }
                        }.subscribe()
                    }
                }
            }
        }

        fun dialogSelectQuantity(context: Context, layoutInflater: LayoutInflater, ingredientObject: IngredientObject, callback: ((IngredientObject) -> Unit)) {
            createDialog(context, layoutInflater, R.layout.dialog_select_quantity) { view, dialog ->
                view.selectQuantityTitle.text = ingredientObject.food.name
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

        private fun dialogBarCodeNotFound(activity: Activity, textId: Int = R.string.scan_not_found) {
            createDialog(activity, activity.layoutInflater, R.layout.dialog_barcode_not_found) { view, dialog ->
                view.dialogBarcodeText.setText(textId)
                view.dialogBarcodeScanButton.setOnClickListener {
                    dialog.dismiss()
                    ImageHandler.instance.startBarcodeActivity(activity)
                }
                view.dialogBarcodeCloseButton.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }

        fun dialogBarcode(activity: Activity, data: Intent?, callback: ((IngredientObject) -> Unit)) {
            val result = IntentIntegrator.parseActivityResult(Activity.RESULT_OK, data)
            if (result?.contents != null) {
                FoodService.instance.getBarcode(result.contents).doOnSuccess {
                    if ((it.second.component1()?.firstOrNull()?.id ?: 0) > 0) {
                        dialogSelectQuantity(activity, activity.layoutInflater, it.second.component1()!!.first().getIngredient(), callback)
                    } else {
                        dialogBarCodeNotFound(activity)
                    }
                }.subscribe()
            } else {
                dialogBarCodeNotFound(activity, R.string.scan_error)
            }
        }

        fun dialogInvite(context: Context, layoutInflater: LayoutInflater, notification: NotificationInviteObject, callback: (() -> Unit), onDismiss: (() -> Unit) = {}) {
            createDialog(context, layoutInflater, R.layout.dialog_confirm) { view, dialog ->
                dialog.setOnDismissListener { onDismiss.invoke() }
                var user = UserObject(uid = notification.from)
                view.dialogConfirmText.text = context.getString(R.string.invitation_text)
                UserService.instance.get<UserObject>(user.uid).doOnSuccess {
                    if (it.second.component2() == null) {
                        user = it.second.component1()!!
                        view.dialogConfirmText.text = context.getString(R.string.invitation_text_name, "${user.first_name} ${user.last_name}")
                    }
                }.subscribe()
                view.dialogConfirmNegativeButton.setOnClickListener {
                    notification.markAsRead()
                    dialog.dismiss()
                }
                view.dialogConfirmPositiveButton.setOnClickListener {
                    dialog.dismiss()
                    ConnectionService.instance.update(user, user.uid).doOnSuccess {
                        if (it.second.component2() == null) {
                            notification.markAsRead()
                            Toast.makeText(context, context.getString(R.string.invitation_accepted, "${user.first_name} ${user.last_name}"), Toast.LENGTH_SHORT).show()
                            UserManager.instance.saveChatUser(context, user)
                            callback.invoke()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                        }
                    }.subscribe()
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