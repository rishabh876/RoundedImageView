package com.rishabhharit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Glide.with(this)
                .load("https://source.unsplash.com/random/400x400/?v=1")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.placeholder(ColorDrawable(Color.LTGRAY))
                .into(circle_iv)

        Glide.with(this)
                .load("https://source.unsplash.com/random/400x400/?v=2")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.placeholder(ColorDrawable(Color.LTGRAY))
                .into(rounded_iv)

        Glide.with(this)
                .load("https://source.unsplash.com/random/400x400/?v=3")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.placeholder(ColorDrawable(Color.LTGRAY))
                .into(rounded_iv_1)

        Glide.with(this)
                .load("https://source.unsplash.com/random/400x400/?v=4")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                //.placeholder(ColorDrawable(Color.LTGRAY))
                .into(rounded_iv_2)
    }
}
