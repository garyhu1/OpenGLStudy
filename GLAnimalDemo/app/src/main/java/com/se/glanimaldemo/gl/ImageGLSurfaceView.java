package com.se.glanimaldemo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.se.glanimaldemo.gl.renderer.ImageGLRender;


/**
 * Created by yb on 2018/12/28.
 */

public class ImageGLSurfaceView extends GLSurfaceView {

    private final ImageGLRender mRenderer;

    public ImageGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        mRenderer = new ImageGLRender(context);
//        MyTextureRenderer renderer = new MyTextureRenderer(context,"filter/half_color_vertex.sh", "filter/half_color_fragment.sh");
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
//        setRenderer(renderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

}
