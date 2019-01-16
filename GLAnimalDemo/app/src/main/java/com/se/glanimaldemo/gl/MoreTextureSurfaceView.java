package com.se.glanimaldemo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.se.glanimaldemo.gl.renderer.MoreTextureRenderer;

/**
 * Created by yb on 2019/1/11.
 */

public class MoreTextureSurfaceView extends GLSurfaceView {

    public MoreTextureSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        setRenderer(new MoreTextureRenderer(context));
    }
}
