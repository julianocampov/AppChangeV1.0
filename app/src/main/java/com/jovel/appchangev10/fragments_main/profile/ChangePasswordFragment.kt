package com.jovel.appchangev10.fragments_main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jovel.appchangev10.R
import com.jovel.appchangev10.databinding.FragmentChangePasswordBinding


class ChangePasswordFragment : Fragment() {

    private lateinit var changePasswordBinding: FragmentChangePasswordBinding
    private lateinit var auth: FirebaseAuth
    private val args: ChangePasswordFragmentArgs by navArgs()
    private lateinit var user1 : com.jovel.appchangev10.model.User
    private lateinit var password: String
    private lateinit var newPassword: String
    private lateinit var confirmNewPassword: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        changePasswordBinding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        user1 = args.user

        buttonListener()
        onChangeListener()

        return changePasswordBinding.root
    }

    private fun buttonListener() {
        changePasswordBinding.saveButton.setOnClickListener {

            readTextInputs()

            if (notEmptyFields(password, newPassword, confirmNewPassword)) {
                updatePassword()
            } else {
                Toast.makeText(requireContext(), getString(R.string.warning_login_empty), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePassword() {
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(user1.email!!, password)

        user?.reauthenticate(credential)?.addOnCompleteListener { reauth ->
            if (reauth.isSuccessful) {
                if (newPassword != confirmNewPassword) {
                    changePasswordBinding.confirmPasswordTextInputLayout.error =
                        getString(R.string.password_not_match)
                } else {
                    user.updatePassword(confirmNewPassword)
                        .addOnCompleteListener { update ->
                            if (update.isSuccessful) {
                                Toast.makeText(requireContext(), getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
                                cleanViews()
                            } else {
                                Toast.makeText(requireContext(), getString(R.string.password_not_updated), Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                changePasswordBinding.oldPasswordTextInputLayout.error =
                    getString(R.string.incorrect_password)
            }
        }
    }

    private fun cleanViews() {
        with(changePasswordBinding){
            oldPasswordEditText.setText("")
            newPasswordEditText.setText("")
            confirmPasswordEditText.setText("")
        }
    }

    private fun notEmptyFields(
        text1: String,
        text2: String,
        text3: String
    ): Boolean {
        return text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty()
    }

    private fun readTextInputs() {
        with(changePasswordBinding) {
            password = oldPasswordEditText.text.toString()
            newPassword = newPasswordEditText.text.toString()
            confirmNewPassword = confirmPasswordEditText.text.toString()
        }
    }

    private fun onChangeListener() {
        with(changePasswordBinding) {
            newPasswordEditText.doAfterTextChanged {
                newPasswordTextInputLayout.error = null
            }

            oldPasswordEditText.doAfterTextChanged {
                oldPasswordTextInputLayout.error = null
            }

            confirmPasswordEditText.doAfterTextChanged {
                confirmPasswordTextInputLayout.error = null
            }
        }
    }
}