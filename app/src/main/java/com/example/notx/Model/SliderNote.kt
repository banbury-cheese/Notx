package com.example.notx.Model

import com.example.notx.R

class SliderNote {

    var image: Int = R.drawable.chemistry_tile
        get() = field
        set(value) {
            field = value
        }

    constructor(image: Int) {
        this.image = image
    }

}