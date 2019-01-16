package com.se.glanimaldemo.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.se.glanimaldemo.gl.renderer.CubeTextureRenderer;

/**
 * Created by yb on 2019/1/12.
 */

public class CubeTextureSurfaceView extends GLSurfaceView {

    private CubeTextureRenderer mRenderer;

    public CubeTextureSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new CubeTextureRenderer(context);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setAngle(float x,float y){
        mRenderer.setAngle(x,y);
        requestRender();
    }
}
