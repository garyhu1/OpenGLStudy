package com.se.glanimaldemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.se.glanimaldemo.gl.MoreTextureSurfaceView;

/**
 * Created by yb on 2019/1/11.
 */

public class MoreTextureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoreTextureSurfaceView surfaceView = new MoreTextureSurfaceView(this);
        setContentView(surfaceView);
    }
}
