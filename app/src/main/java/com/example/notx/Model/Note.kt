package com.example.notx.Model

class Note {

     var chapter: String = ""
        get() = field
        set(value) {
            field = value
        }

     var notePostId: String = ""
        get() = field
        set(value) {
            field = value
        }

     var pdfUrl: String = ""
        get() = field
        set(value) {
            field = value
        }

     var publisher: String = ""
        get() = field
        set(value) {
            field = value
        }

     var schoolCode: String = ""
        get() = field
        set(value) {
            field = value
        }


    constructor(
        chapter: String,
        notePostId: String,
        pdfUrl: String,
        publisher: String,
        schoolCode: String,
    ) {
        this.chapter = chapter
        this.notePostId = notePostId
        this.pdfUrl = pdfUrl
        this.publisher = publisher
        this.schoolCode = schoolCode
    }

    constructor()



}