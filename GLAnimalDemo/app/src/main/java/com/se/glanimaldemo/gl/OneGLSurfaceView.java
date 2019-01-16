package com.se.glanimaldemo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.se.glanimaldemo.gl.renderer.OneGLRenderer;

/**
 * Created by yb on 2018/12/27.
 */

public class OneGLSurfaceView extends GLSurfaceView {

    private final OneGLRenderer mRenderer;
    public OneGLSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        mRenderer = new OneGLRenderer();
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

}
