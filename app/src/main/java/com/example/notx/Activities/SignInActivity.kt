package com.example.notx.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signup_link_btn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        login_btn.setOnClickListener{
            loginUser()
        }

        forgot_password_button.setOnClickListener {

            val email = email_login.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "email-id is required to reset password", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Password reset link sent to your email id",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else{
                            Toast.makeText(
                                this,
                                "Password reset link not sent",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    private fun loginUser() {

        val email = email_login.text.toString()
        val password = password_login.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "email-id is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "password is required",
                Toast.LENGTH_SHORT
            ).show()

            else-> {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("LogIn")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                    if (task.isSuccessful)
                    {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Successfully Signed In", Toast.LENGTH_SHORT)

                        val intent =  Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val  message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT)
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null)
        {
            val intent =  Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}