package com.epitech.diabetips.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.R
import com.epitech.diabetips.managers.AuthManager
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import com.epitech.diabetips.textWatchers.EmailWatcher
import com.epitech.diabetips.textWatchers.InputWatcher
import com.epitech.diabetips.textWatchers.PasswordConfirmWatcher
import com.epitech.diabetips.textWatchers.PasswordWatcher
import com.epitech.diabetips.utils.ImageHandler
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import kotlinx.android.synthetic.main.dialog_change_picture.view.*
import kotlinx.android.synthetic.main.dialog_deactivate_account.view.*
import kotlinx.android.synthetic.main.dialog_logout.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.InputStream

class ProfileFragment : NavigationFragment(FragmentType.PROFILE) {

    enum class RequestCode { GET_IMAGE, GET_PHOTO }

    private var loading: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        view.nameProfileInput.addTextChangedListener(InputWatcher(context, view.nameProfileInputLayout, true, R.string.name_invalid_error))
        view.firstNameProfileInput.addTextChangedListener(InputWatcher(context, view.firstNameProfileInputLayout, true, R.string.first_name_invalid_error))
        view.emailProfileInput.addTextChangedListener(EmailWatcher(context, view.emailProfileInputLayout))
        view.newPasswordInput.addTextChangedListener(PasswordWatcher(context, view.newPasswordInputLayout, true, true))
        view.newPasswordConfirmInput.addTextChangedListener(PasswordConfirmWatcher(context, view.newPasswordConfirmInputLayout, view.newPasswordInput))
        view.updateProfileButton.setOnClickListener {
            updateProfile()
        }
        view.logoutButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
            MaterialHandler.instance.handleTextInputLayoutSize(dialogView as ViewGroup)
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogView.logoutNegativeButton.setOnClickListener {
                dialog.dismiss()
            }
            dialogView.logoutPositiveButton.setOnClickListener {
                dialog.dismiss()
                AuthManager.instance.removePreferences(context!!)
                Toast.makeText(context, getString(R.string.logout), Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
            dialog.show()
        }

        view.deactivateAccountButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_deactivate_account, null)
            MaterialHandler.instance.handleTextInputLayoutSize(dialogView as ViewGroup)
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogView.deactivateAccountNegativeButton.setOnClickListener {
                dialog.dismiss()
            }
            dialogView.deactivateAccountPositiveButton.setOnClickListener {
                dialog.dismiss()
                UserService.instance.deactivateAccount().doOnSuccess {
                    if (it.second.component2() == null) {
                        AuthManager.instance.removePreferences(context!!)
                        Toast.makeText(context, getString(R.string.deactivated), Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    } else {
                        Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }.subscribe()
            }
            dialog.show()


        }

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
            dialog.show()
        }
        getAccountInfo(view)
        return view
    }

    private fun getAccountInfo(view: View? = this.view) {
        val account = AccountManager.instance.getAccount(context!!)
        if (account.uid != "") {
            setAccountInfo(account, view)
        } else {
            loading = true
            UserService.instance.getUser().doOnSuccess {
                if (it.second.component2() == null) {
                    AccountManager.instance.saveAccount(context!!, it.second.component1()!!)
                    setAccountInfo(it.second.component1()!!, view)
                } else {
                    Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
                loading = false
            }.subscribe()
        }
    }

    private fun setAccountInfo(account: AccountObject, view: View? = this.view) {
        view?.firstNameProfileInput?.setText(account.first_name)
        view?.nameProfileInput?.setText(account.last_name)
        view?.emailProfileInput?.setText(account.email)
        ImageHandler.instance.loadImage(view?.imagePhotoProfile as ImageView, context!!, UserService.instance.getPictureUrl(account.uid), R.drawable.ic_person, false)
    }

    private fun updateProfile() {
        if (validateFields()) {
            val account: AccountObject = AccountManager.instance.getAccount(context!!)
            account.last_name = view?.nameProfileInput?.text.toString()
            account.first_name = view?.firstNameProfileInput?.text.toString()
            account.email = view?.emailProfileInput?.text.toString()
            if (view?.newPasswordInput?.text.toString().isNotEmpty()) {
                account.password = view?.newPasswordInput?.text.toString()
            }
            loading = true
            UserService.instance.updateUser(account).doOnSuccess {
                if (it.second.component2() == null) {
                    AccountManager.instance.saveAccount(context!!, it.second.component1()!!)
                    Toast.makeText(context, getString(R.string.update_profile), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
                loading = false
            }.subscribe()
        }
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
        UserService.instance.updatePicture(image).doOnSuccess {
            if (it.second.component2() == null) {
                view?.imagePhotoProfile?.setImageBitmap(image)
                Toast.makeText(context, R.string.update_profile_picture, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
            }
            loading = false
        }.subscribe()

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
}
