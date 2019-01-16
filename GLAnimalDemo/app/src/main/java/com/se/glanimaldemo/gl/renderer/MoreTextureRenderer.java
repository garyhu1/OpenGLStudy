package com.se.glanimaldemo.gl.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.se.glanimaldemo.R;
import com.se.glanimaldemo.utils.BufferTransformUtil;
import com.se.glanimaldemo.utils.ShaderUtils;
import com.se.glanimaldemo.utils.TextureHelper;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2019/1/11.
 */

public class MoreTextureRenderer implements GLSurfaceView.Renderer {


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
            0.3f, 1f,
            0.3f, 1.5f,
            0.8f, 1.5f,
            0.8f, 1f,
    };

    /**
     * 顶点坐标2
     */
    private static final float[] POINT_DATA2 = {
            -0.3f, -0.6f,
            -0.3f, 0f,
            0.3f, 0f,
            0.3f, -0.6f,
    };

    /**
     * 纹理坐标
     */
    private static final float[] TEX_VERTEX = {
            0, 1,
            0, 0,
            1, 0,
            1, 1,
    };

    private int POSITION_COMPONENT_COUNT = 2;

    /**
     * 纹理坐标中每个点占的向量个数
     */
    private int TEX_VERTEX_COMPONENT_COUNT = 2;

    private int uTextureUnitLocation;
    private int uMatrix;
    private int aPosition;
    private int aTexCoord;

    private FloatBuffer mVertexData;
    private FloatBuffer mVertexData2;
    private FloatBuffer mTexVertexBuffer;

    private int mTexture;
    private TextureHelper.TextureBean mTextureBean,mTextureBean2;
    private Context mContext;
    private int mProgram;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private Bitmap mBitmap;

    public MoreTextureRenderer (Context context){
        mContext = context;

        mVertexData = BufferTransformUtil.floatBufferUtil(POINT_DATA);
        mVertexData2 = BufferTransformUtil.floatBufferUtil(POINT_DATA2);
        mTexVertexBuffer = BufferTransformUtil.floatBufferUtil(TEX_VERTEX);
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

//        mTexture = createTexture();
        mTextureBean = TextureHelper.loadTexture(mContext, R.drawable.ok);
        mTextureBean2 = TextureHelper.loadTexture(mContext, R.drawable.pikachu);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        float sWidthHeight=width > height ? width/(float)height : height/(float)width;
        if(width>height){
            Matrix.orthoM(mProjectMatrix, 0,
                    -sWidthHeight,sWidthHeight, -1,1, 3, 7);
        }else{
            Matrix.orthoM(mProjectMatrix, 0, -1, 1,
                    -sWidthHeight, sWidthHeight,3, 7);
        }

        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);

        // 设置相机视图
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation 转换矩阵（变换矩阵）
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        // 设置纹理坐标
        GLES20.glEnableVertexAttribArray(aTexCoord);
        GLES20.glVertexAttribPointer(aTexCoord,2,GLES20.GL_FLOAT,false,0,mTexVertexBuffer);

        // 设置当前活动的纹理单元为纹理单元0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        draw1();

        draw2();
    }

    private void draw1(){

        float[] aa = new float[16];
        Matrix.rotateM(aa,0,mMVPMatrix,0,60,1.0f,0,0);

        GLES20.glUniformMatrix4fv(uMatrix,1,false,aa,0);

//        GLES20.glUniformMatrix4fv(uMatrix,1,false,mMVPMatrix,0);
        // 设置顶点坐标
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition,2,GLES20.GL_FLOAT,false,0,mVertexData);

        // 将纹理ID绑定到当前活动的纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBean.getTextureId());
        // 将纹理单元传递片段着色器的u_TextureUnit
        GLES20.glUniform1i(uTextureUnitLocation, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, POINT_DATA.length / POSITION_COMPONENT_COUNT);
    }


    private float ratio = 0.5f;
    private boolean insert = true;

    private void draw2(){
        // Set the camera position (View matrix)
        // 设置相机视图
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3,
                0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation 转换矩阵（变换矩阵）
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

        float[] aa = new float[16];
        if(ratio >= 1.0f){
            ratio = 1.0f;
            insert = false;
        }else if(ratio <= 0.5f){
            ratio = 0.5f;
            insert = true;
        }
        Matrix.scaleM(aa,0,mMVPMatrix,0,ratio,ratio,1);

        if(insert){
            ratio += 0.01f;
        }else {
            ratio -= 0.01f;
        }

        GLES20.glUniformMatrix4fv(uMatrix,1,false,aa,0);
        // 设置顶点坐标
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition,2,GLES20.GL_FLOAT,false,0,mVertexData2);

        // 将纹理ID绑定到当前活动的纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBean2.getTextureId());
        // 将纹理单元传递片段着色器的u_TextureUnit
        GLES20.glUniform1i(uTextureUnitLocation, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, POINT_DATA.length / POSITION_COMPONENT_COUNT);
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
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return texture[0];
        }
        return 0;
    }

}
