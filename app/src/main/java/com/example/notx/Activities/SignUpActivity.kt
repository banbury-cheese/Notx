package com.example.notx.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signin_link_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signup_btn.setOnClickListener{
            CreateAccount()
        }

        no_school_code_button.setOnClickListener {
            val url = "https://docs.google.com/forms/d/e/1FAIpQLScsWSidSpJJ8HnyAvNBqWKrIBoern-1A8deDy97D-sm0ybObw/viewform"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

    private fun CreateAccount() {
        val fullname = fullname_signup.text.toString()
        val schoolCode = school_code_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()
        val classSection = class_signup.text.toString()
        val phoneNo = phoneNo_signup.text.toString()

        when{
            TextUtils.isEmpty(fullname) -> Toast.makeText(
                this,
                "full name is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(phoneNo) -> Toast.makeText(
                this,
                "Phone Number is required",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(schoolCode) -> Toast.makeText(
                this,
                "school code is required",
                Toast.LENGTH_SHORT
            ).show()
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
            TextUtils.isEmpty(classSection) -> Toast.makeText(
                this,
                "Class is required",
                Toast.LENGTH_SHORT
            ).show()
            else->{

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful)
                        {
                            saveUserInfo(
                                fullname,
                                schoolCode,
                                email,
                                classSection,
                                phoneNo,
                                progressDialog
                            )
                        }
                        else
                        {
                            val  message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(
        fullname: String,
        schoolCode: String,
        email: String,
        classSection: String,
        phoneNo: String,
        progressDialog: ProgressDialog
    ) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullname.toLowerCase()
        userMap["email"] = email
        userMap["schoolCode"] = schoolCode
        userMap["classSection"] = classSection
        userMap["phoneNo"] = phoneNo
        userMap["bio"] = "I can touch my nose with my tongue"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/notx-3a6e4.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=08cc3663-03e4-4841-aba8-1158dee0088c"

        userRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener{ task->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this,
                        "Account has been created successfully",
                        Toast.LENGTH_SHORT
                    )

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