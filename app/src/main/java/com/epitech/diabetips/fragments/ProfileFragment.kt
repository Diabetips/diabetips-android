package com.epitech.diabetips.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.epitech.diabetips.managers.UserManager
import com.epitech.diabetips.R
import com.epitech.diabetips.activities.SettingsActivity
import com.epitech.diabetips.adapters.DropdownAdapter
import com.epitech.diabetips.adapters.MenuAdapter
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.services.BiometricService
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.UserObject
import com.epitech.diabetips.storages.BiometricObject
import com.epitech.diabetips.textWatchers.EmailWatcher
import com.epitech.diabetips.textWatchers.InputWatcher
import com.epitech.diabetips.textWatchers.PasswordConfirmWatcher
import com.epitech.diabetips.textWatchers.PasswordWatcher
import com.epitech.diabetips.utils.*
import com.google.android.material.appbar.AppBarLayout
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.dialog_change_picture.view.*
import kotlinx.android.synthetic.main.dialog_deactivate_account.view.*
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.dialog_menu.view.*
import java.io.InputStream
import kotlin.math.abs

class ProfileFragment : ANavigationFragment(FragmentType.PROFILE), DatePickerDialog.OnDateSetListener {

    enum class RequestCode { GET_IMAGE, GET_PHOTO }

    private var loading: Boolean = false

    private var lastVerticalOffset: Int = 0
    private var baseCircleToolbarSize: Float = 1f
    private var minImageMargin: Float = 0f
    private var maxImageMargin: Float = 1f
    private var minImageSize: Float = 0f
    private var maxImageSize: Float = 1f
    private var maxImageOffset: Float = 1f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = createFragmentView(R.layout.fragment_profile, inflater, container)
        view.nameProfileInput.addTextChangedListener(InputWatcher(context, view.nameProfileInputLayout, true, R.string.name_invalid_error))
        view.firstNameProfileInput.addTextChangedListener(InputWatcher(context, view.firstNameProfileInputLayout, true, R.string.first_name_invalid_error))
        view.emailProfileInput.addTextChangedListener(EmailWatcher(context, view.emailProfileInputLayout))
        view.newPasswordInput.addTextChangedListener(PasswordWatcher(context, view.newPasswordInputLayout, true, true))
        view.newPasswordConfirmInput.addTextChangedListener(PasswordConfirmWatcher(context, view.newPasswordConfirmInputLayout, view.newPasswordInput))
        view.birthDateProfileInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                view.birthDateProfileInput.clearFocus()
                TimeHandler.instance.getDatePickerDialog(requireContext(), this,
                    TimeHandler.instance.changeTimeFormat(view.birthDateProfileInput.text.toString(),
                        getString(R.string.format_date_birth),
                        getString(R.string.format_time_api))
                        ?: TimeHandler.instance.currentTimeFormat(getString(R.string.format_time_api)))
                    .show(requireActivity().supportFragmentManager, "DatePickerDialog")
            }
        }
        view.updateProfileButton.setOnClickListener {
            updateProfile()
        }
        view.settingsButton.setOnClickListener {
            openSettings()
        }
        view.logoutButton.setOnClickListener {
            logout()
        }
        view.deactivateAccountButton.setOnClickListener {
            deactivateAccount()
        }
        handleProfileImage(view)
        handlePopupMenu(view)
        handleAnimation(view)
        view.sexProfileDropdown.setAdapter(DropdownAdapter(requireContext(), R.array.sex))
        view.diabetesTypeProfileDropdown.setAdapter(DropdownAdapter(requireContext(), R.array.diabetes_type))
        getAccountInfo(view)
        return view
    }

    private fun handleProfileImage(view: View) {
        view.imagePhotoProfile.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_change_picture, null)
            MaterialHandler.instance.handleTextInputLayoutSize(dialogView as ViewGroup)
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogView.newPictureButton.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, RequestCode.GET_PHOTO.ordinal)
                dialog.dismiss()
            }
            dialogView.pictureGalleryButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, RequestCode.GET_IMAGE.ordinal)
                dialog.dismiss()
            }
            dialogView.deletePictureButton.setOnClickListener {
                loading = true
                UserService.instance.removePicture<UserObject>("me").doOnSuccess {
                    if (it.second.component2() == null) {
                        Toast.makeText(requireContext(), R.string.remove_profile_picture, Toast.LENGTH_SHORT).show()
                        setAccountInfo(UserManager.instance.getUser(requireContext()))
                    } else {
                        Toast.makeText(requireContext(), it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                    loading = false
                    dialog.dismiss()
                }.subscribe()
            }
            dialog.show()
        }
    }

    private fun handlePopupMenu(view: View) {
        view.menuButton.setOnClickListener {
            val dialogView: View = layoutInflater.inflate(R.layout.dialog_menu, null)
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.attributes?.apply {
                gravity = Gravity.TOP or Gravity.END
                y = requireContext().resources.getDimension(R.dimen.input_size).toInt() + requireContext().resources.getDimension(R.dimen.half_margin_size).toInt()
            }
            dialogView.menuList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = MenuAdapter(R.menu.profile, requireContext()) { item ->
                    dialog.dismiss()
                    when (item.itemId) {
                        R.id.profileSettings -> openSettings()
                        R.id.profileLogout -> logout()
                        R.id.profileDeactivate -> deactivateAccount()
                    }
                }
                addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.menu_divider)!!))
            }
            dialog.show()
        }
    }

    private fun handleAnimation(view: View) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        maxImageOffset = displayMetrics.widthPixels / 2 - (requireContext().resources.getDimension(R.dimen.input_size) + requireContext().resources.getDimension(R.dimen.margin_size))
        baseCircleToolbarSize = requireContext().resources.getDimension(R.dimen.circle_toolbar_size)
        minImageSize = requireContext().resources.getDimension(R.dimen.input_size)
        maxImageSize = requireContext().resources.getDimension(R.dimen.profile_image_size) - minImageSize
        minImageMargin = requireContext().resources.getDimension(R.dimen.quarter_margin_size)
        maxImageMargin = requireContext().resources.getDimension(R.dimen.profile_image_margin) - minImageMargin
        view.profileAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (lastVerticalOffset == verticalOffset)
                return@OnOffsetChangedListener
            lastVerticalOffset = verticalOffset
            view.profileAppBar?.y = -verticalOffset.toFloat()
            val ratio = 1 - abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
            view.circleToolbarView?.apply {
                layoutParams.also {
                    it.height = (baseCircleToolbarSize * ratio).toInt()
                }
            }
            view.imagePhotoProfile?.apply {
                (layoutParams as ViewGroup.MarginLayoutParams).also {
                    x = if (ratio < 0.25f) maxImageOffset * (1 - ratio * 4) else 0f
                    it.height = (minImageSize + maxImageSize * ratio).toInt()
                    it.width = (minImageSize + maxImageSize * ratio).toInt()
                    it.topMargin = (minImageMargin + maxImageMargin * ratio).toInt()
                    requestLayout()
                }
            }
        })
    }

    private fun getAccountInfo(view: View? = this.view) {
        val account = UserManager.instance.getUser(requireContext())
        if (account.uid != "") {
            setAccountInfo(account, view)
            setBiometricInfo(UserManager.instance.getBiometric(requireContext()), view)
        } else {
            loading = true
            UserService.instance.get<UserObject>("me").doOnSuccess {
                if (it.second.component2() == null) {
                    UserManager.instance.saveUser(requireContext(), it.second.component1()!!)
                    setAccountInfo(it.second.component1()!!, view)
                    getBiometricInfo(view)
                } else {
                    Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    loading = false
                }
            }.subscribe()
        }
    }

    private fun getBiometricInfo(view: View? = this.view) {
        loading = true
        BiometricService.instance.get<BiometricObject>().doOnSuccess {
            if (it.second.component2() == null) {
                UserManager.instance.saveBiometric(requireContext(), it.second.component1()!!)
                setBiometricInfo(it.second.component1()!!, view)
            } else {
                Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
            loading = false
        }.subscribe()
    }

    private fun setAccountInfo(account: UserObject, view: View? = this.view) {
        view?.firstNameProfileInput?.setText(account.first_name)
        view?.nameProfileInput?.setText(account.last_name)
        view?.emailProfileInput?.setText(account.email)
        ImageHandler.instance.loadImage(view?.imagePhotoProfile as ImageView, requireContext(), UserService.instance.getPictureUrl(account.uid), R.drawable.ic_person, false)
    }

    private fun setBiometricInfo(biometric: BiometricObject, view: View? = this.view) {
        view?.heightProfileInput?.setText(biometric.height?.toString())
        view?.weightProfileInput?.setText(biometric.mass?.toString())
        view?.hyperglycemiaProfileInput?.setText(biometric.hyperglycemia?.toString())
        view?.hypoglycemiaProfileInput?.setText(biometric.hypoglycemia?.toString())
        view?.birthDateProfileInput?.setText(TimeHandler.instance.changeTimeFormat(
            biometric.date_of_birth,
            context?.getString(R.string.format_date_api)!!,
            context?.getString(R.string.format_date_birth)!!))
        view?.diabetesTypeProfileDropdown?.setText(biometric.getDiabetesType(requireContext()))
        view?.sexProfileDropdown?.setText(biometric.getSex(requireContext()))
    }

    private fun updateProfile() {
        if (validateFields()) {
            val user: UserObject = UserManager.instance.getUser(requireContext())
            user.last_name = view?.nameProfileInput?.text.toString()
            user.first_name = view?.firstNameProfileInput?.text.toString()
            user.email = view?.emailProfileInput?.text.toString()
            if (view?.newPasswordInput?.text.toString().isNotEmpty()) {
                user.password = view?.newPasswordInput?.text.toString()
            }
            loading = true
            UserService.instance.update(user, "me").doOnSuccess {
                if (it.second.component2() == null) {
                    UserManager.instance.saveUser(requireContext(), it.second.component1()!!)
                    updateBiometric()
                } else {
                    Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    loading = false
                }
            }.subscribe()
        }
    }

    private fun updateBiometric() {
        val biometric: BiometricObject = UserManager.instance.getBiometric(requireContext())
        biometric.height = view?.heightProfileInput?.text.toString().toIntOrNull()
        biometric.mass = view?.weightProfileInput?.text.toString().toIntOrNull()
        biometric.hypoglycemia = view?.hypoglycemiaProfileInput?.text.toString().toIntOrNull()
        biometric.hyperglycemia = view?.hyperglycemiaProfileInput?.text.toString().toIntOrNull()
        biometric.date_of_birth = TimeHandler.instance.changeTimeFormat(
            view?.birthDateProfileInput?.text.toString(),
            context?.getString(R.string.format_date_birth)!!,
            context?.getString(R.string.format_date_api)!!)
        biometric.setSex(requireContext(), view?.sexProfileDropdown?.text.toString())
        biometric.setDiabetesType(requireContext(), view?.diabetesTypeProfileDropdown?.text.toString())
        loading = true
        BiometricService.instance.update(biometric).doOnSuccess {
            if (it.second.component2() == null) {
                UserManager.instance.saveBiometric(requireContext(), it.second.component1()!!)
                Toast.makeText(context, getString(R.string.update_profile), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
            loading = false
        }.subscribe()
    }

    private fun validateFields() : Boolean {
        view?.nameProfileInput?.text = view?.nameProfileInput?.text
        view?.firstNameProfileInput?.text = view?.firstNameProfileInput?.text
        view?.emailProfileInput?.text = view?.emailProfileInput?.text
        view?.newPasswordInput?.text = view?.newPasswordInput?.text
        view?.newPasswordConfirmInput?.text = view?.newPasswordConfirmInput?.text
        return view?.nameProfileInputLayout?.error == null
                && view?.firstNameProfileInputLayout?.error == null
                && view?.emailProfileInputLayout?.error == null
                && view?.newPasswordInputLayout?.error == null
                && view?.newPasswordConfirmInputLayout?.error == null
    }

    private fun changeProfilePicture(image: Bitmap) {
        loading = true
        UserService.instance.updatePicture<UserObject>(image, "me").doOnSuccess {
            if (it.second.component2() == null) {
                view?.imagePhotoProfile?.setImageBitmap(image)
                Toast.makeText(context, R.string.update_profile_picture, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
            loading = false
        }.subscribe()
    }

    private fun openSettings() {
        startActivity(Intent(context, SettingsActivity::class.java))
    }

    private fun logout() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        MaterialHandler.instance.handleTextInputLayoutSize(dialogView as ViewGroup)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.logoutNegativeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.logoutPositiveButton.setOnClickListener {
            dialog.dismiss()
            UserManager.instance.removePreferences(requireContext())
            AuthManager.instance.removePreferences(requireContext())
            Toast.makeText(context, getString(R.string.logout), Toast.LENGTH_SHORT).show()
            activity?.finish()
        }
        dialog.show()
    }

    private fun deactivateAccount() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_deactivate_account, null)
        MaterialHandler.instance.handleTextInputLayoutSize(dialogView as ViewGroup)
        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.deactivateAccountNegativeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.deactivateAccountPositiveButton.setOnClickListener {
            dialog.dismiss()
            UserService.instance.remove<UserObject>("me").doOnSuccess {
                if (it.second.component2() == null) {
                    AuthManager.instance.removePreferences(requireContext())
                    Toast.makeText(context, getString(R.string.deactivated), Toast.LENGTH_SHORT).show()
                    activity?.finish()
                } else {
                    Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
            }.subscribe()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestCode.GET_IMAGE.ordinal) {
                val imageStream: InputStream? = context?.contentResolver?.openInputStream(data?.data!!)
                changeProfilePicture(BitmapFactory.decodeStream(imageStream))
            } else if (requestCode == RequestCode.GET_PHOTO.ordinal) {
                changeProfilePicture(data?.extras?.get("data") as Bitmap)
            }
        }
    }

    override fun isLoading(): Boolean {
        return loading
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        this.view?.birthDateProfileInput?.setText(TimeHandler.instance.formatTimestamp(
            TimeHandler.instance.getTimestampDate(year, monthOfYear, dayOfMonth),
            requireContext().getString(R.string.format_date_birth)))
    }
}
