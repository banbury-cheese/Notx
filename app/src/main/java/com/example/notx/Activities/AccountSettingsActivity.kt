package com.example.notx.Activities

import android.app.Activity
import android.app.ProgressDialog
import com.google.android.gms.tasks.Continuation
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.notx.Model.User
import com.example.notx.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*

class AccountSettingsActivity : AppCompatActivity()
{
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")


        profile_edit_logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }


        edit_profile_image.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingsActivity)
        }

        save_profile_edit_btn.setOnClickListener {
            if (checker == "clicked")
            {
                uploadImageAndUpdateInfo()
            }
            else
            {
                updateUserInfoOnly()
            }
        }

        close_profile_edit_btn.setOnClickListener {
            val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        userInfo()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode == Activity.RESULT_OK  &&  data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_profile_edit.setImageURI(imageUri)
        }
    }

    private fun updateUserInfoOnly() {
        when {

            TextUtils.isEmpty(fullname_profile_edit.text.toString()) -> Toast.makeText(this, "Please write your full name first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email_profile_edit.text.toString()) -> Toast.makeText(this, "Please write your school Email first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(bio_profile_edit.text.toString()) -> Toast.makeText(this, "Please write something about you first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(phoneNo_profile_edit.text.toString()) -> Toast.makeText(this, "Please write your phone no. first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(class_profile_edit.text.toString()) -> Toast.makeText(this, "Please enter your class first.", Toast.LENGTH_LONG).show()

            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = fullname_profile_edit.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_edit.text.toString()
                userMap["email"] = email_profile_edit.text.toString()
                userMap["phoneNo"] = phoneNo_profile_edit.text.toString().toLowerCase()
                userMap["classSection"] = class_profile_edit.text.toString().toLowerCase()


                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage())
                        .placeholder(R.drawable.profile)
                        .into(profile_image_profile_edit)

                    fullname_profile_edit.setText(user!!.getFullname())
                    email_profile_edit.setText(user!!.getEmail())
                    bio_profile_edit.setText(user!!.getBio())
                    phoneNo_profile_edit.setText(user!!.getPhoneNo())
                    class_profile_edit.setText(user!!.getclassSection())

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun uploadImageAndUpdateInfo()
    {
        when
        {
            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(fullname_profile_edit.text.toString()) -> Toast.makeText(this, "Please write your full name first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email_profile_edit.text.toString()) -> Toast.makeText(this, "Please write your school Email first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(bio_profile_edit.text.toString()) -> Toast.makeText(this, "Please write something about you first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(phoneNo_profile_edit.text.toString()) -> Toast.makeText(this, "Please write your phone no. first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(class_profile_edit.text.toString()) -> Toast.makeText(this, "Please enter your class first.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = fullname_profile_edit.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile_edit.text.toString()
                        userMap["email"] = email_profile_edit.text.toString()
                        userMap["phoneNo"] = phoneNo_profile_edit.text.toString().toLowerCase()
                        userMap["classSection"] = class_profile_edit.text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account Information has been updated successfully.", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else
                    {
                        progressDialog.dismiss()
                    }
                } )
            }
        }
    }

}
