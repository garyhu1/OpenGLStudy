package com.se.glanimaldemo.gl.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.se.glanimaldemo.shape.MulTextureCube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2019/1/14.
 */

public class PumpKinTextureRender implements GLSurfaceView.Renderer {

    private MulTextureCube mulTextureCube;
    private float angleTex=0;
    private float speechTex=-1.5f;

    public PumpKinTextureRender(Context context){

        mulTextureCube=new MulTextureCube(context);


    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        gl.glClearColor(1.0f,1.0f,0.0f,1.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glDisable(GL10.GL_DITHER);

        mulTextureCube.loadTexture(gl);
        gl.glEnable(GL10.GL_TEXTURE_2D);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        if(height==0){
            height=1;
        }
        float aspect=(float)width/height;

        gl.glViewport(0,0,width,height);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();
        gl.glTranslatef(0.0f,0.0f,-8.0f);
        gl.glRotatef(angleTex,0.1f,1.0f,0.2f);
        mulTextureCube.draw(gl);

        angleTex+=speechTex;

    }
}
