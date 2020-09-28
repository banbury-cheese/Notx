package com.example.notx.Model

class Book {

     var bookPostId: String = ""
        get() = field
        set(value) {
            field = value
        }

     var bookImage: String = ""
        get() = field
        set(value) {
            field = value
        }

     var heading: String = ""
        get() = field
        set(value) {
            field = value
        }

     var description: String = ""
        get() = field
        set(value) {
            field = value
        }

     var schoolCode: String = ""
        get() = field
        set(value) {
            field = value
        }

     var startTime: String = ""
        get() = field
        set(value) {
            field = value
        }

         var price: String = ""
        get() = field
        set(value) {
            field = value
        }

     var quality: String = ""
        get() = field
        set(value) {
            field = value
        }

     var publisher: String = ""
        get() = field
        set(value) {
            field = value
        }


    constructor()

    constructor(
        bookPostId: String,
        bookImage: String,
        heading: String,
        description: String,
        schoolCode: String,
        endTime: String,
        price: String,
        quality: String,
        publisher: String,
    ) {
        this.bookPostId = bookPostId
        this.bookImage = bookImage
        this.heading = heading
        this.description = description
        this.schoolCode = schoolCode
        this.startTime = endTime
        this.price = price
        this.quality = quality
        this.publisher = publisher
    }


}