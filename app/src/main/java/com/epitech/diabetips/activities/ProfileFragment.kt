package com.epitech.diabetips.activities

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epitech.diabetips.managers.AccountManager
import com.epitech.diabetips.R
import com.epitech.diabetips.services.UserService
import com.epitech.diabetips.storages.AccountObject
import com.epitech.diabetips.utils.MaterialHandler
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        MaterialHandler.instance.handleTextInputLayoutSize(view as ViewGroup)
        view.updateProfileButton.setOnClickListener {
            updateProfile()
        }
        getAccountInfo(view)
        return view
    }

    private fun getAccountInfo(view: View? = this.view) {
        val account = AccountManager.instance.getAccount(context!!)
        view?.firstNameProfileInput?.setText(account.first_name)
        view?.nameProfileInput?.setText(account.last_name)
        view?.emailProfileInput?.setText(account.email)
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
            UserService.instance.updateUser(account).doOnSuccess {
                if (it.second.component2() == null) {
                    AccountManager.instance.saveAccount(context!!, it.second.component1()!!)
                    Toast.makeText(context, getString(R.string.update_profile), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it.second.component2()!!.exception.message, Toast.LENGTH_SHORT).show()
                }
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
}
