package com.se.glanimaldemo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.se.glanimaldemo.gl.renderer.CubeRenderer;

/**
 * Created by yb on 2019/1/12.
 */

public class CubeSurfaceView extends GLSurfaceView {

    public CubeSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        setRenderer(new CubeRenderer(context));
    }
}
