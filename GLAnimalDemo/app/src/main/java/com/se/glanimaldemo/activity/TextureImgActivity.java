package com.se.glanimaldemo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateInterpolator;

import com.se.glanimaldemo.gl.ImageGLSurfaceView;
import com.se.glanimaldemo.gl.TrianglesGLSurfaceView;

/**
 * Created by yb on 2018/12/29.
 */

public class TextureImgActivity extends AppCompatActivity {

    private ImageGLSurfaceView glSurfaceView;
    private float scale = 0.5f;
    private boolean insert = true;
    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new ImageGLSurfaceView(TextureImgActivity.this);
        setContentView(glSurfaceView);

    }

}
