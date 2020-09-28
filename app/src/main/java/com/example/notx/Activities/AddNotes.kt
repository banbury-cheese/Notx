package com.example.notx.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.activity_add_notes.*

class AddNotes : AppCompatActivity() {

    private var storagePostPicRef: StorageReference? = null
    var pdfUri: Uri? = null
    private var myUrl = ""
    private lateinit var schoolCodeNotes: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Notes")
        getSchoolCodeForPost()

        close_add_notes_btn.setOnClickListener {
            val intent = Intent(this@AddNotes, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        save_new_notes_btn.setOnClickListener { uploadPDF() }

        upload_button.setOnClickListener(View.OnClickListener { selectPDF() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            pdfUri = data.data
            uploadPDF()
        }
    }

    private fun uploadPDF()
    {
        when{
            TextUtils.isEmpty(subject_name_add_note.text.toString()) -> Toast.makeText(this, "Please enter the class.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(class_add_note.text.toString()) -> Toast.makeText(this, "Please enter the subject.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(chapter_name_add_note.text.toString()) -> Toast.makeText(this, "Please enter the price.", Toast.LENGTH_LONG).show()
            pdfUri == null -> Toast.makeText(this, "Please select pdf first.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding New PDF")
                progressDialog.setMessage("Please wait, we are adding your PDF...")
                progressDialog.show()

                val currentTime = System.currentTimeMillis().toString()

                val fileRef = storagePostPicRef!!.child( currentTime + ".pdf")

                var uploadTask = fileRef.putFile(pdfUri!!)

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

                            val ref = FirebaseDatabase.getInstance().reference.child("Notes").child(class_add_note.text.toString()).child(subject_name_add_note.text.toString())
                            val notePostId = ref.push().key

                            val postMap = HashMap<String, Any>()

                            postMap["notePostId"] = notePostId!!
                            postMap["chapter"] = chapter_name_add_note.text.toString()
                            postMap["schoolCode"] = schoolCodeNotes
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["pdfUrl"] = myUrl

                            ref.child(notePostId).updateChildren(postMap)

                            val intent = Intent(this@AddNotes, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            progressDialog.dismiss()
                            Toast.makeText(this, "PDF added successfully", Toast.LENGTH_LONG).show()
                        }
                        else
                        {
                            progressDialog.dismiss()
                        }
                    } )
            }
        }
    }

    private fun selectPDF() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select pdf file"), 1)
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
                    schoolCodeNotes = user!!.getSchoolCode().toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}