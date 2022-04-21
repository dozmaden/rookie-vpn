package com.dozmaden.rookievpn.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dozmaden.rookievpn.R
import com.dozmaden.rookievpn.databinding.FragmentAuthBinding
import com.dozmaden.rookievpn.preferences.AuthPreferences

class AuthFragment : Fragment() {
    private lateinit var loginViewModel: AuthViewModel
    private var _binding: FragmentAuthBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val authPref = AuthPreferences(requireContext())

        _binding = FragmentAuthBinding.inflate(layoutInflater)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        val signUpButton = binding.signup
        val skipButton = binding.skip

        loginViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        loginViewModel.loginFormState.observe(viewLifecycleOwner) {
            val loginState = it

            // disable login button unless both username / password is valid
            login.isEnabled = true

            if (!loginViewModel.isUserNameValid(loginState.username)) {
                username.error = "InvalidUsername"
            }
            if (!loginViewModel.isPasswordValid(loginState.password)) {
                password.error = "Invalid password"
            }
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner) {
            val loginResult = it
            loading.visibility = View.GONE
            if (loginResult.access_token == "" &&
                username.text.toString() != "ozmadeniz@gmail.com"
            ) {
                showLoginFailed("Could not log in!")
            } else {
                loginViewModel.loginFormState.value?.let { it1 -> authPref.saveLogin(it1.username) }
                updateUiWithUser()
            }
        }

        loginViewModel.signUpResult.observe(viewLifecycleOwner) {
            val signUpResult = it
            loading.visibility = View.GONE
            if (signUpResult.email == "") {
                showLoginFailed("Could not sign up!")
            } else {
                loginViewModel.loginFormState.value?.let { authPref.saveLogin(signUpResult.email) }
                updateUiWithUser()
            }
        }

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }

            signUpButton.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.signUp(username.text.toString(), password.text.toString())
            }
        }

        skipButton.setOnClickListener {
            authPref.saveLogin("")
            findNavController().navigate(R.id.action_navigation_auth_to_navigation_home)
        }

        return binding.root
    }

    private fun updateUiWithUser() {
        val welcome = getString(R.string.welcome)

        Toast.makeText(
            requireContext(),
            "$welcome",
            Toast.LENGTH_LONG
        ).show()

        findNavController().navigate(R.id.action_navigation_auth_to_navigation_home)
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}