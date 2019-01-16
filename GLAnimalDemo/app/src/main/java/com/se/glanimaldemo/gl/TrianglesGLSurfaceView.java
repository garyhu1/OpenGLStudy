package com.se.glanimaldemo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.se.glanimaldemo.gl.renderer.TrianglesRenderer;

/**
 * Created by yb on 2018/12/29.
 */

public class TrianglesGLSurfaceView extends GLSurfaceView {

    private final TrianglesRenderer mRenderer;
    public TrianglesGLSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        mRenderer = new TrianglesRenderer();
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
