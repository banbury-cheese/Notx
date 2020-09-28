package com.example.notx.Model

import android.util.Log


class User {

    private var fullname: String = ""
    private var schoolCode: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var email: String = ""
    private var phoneNo: String = ""
    private var classSection: String = ""

    constructor(){

    }

    constructor(
        username: String,
        fullname: String,
        bio: String,
        image: String,
        uid: String,
        email: String,
        phoneNo: String,
        classSection: String
    ) {
        this.fullname = fullname
        this.schoolCode = username
        this.image = image
        this.uid = uid
        this.bio = bio
        this.email = email
        this.classSection = classSection
        this.phoneNo = phoneNo
    }


    fun getFullname(): String? {
        return convert(fullname)
    }

    fun setFullname(fullname: String) {
        this.fullname = fullname
    }

    fun getSchoolCode(): String? {
        Log.i("Error Check",schoolCode+"Constructor")
        return schoolCode
    }

    fun setSchoolCode(schoolCode: String) {
        this.schoolCode = schoolCode
    }

    fun getBio(): String? {
        return bio
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun getUid(): String? {
        return uid
    }

    fun setUid(uid: String) {
        this.uid = uid
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getPhoneNo(): String? {
        return phoneNo
    }

    fun setPhoneNo(phoneNo: String) {
        this.phoneNo = phoneNo
    }

    fun getclassSection(): String? {
        return classSection
    }

    fun setclassSection(classSection: String) {
        this.classSection = classSection
    }

    fun convert(str: String): String? {

        // Create a char array of given String
        val ch = str.toCharArray()
        for (i in 0 until str.length) {

            // If first character of a word is found
            if (i == 0 && ch[i] != ' ' ||
                ch[i] != ' ' && ch[i - 1] == ' '
            ) {

                // If it is in lower-case
                if (ch[i] >= 'a' && ch[i] <= 'z') {

                    // Convert into Upper-case
                    ch[i] = (ch[i] - 'a' + 'A'.toInt()).toChar()
                }
            } else if (ch[i] >= 'A' && ch[i] <= 'Z') // Convert into Lower-Case
                ch[i] = (ch[i] + 'a'.toInt() - 'A'.toInt())
        }

        // Convert the char array to equivalent String
        return String(ch)
    }
}

