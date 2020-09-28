package com.example.notx.Model

class Post {

    private var postid: String = ""
    private var postimage: String = ""
    private var heading: String = ""
    private var description: String = ""
    private var schoolCode: String = ""
    private var publisher: String = ""

    constructor()


    constructor(postid: String, postimage: String, heading: String, description: String, schoolCode: String, publisher: String) {
        this.postid = postid
        this.postimage = postimage
        this.heading = heading
        this.description = description
        this.schoolCode = schoolCode
        this.publisher = publisher
    }


    fun getPostid(): String{
        return postid
    }

    fun getPostimage(): String{
        return postimage
    }

    fun getPublisher(): String{
        return publisher
    }

    fun getSchoolCode(): String{
        return schoolCode
    }

    fun getHeading(): String{
        return heading
    }

    fun getDescription(): String{
        return description
    }

    fun setPostid(postid: String)
    {
        this.postid = postid
    }

    fun setPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun setsSchoolCode(schoolCode: String)
    {
        this.schoolCode = schoolCode
    }

    fun setPostimage(postimage: String)
    {
        this.postimage = postimage
    }
    fun setHeading(heading: String)
    {
        this.heading = heading
    }

    fun setDescription(description: String)
    {
        this.description = description
    }
}