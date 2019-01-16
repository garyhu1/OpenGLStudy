package com.se.glanimaldemo.gl.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.se.glanimaldemo.R;
import com.se.glanimaldemo.utils.BufferTransformUtil;
import com.se.glanimaldemo.utils.ShaderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2019/1/11.
 */

public class FlowWaterRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private static final String VERTEX_SHADER = "" +
            "uniform mat4 u_Matrix;\n" +
            "attribute vec4 a_Position;\n" +
            // 纹理坐标：2个分量，S和T坐标
            "attribute vec2 a_TexCoord;\n" +
            "varying vec2 v_TexCoord;\n" +
            "void main()\n" +
            "{\n" +
            "    v_TexCoord = a_TexCoord;\n" +
            "    gl_Position = u_Matrix * a_Position;\n" +
            "}";
    private static final String FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "varying vec2 v_TexCoord;\n" +
            "uniform sampler2D u_TextureUnit;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);\n" +
            "}";

    /**
     * 顶点坐标
     */
    private static final float[] POINT_DATA = {
            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f,
    };

    /**
     * 顶点坐标
     */
    private static final float[] POINT_DATA2 = {
            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, 0.5f,
            0.5f, -0.5f,
    };


    /**
     * 纹理坐标
     */
    private static final float[] TEX_VERTEX = {
            0.0f, 0f,
            0.0f, 1f,
            1.0f, 0f,
            1.0f, 1f,
    };


    private FloatBuffer mVertexData;
    private FloatBuffer mVertexData2;
    private FloatBuffer mTexData;
    private int mProgram;

    private int mTextureId;
    private Bitmap mBitmap;

    private int uTextureUnitLocation;
    private int uMatrix;
    private int aPosition;
    private int aTexCoord;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public FlowWaterRenderer (Context context){
        mContext = context;

        mBitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.map);
        mVertexData = BufferTransformUtil.floatBufferUtil(POINT_DATA);
        mVertexData2 = BufferTransformUtil.floatBufferUtil(POINT_DATA2);
        mTexData = BufferTransformUtil.floatBufferUtil(TEX_VERTEX);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        mProgram = ShaderUtils.createProgram(VERTEX_SHADER,FRAGMENT_SHADER);

        aPosition = GLES20.glGetAttribLocation(mProgram, "a_Position");
        aTexCoord = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        uMatrix = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        uTextureUnitLocation = GLES20.glGetUniformLocation(mProgram,"u_TextureUnit");

        // 开启纹理透明混合，这样才能绘制透明图片
        GLES20.glEnable(GL10.GL_BLEND);
        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        mTextureId = createTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        int w=mBitmap.getWidth();
        int h=mBitmap.getHeight();
        float sWH=w/(float)h;
        float sWidthHeight=width/(float)height;
        if(width>height){
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0,
                        -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0,
                        -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 7);
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -1, 1,
                        -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -1, 1,
                        -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // 设置相机视图
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation 转换矩阵（变换矩阵）
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aTexCoord);
        // 把纹理设置到0位置（着色器）
        GLES20.glUniform1i(uTextureUnitLocation, 0);

        float x = mTexData.get(0) + 0.01f;
        float y = mTexData.get(2) + 0.01f;
        float u = mTexData.get(4) + 0.01f;
        float v = mTexData.get(6) + 0.01f;



        mTexData.put(0,x);
        mTexData.put(2,y);
        mTexData.put(4,u);
        mTexData.put(6,v);

        GLES20.glVertexAttribPointer(aTexCoord,2, GLES20.GL_FLOAT,false,0,mTexData);

//        draw2();
        draw1();
//        draw2();
    }

    private float valX1;// 平移距离
    private float valX2 = -2.0f;

    private void draw1(){
//
//        float[] aa = new float[16];
//        valX1 += 0.01f;
//        if(valX1 >= 2.0f){
//            valX1 = -1.99f;
//        }
////        Log.e("DD","valX1 = "+valX1);
//        Matrix.translateM(aa,0,mMVPMatrix,0,valX1,0,0);


        GLES20.glUniformMatrix4fv(uMatrix,1,false,mMVPMatrix,0);


        GLES20.glVertexAttribPointer(aPosition,2, GLES20.GL_FLOAT,false,0,mVertexData);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }

    private void draw2(){

        float[] aa = new float[16];

        valX2 += 0.01f;
        if(valX2 >= 2.0f){
            valX2 = -1.99f;
        }
//        Log.d("DD","valX2 = "+valX2);
        Matrix.translateM(aa,0,mMVPMatrix,0,valX2,0,0);

        GLES20.glUniformMatrix4fv(uMatrix,1,false,aa,0);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aTexCoord);
        // 把纹理设置到0位置（着色器）
        GLES20.glUniform1i(uTextureUnitLocation, 0);

        GLES20.glVertexAttribPointer(aPosition,2, GLES20.GL_FLOAT,false,0,mVertexData2);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    }

    private int createTexture(){
        int[] texture=new int[1];
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
