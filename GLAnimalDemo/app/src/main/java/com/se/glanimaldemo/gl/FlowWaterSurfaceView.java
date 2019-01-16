package com.se.glanimaldemo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.se.glanimaldemo.gl.renderer.FlowWaterRenderer;

/**
 * Created by yb on 2019/1/11.
 */

public class FlowWaterSurfaceView extends GLSurfaceView {

    public FlowWaterSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        setRenderer(new FlowWaterRenderer(context));
    }
}
