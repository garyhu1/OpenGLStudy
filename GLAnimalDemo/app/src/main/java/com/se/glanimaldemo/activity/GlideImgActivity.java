package com.se.glanimaldemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.se.glanimaldemo.R;
import com.se.glanimaldemo.view.CustomRoundAngleImageView;

/**
 * Created by yb on 2019/1/9.
 */

public class GlideImgActivity extends AppCompatActivity {

    private CustomRoundAngleImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_img);

        img = findViewById(R.id.img);

        Glide.with(this)
                .load( "https://lkimgyt.luokuang.com/1543975297448622093.png")
//                .load("http://p99.pstatp.com/large/pgc-image/c9bab1ec69014668a27321138fb96548")
//                .bitmapTransform(new RoundedCornersTransformation(this, 24, 0, RoundedCornersTransformation.CornerType.ALL))
                .asGif()
                .into(img);
    }
}
