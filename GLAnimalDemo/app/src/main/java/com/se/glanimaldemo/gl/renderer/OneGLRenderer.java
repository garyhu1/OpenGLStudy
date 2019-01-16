package com.se.glanimaldemo.gl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.se.glanimaldemo.shape.Square;
import com.se.glanimaldemo.shape.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2018/12/27.
 */

public class OneGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mTriangle;
    private Square mSquare;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    // 定义一个旋转矩阵
    private float[] mRotationMatrix = new float[16];


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // 初始化三角形
        mTriangle = new Triangle();
        // 初始正方形
        mSquare = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        // 这个投影矩阵被应用于对象坐标在onDrawFrame（）方法中
        // 透视投影
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    private float ratio = 0.5f;
    private boolean insert = true;

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setIdentityM(mRotationMatrix,0);

        // Set the camera position (View matrix)
        // 设置相机视图
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation 转换矩阵（变换矩阵）
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // 创建一个旋转矩阵
//        long time = SystemClock.uptimeMillis() % 4000L;
//        Log.e("EEE","time = "+time);
//        Log.e("EEE","SystemClock.uptimeMillis() = "+SystemClock.uptimeMillis());
//        float angle = 0.090f * ((int) time);

        if(insert){
            ratio += 0.01;
        }else  {
            ratio -= 0.01;
        }

        if(ratio <= 0.5f){
            ratio = 0.5f;
            insert = true;
        }else if(ratio >= 1.0f){
            ratio = 1.0f;
            insert = false;
        }


        float[] aa = new float[16];
        Matrix.scaleM(aa,0,mMVPMatrix,0,ratio,ratio,1);

//        mTriangle.draw(mMVPMatrix);
//        mTriangle.draw(scratch);
        mSquare.draw(aa);
    }

    public static int loadShader(int type, String shaderCode){
        // 创造顶点着色器类型(GLES20.GL_VERTEX_SHADER)
        // 或者是片段着色器类型 (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        // 添加上面编写的着色器代码并编译它
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

}
