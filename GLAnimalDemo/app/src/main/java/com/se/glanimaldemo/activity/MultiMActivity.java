package com.se.glanimaldemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.se.glanimaldemo.R;
import com.se.glanimaldemo.gl.renderer.OneGLRenderer;
import com.se.glanimaldemo.utils.BufferTransformUtil;
import com.se.glanimaldemo.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2019/1/14.
 */

public class MultiMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView surfaceView = new GLSurfaceView(this);
        MyRenderer renderer = new MyRenderer();
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(renderer);
        setContentView(surfaceView);

    }

    public class MyRenderer implements GLSurfaceView.Renderer{

        // 正方形顶点坐标
        final float cubePositions[] = {
                -1.0f,1.0f,1.0f,    //正面左上0
                -1.0f,-1.0f,1.0f,   //正面左下1
                1.0f,-1.0f,1.0f,    //正面右下2
                1.0f,1.0f,1.0f,     //正面右上3
                -1.0f,1.0f,-1.0f,    //反面左上4
                -1.0f,-1.0f,-1.0f,   //反面左下5
                1.0f,-1.0f,-1.0f,    //反面右下6
                1.0f,1.0f,-1.0f,     //反面右上7
        };

        //  纹理坐标点
        private float[] textureCoord = {
                0.0f,1.0f,1.0f,
                0.0f,0.0f,1.0f,
                1.0f,0.0f,1.0f,
                1.0f,1.0f,1.0f
        };

        final short index[]={
                6,7,4,6,4,5,    //后面
                6,3,7,6,2,3,    //右面
                6,5,1,6,1,2,    //下面
                0,3,2,0,2,1,    //正面
                0,1,5,0,5,4,    //左面
                0,7,3,0,4,7,    //上面
        };

//        /**
//         * 顶点坐标
//         */
//        private  float[] cubePositions = {
//                -0.5f, 0.5f,
//                -0.5f, -0.5f,
//                0.5f, 0.5f,
//                0.5f, -0.5f,
//        };
//
//
//        /**
//         * 纹理坐标
//         */
//        private  float[] textureCoord = {
//                0.0f, 0f,
//                0.0f, 1f,
//                1.0f, 0f,
//                1.0f, 1f,
//        };

        // 顶点shader
        private final String vertexShaderCode =
                "attribute vec4 vPosition;"+            //顶点坐标
                        "uniform mat4 vMatrix;"+                //变换矩阵
                        "attribute vec2 aTextureCoords;"+       //纹理坐标，传入
                        "varying vec2 vTextureCoords;"+         //纹理坐标，传递到片断shader
                        "void main(){"+                         //每个shader中必须有一个main函数
                        "gl_Position = vMatrix*vPosition;"+     //坐标变换赋值
                        "vTextureCoords = aTextureCoords;"+     //传递纹理坐标
                        "}";

        // 片断shader
        private final String fragmentShaderCode =
                "precision mediump float;"+                                //精度设置为mediump
                        "uniform sampler2D uTextureUnit;"+                         //纹理单元
                        "varying vec2 vTextureCoords;"+                            //纹理坐标，由顶点shader传递过来
                        "void main(){"+                                            //每个shader中必须有一个main函数
                        "gl_FragColor = texture2D(uTextureUnit, vTextureCoords);"+ //赋值2D纹理
                        "}";

        private int mMatrixHandle;
        private int mPositionHandle;
        private int mTextureHandle;
        private int mTextureCoordHandle;

        private FloatBuffer vertexBuffer;
        private FloatBuffer textureCoordBuffer;
        private ShortBuffer indexBuffer;
        private int mProgram;

        private int texture1,texture2,texture3,texture4,texture5,texture6;

        private float[] mProjectMatrix = new float[16];
        private float[] mViewMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        private Bitmap[] mBitmap = new Bitmap[6];
        private int[] textures;

        public MyRenderer(){
            mBitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.sea);
            mBitmap[1] = BitmapFactory.decodeResource(getResources(), R.drawable.water);
            mBitmap[2] = BitmapFactory.decodeResource(getResources(), R.drawable.pikachu);
            mBitmap[3] = BitmapFactory.decodeResource(getResources(), R.drawable.tuzki);
            mBitmap[4] = BitmapFactory.decodeResource(getResources(), R.drawable.ok);
            mBitmap[5] = BitmapFactory.decodeResource(getResources(), R.drawable.fengj);

            vertexBuffer = BufferTransformUtil.floatBufferUtil(cubePositions);
            textureCoordBuffer = BufferTransformUtil.floatBufferUtil(textureCoord);

            ByteBuffer cc= ByteBuffer.allocateDirect(index.length*2);
            cc.order(ByteOrder.nativeOrder());
            indexBuffer=cc.asShortBuffer();
            indexBuffer.put(index);
            indexBuffer.position(0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.2f,0.3f,0.3f,1.0f);

            mProgram = ShaderUtils.createProgram(vertexShaderCode,fragmentShaderCode);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
            mMatrixHandle = GLES20.glGetUniformLocation(mProgram,"vMatrix");
            mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram,"aTextureCoords");
            mTextureHandle = GLES20.glGetUniformLocation(mProgram,"uTextureUnit");

            // 开启纹理透明混合，这样才能绘制透明图片
            GLES20.glEnable(GL10.GL_BLEND);
            GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

            // 添加纹理图片
            loadTexture();
//            texture1 = createTexture(mBitmap[0]);
//            texture2 = createTexture(mBitmap[1]);
//            texture3 = createTexture(mBitmap[2]);
//            texture4 = createTexture(mBitmap[3]);
//            texture5 = createTexture(mBitmap[4]);
//            texture6 = createTexture(mBitmap[5]);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0,0,width,height);

            //计算宽高比
            float ratio=(float)width/height;
            //设置透视投影
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
            //设置相机位置
            Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            //计算变换矩阵
            Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

            // 将程序添加到OpenGL ES环境
            GLES20.glUseProgram(mProgram);

//            // 设置相机视图
//            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3,
//                    0f, 0f, 0f, 0f, 1.0f, 0.0f);
//            // Calculate the projection and view transformation 转换矩阵（变换矩阵）
//            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
            // 把纹理设置到0位置（片元着色器）我们使用glUniform1i设置uniform采样器的位置值，或者说纹理单元
            GLES20.glUniform1i(mTextureHandle, 0);

            // 设置顶点值
            GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,
                    false,0,vertexBuffer);
            GLES20.glVertexAttribPointer(mTextureCoordHandle,3,GLES20.GL_FLOAT,
                    false,0,textureCoordBuffer);

            // 正面
            float[] one = new float[16];
            Matrix.translateM(one,0,mMVPMatrix,0,0.0f,0.0f,-0.5f);
            GLES20.glUniformMatrix4fv(mMatrixHandle,1,false,mMVPMatrix,0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[4]);
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
            //索引法绘制正方体
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length, GLES20.GL_UNSIGNED_SHORT,indexBuffer);
        }

        public void loadTexture(){
            IntBuffer textBuffer=IntBuffer.allocate(6);
            GLES20.glGenTextures(6,textBuffer);
            textures=textBuffer.array();

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap[0],0);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[1]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap[1],0);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[2]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap[2],0);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[3]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap[3],0);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[4]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap[4],0);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[5]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,mBitmap[5],0);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }

        private int createTexture(Bitmap bitmap){
            int[] texture=new int[1];
            if(bitmap!=null&&!bitmap.isRecycled()){
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
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                return texture[0];
            }
            return 0;
        }
    }

}
