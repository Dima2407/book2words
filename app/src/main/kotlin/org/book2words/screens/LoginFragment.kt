package org.book2words.screens

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.backendless.BackendlessUser
import org.book2words.MainActivity
import org.book2words.R
import org.book2words.services.net.B2WHandler
import org.book2words.services.net.B2WService

public class LoginFragment : Fragment() {

    private var loginView: EditText? = null
    private var passwordView: EditText? = null
    private var loginButton: View? = null

    private val authTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            loginButton!!.setEnabled(loginView!!.getText().length() > 0
                    && passwordView!!.getText().length() > 0)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginView = view!!.findViewById(R.id.edit_login) as EditText
        passwordView = view.findViewById(R.id.edit_password) as EditText
        loginButton = view.findViewById(R.id.button_login)
        loginButton!!.setOnClickListener({
            login()
        })
        loginView!!.addTextChangedListener(authTextWatcher)
        passwordView!!.addTextChangedListener(authTextWatcher)
        passwordView!!.setOnEditorActionListener(object : TextView.OnEditorActionListener {

            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login()
                    handled = true
                }
                return handled
            }

        })
    }

    private fun login() {
        val login = loginView!!.getText().toString()
        val password = passwordView!!.getText().toString()
        B2WService.login(getActivity(), "qwerty@qwerty.com", "qwerty", object : B2WHandler<BackendlessUser> () {
            override fun onResult(success: Boolean, data: BackendlessUser) {
                val intent = Intent(getActivity(), javaClass<MainActivity>())
                startActivity(intent)
            }
        })
    }
}