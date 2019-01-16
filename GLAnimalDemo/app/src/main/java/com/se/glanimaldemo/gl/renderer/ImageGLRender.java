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

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2018/12/28.
 */

public class ImageGLRender implements GLSurfaceView.Renderer {

    private Bitmap mBitmap;
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    // 定义一个旋转矩阵
    private float[] mRotationMatrix = new float[16];

    private FloatBuffer bPos;
    private FloatBuffer bCoord;
    private FloatBuffer colorBuffer;

    private int glHMatrix;
    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int mColor;
    private int mProgram;

    private int textureId;
    private Context mContext;

    // number of coordinates per vertex in this array
    private final float[] sPos={
            -1.0f,1.0f,
            -1.0f,-1.0f,
            1.0f,1.0f,
            1.0f,-1.0f
    };

    private final float[] sCoord={
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };

    private String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;"+
            "varying  vec4 vColor;"+
            "attribute vec4 aColor;"+
            "varying vec2 aCoordinate;"+
            "attribute vec2 vCoordinate;"+
            "void main() {" +
            "  gl_Position = vMatrix * vPosition;"+
            "  vColor = aColor;"+
            "  aCoordinate = vCoordinate;"+
            "}";
    private String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D vTexture;"+
            "varying vec2 aCoordinate;" +
            "varying vec4 vColor;"+
            "void main() {" +
            "  gl_FragColor = texture2D(vTexture,aCoordinate) * vColor;" +// 在纹理上添加了颜色混合
            "}";

    /*
    * 得到两个混合的纹理，
    * 第三个参数的含义： 0.0表示返回第一个纹理，1.0返回第二个纹理，
    *                 0.2表示返回80%的第一个纹理，20%的第二个纹理
    */
    // gl_FragColor = mix(texture(ourTexture1, TexCoord), texture(ourTexture2, TexCoord), 0.2);

    float color[] = {
            1.0f, 0f, 0f, 1.0f ,
            0f, 1.0f, 0f, 1.0f ,
            0f, 0f, 1.0f, 1.0f,
            0f, 1.0f, 1.0f, 1.0f,
    };


    public ImageGLRender(Context context){
        mContext = context;

        mBitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.fengj);

        bPos = BufferTransformUtil.floatBufferUtil(sPos);
        bCoord = BufferTransformUtil.floatBufferUtil(sCoord);
        colorBuffer = BufferTransformUtil.floatBufferUtil(color);

//        ByteBuffer bb=ByteBuffer.allocateDirect(sPos.length*4);
//        bb.order(ByteOrder.nativeOrder());
//        bPos=bb.asFloatBuffer();
//        bPos.put(sPos);
//        bPos.position(0);
//        ByteBuffer cc=ByteBuffer.allocateDirect(sCoord.length*4);
//        cc.order(ByteOrder.nativeOrder());
//        bCoord=cc.asFloatBuffer();
//        bCoord.put(sCoord);
//        bCoord.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        int vertexShader = OneGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = OneGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);


        mProgram = GLES20.glCreateProgram();
        // 把顶点着色器和片段着色器添加到程序中
        GLES20.glAttachShader(mProgram,vertexShader);
        GLES20.glAttachShader(mProgram,fragmentShader);

        // GLES20链接可执行程序
        GLES20.glLinkProgram(mProgram);

        glHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
        glHPosition=GLES20.glGetAttribLocation(mProgram,"vPosition");
        glHCoordinate=GLES20.glGetAttribLocation(mProgram,"vCoordinate");
        glHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
        mColor = GLES20.glGetAttribLocation(mProgram,"aColor");
//        mProgram = ShaderUtils.createProgram(context.getResources(),vertex,fragment);

        textureId=createTexture();
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

    private float ratio = 0.5f;
    private boolean insert = true;

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);

//        Matrix.setIdentityM(mRotationMatrix,0);

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

        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(glHMatrix,1,false,aa,0);
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        GLES20.glEnableVertexAttribArray(mColor);
        // 激活纹理设置到0位置（着色器）就一个纹理的时候不用激活，默认为0的位置
        GLES20.glUniform1i(glHTexture, 0);

        GLES20.glVertexAttribPointer(glHPosition,2, GLES20.GL_FLOAT,false,0,bPos);
        GLES20.glVertexAttribPointer(glHCoordinate,2, GLES20.GL_FLOAT,false,0,bCoord);
        GLES20.glVertexAttribPointer(mColor,4,GLES20.GL_FLOAT,false,0,colorBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(glHPosition);

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
