package com.se.glanimaldemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.se.glanimaldemo.gl.FlowWaterSurfaceView;

/**
 * Created by yb on 2019/1/11.
 */

public class FlowWaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlowWaterSurfaceView surfaceView = new FlowWaterSurfaceView(this);
        setContentView(surfaceView);
    }
}
