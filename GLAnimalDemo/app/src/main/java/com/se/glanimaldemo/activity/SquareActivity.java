package com.se.glanimaldemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.se.glanimaldemo.gl.ImageGLSurfaceView;
import com.se.glanimaldemo.gl.OneGLSurfaceView;

/**
 * Created by yb on 2018/12/29.
 */

public class SquareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneGLSurfaceView glSurfaceView = new OneGLSurfaceView(SquareActivity.this);
        setContentView(glSurfaceView);
    }
}
