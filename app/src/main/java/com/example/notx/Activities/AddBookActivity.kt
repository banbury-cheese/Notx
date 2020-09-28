package com.example.notx.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.notx.Model.User
import com.example.notx.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.activity_add_post.*

class AddBookActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null
    private lateinit var schoolCodeBook: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Book Pictures")

        getSchoolCodeForPost()

        save_new_book_btn.setOnClickListener { uploadImage() }

        CropImage.activity()
            .setAspectRatio(115, 175)
            .start(this@AddBookActivity)

        close_add_book_btn.setOnClickListener {
            val intent = Intent(this@AddBookActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_add_book.setImageURI(imageUri)
        }

    }


    private fun uploadImage()
    {
        when{
            TextUtils.isEmpty(class_add_book.text.toString()) -> Toast.makeText(this, "Please enter the class.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(subject_add_book.text.toString()) -> Toast.makeText(this, "Please enter the subject.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(price_add_book.text.toString()) -> Toast.makeText(this, "Please enter the price.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(quality_add_book.text.toString()) -> Toast.makeText(this, "Please enter the quality.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(description_add_book.text.toString()) -> Toast.makeText(this, "Please write something about the book", Toast.LENGTH_LONG).show()

            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding New Book")
                progressDialog.setMessage("Please wait, we are adding your book...")
                progressDialog.show()

                val currentTime = System.currentTimeMillis().toString()

                val fileRef = storagePostPicRef!!.child( currentTime + ".jpg")

                var uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                })
                    .addOnCompleteListener (OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful)
                        {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Books")
                            val bookPostId = ref.push().key

                            val postMap = HashMap<String, Any>()
                            postMap["bookPostId"] = bookPostId!!
                            postMap["startTime"] = currentTime
                            postMap["quality"] = "Quality: " + quality_add_book.text.toString()
                            postMap["price"] = "Price: " + price_add_book.text.toString() +" Rs"
                            postMap["description"] = description_add_book.text.toString()
                            postMap["heading"] = subject_add_book.text.toString()+" Class "+ class_add_book.text.toString()
                            postMap["schoolCode"] = schoolCodeBook
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["bookImage"] = myUrl

                            ref.child(bookPostId).updateChildren(postMap)

                            Toast.makeText(this, "Book will be visible for the next 7 days", Toast.LENGTH_LONG).show()

                            val intent = Intent(this@AddBookActivity, MainActivity::class.java)
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

    private fun getSchoolCodeForPost(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    val user = p0.getValue(User::class.java)
                    schoolCodeBook = user!!.getSchoolCode().toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}