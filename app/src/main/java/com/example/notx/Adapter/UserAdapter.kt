package com.example.notx.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notx.Activities.MainActivity
import com.example.notx.Fragments.SettingsFragment
import com.example.notx.Model.User
import com.example.notx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*


class UserAdapter(private var mContext: Context,
                  private var mUser: List<User>,
                  private var isFragment: Boolean =false) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.bioTextView.text = user.getBio()
        holder.userFullnameTextView.text = user.getFullname()
        holder.whatsappIcon.tag = user.getPhoneNo()
        holder.emailIcon.tag = user.getEmail()
        holder.callIcon.tag = user.getPhoneNo()
        holder.classTextView.text = user.getclassSection()

        Picasso.get().load(user.getImage())
            .placeholder(R.drawable.profile)
            .into(holder.userProfileImage)

        holder.itemView.setOnClickListener(View.OnClickListener {
            if (isFragment)
            {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.getUid())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SettingsFragment()).commit()
            }
            else
            {
                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("publisherId", user.getUid())
                mContext.startActivity(intent)
            }
        })





        holder.emailIcon.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:${holder.emailIcon.tag.toString()}"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Wanted to contact you regarding...")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi there")
            mContext.startActivity(Intent.createChooser(emailIntent, "Send Mail"))

        }

        holder.callIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:${holder.callIcon.tag.toString()}"))
            mContext.startActivity(intent)
        }

        holder.whatsappIcon.setOnClickListener{
            val smsNumber = holder.whatsappIcon.tag.toString() // E164 format without '+' sign

            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi ${holder.userFullnameTextView?.text}")
            sendIntent.putExtra("jid",
                "$smsNumber@s.whatsapp.net") //phone number without "+" prefix

            sendIntent.setPackage("com.whatsapp")
            mContext.startActivity(sendIntent)
        }

    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){

        var userFullnameTextView: TextView = itemView.findViewById(R.id.full_name_profile_search)
        var bioTextView: TextView = itemView.findViewById(R.id.bio_profile_search)
        var classTextView: TextView = itemView.findViewById(R.id.class_profile_search)
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.profile_image_profile_search)
        var viewProfileButton: Button = itemView.findViewById(R.id.view_profile_profile_search)
        var whatsappIcon: ImageView = itemView.findViewById(R.id.whatsapp_icon_profile_search)
        var callIcon: ImageView = itemView.findViewById(R.id.call_icon_profile_search)
        var emailIcon: ImageView = itemView.findViewById(R.id.email_icon_profile_search)

    }



}