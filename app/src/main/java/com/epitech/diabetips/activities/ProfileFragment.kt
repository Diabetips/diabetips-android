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
import com.epitech.diabetips.utils.ImageHandler
import com.epitech.diabetips.utils.MaterialHandler
import com.epitech.diabetips.utils.NavigationFragment
import kotlinx.android.synthetic.main.dialog_change_picture.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.io.InputStream

class ProfileFragment : NavigationFragment(FragmentType.PROFILE) {

    enum class RequestCode { GET_IMAGE, GET_PHOTO }

    var loading: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        view.updateProfileButton.setOnClickListener {
            updateProfile()
        }
        view.logoutButton.setOnClickListener {
            AuthManager.instance.removePreferences(context!!)
            Toast.makeText(context, getString(R.string.logout), Toast.LENGTH_SHORT).show()
            activity?.finish()
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
        var error = false
        if (view?.nameProfileInput?.text.toString().isEmpty()) {
            view?.nameProfileInputLayout?.error = getString(R.string.name_invalid_error)
            error = true
        } else {
            view?.nameProfileInputLayout?.error = null
        }
        if (view?.firstNameProfileInput?.text.toString().isEmpty()) {
            view?.firstNameProfileInputLayout?.error = getString(R.string.first_name_invalid_error)
            error = true
        } else {
            view?.firstNameProfileInputLayout?.error = null
        }
        if (view?.emailProfileInput?.text.toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(view?.emailProfileInput?.text.toString()).matches()) {
            view?.emailProfileInputLayout?.error = getString(R.string.email_invalid_error)
            error = true
        } else {
            view?.emailProfileInputLayout?.error = null
        }
        if (view?.newPasswordInput?.text.toString().isNotEmpty()) {
            if (view?.newPasswordInput?.text.toString().length < resources.getInteger(R.integer.password_length)) {
                view?.newPasswordInputLayout?.error = getString(R.string.password_length_error)
                error = true
            } else {
                view?.newPasswordInputLayout?.error = null
            }
            if (view?.newPasswordInput?.text.toString() != view?.newPasswordConfirmInput?.text.toString()) {
                view?.newPasswordConfirmInputLayout?.error = getString(R.string.password_match_error)
                error = true
            } else {
                view?.newPasswordConfirmInputLayout?.error = null
            }
        } else {
            view?. newPasswordInputLayout?.error = null
            view?.newPasswordConfirmInputLayout?.error = null
        }
        return !error
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
