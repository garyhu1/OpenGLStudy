package com.se.glanimaldemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.se.glanimaldemo.gl.ImageGLSurfaceView;
import com.se.glanimaldemo.gl.OneGLSurfaceView;
import com.se.glanimaldemo.gl.TrianglesGLSurfaceView;

/**
 * Created by yb on 2018/12/29.
 */

public class TrianglesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrianglesGLSurfaceView glSurfaceView = new TrianglesGLSurfaceView(TrianglesActivity.this);
        setContentView(glSurfaceView);
    }
}
