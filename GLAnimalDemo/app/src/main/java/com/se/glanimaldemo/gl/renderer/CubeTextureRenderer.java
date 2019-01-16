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

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2019/1/12.
 */

public class CubeTextureRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = CubeTextureRenderer.class.getSimpleName();
    private Context mContext;

    // 旋转角度
    private float angleX,angleY;

    // 正方形顶点坐标
    private float s = 0.8f;

    private float[] coords = { -s,s,-s,s,s,s,s,s,-s, //023
            -s,s,-s,-s,s,s,s,s,s, //012
            -s,s,s,-s,-s,s,s,s,s, //152
            s,s,s,-s,-s,s,s,-s,s, //256
            s,s,s,s,-s,s,s,-s,-s, //267
            s,s,s,s,-s,-s,s,s,-s, //273
            s,s,-s,s,-s,-s,-s,-s,-s, //374
            s,s,-s,-s,-s,-s,-s,s,-s, //340
            -s,s,-s,-s,-s,-s,-s,s,s, //041
            -s,s,s,-s,-s,-s,-s,-s,s, //145
            -s,-s,s,-s,-s,-s,s,-s,s, //546
            s,-s,s,-s,-s,-s,s,-s,-s //647
     };

    //  纹理坐标点
    private float[] textureCoord = {
            0,1,1,0,1,1,
            0,1,0,0,1,0,
            0,1,0,0,1,1,
            1,1,0,0,1,0,
            0,1,0,0,1,0,
            0,1,1,0,1,1,
            0,1,0,0,1,0,
            0,1,1,0,1,1,
            0,1,0,0,1,1,
            1,1,0,0,1,0,
            0,1,0,0,1,1,
            1,1,0,0,1,0
    };

    public void setAngle(float x,float y){
        this.angleX = x;
        this.angleY = y;
    }

    //  纹理坐标点
//    private float[] textureCoord = {
//            0,1,1,0,1,1,
//            0,1,0,0,1,0,
//            0,1,0,0,1,1,
//            1,1,0,0,1,0,
//            0,1,0,0,1,0,
//            0,1,1,0,1,1,
//            0,1,0,0,1,0,
//            0,1,1,0,1,1,
//            0,1,0,0,1,1,
//            1,1,0,0,1,0,
//            0,1,0,0,1,1,
//            1,1,0,0,1,0
//    };

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
    private int program;
    private int texture1,texture2,texture3,texture4,texture5,texture6;

    private FloatBuffer m1stSB,m2ndSB,m3rdSB,m4thSB,m5stSB,m6ndSB;
    private float[] m1stInd,m2ndInd,m3rdInd,m4thInd,m5stInd,m6ndInd;

    private int vertexShader,fragmentShader;


    public CubeTextureRenderer(Context context){
        mContext = context;
        parseVertexInd();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f,1f,1f,1.0f);
        //打开深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //逆时针为正面
        GLES20.glFrontFace(GLES20.GL_CCW);
        //打开背面剪裁
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //背面剪裁
        GLES20.glCullFace(GLES20.GL_BACK);

        // 初始化缓冲数据
        vertexBuffer = BufferTransformUtil.floatBufferUtil(coords);
        textureCoordBuffer = BufferTransformUtil.floatBufferUtil(textureCoord);

        // 编译shader代码
        vertexShader = OneGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        fragmentShader = OneGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

//       for (int i = 0; i < 6; i++) {
//            texture = loadTexture(mContext, R.drawable.fengj,i);
//        }

        texture1 = loadTexture(mContext, R.drawable.water);
        texture2 = loadTexture(mContext, R.drawable.fengj);
        texture3 = loadTexture(mContext, R.drawable.sea);
        texture4 = loadTexture(mContext, R.drawable.pikachu);
        texture5 = loadTexture(mContext, R.drawable.tuzki);
        texture6 = loadTexture(mContext, R.drawable.ok);
        // 创建Program
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);
        GLES20.glLinkProgram(program);
    }

    private float[] projectMatrix = new float[16];
    private float[] viewMatrix = new float[16];

    private float[] mVMatrix = new float[16];

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视窗大小及位置
        GLES20.glViewport(0,0,width,height);

        float ratio = (float)width/height;
        //设置透视投影
        Matrix.frustumM(projectMatrix,0,-ratio,ratio,-1,1,3,30);
        //设置相机位置setLookAtM(float[] rm, int rmOffset,
        // float eyeX, float eyeY, float eyeZ, 摄像机的位置
        //float centerX, float centerY, float centerZ, 物体的位置
        // float upX, float upY,float upZ)  相机上方向量
        Matrix.setLookAtM(viewMatrix,0,5f,5f,10f,0f,0f,0f,0f,0f,1f);
        //计算变换矩阵
        Matrix.multiplyMM(mVMatrix,0,projectMatrix,0,viewMatrix,0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除深度缓冲与颜色缓冲
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);

        // 添加program到OpenGL ES环境中
        GLES20.glUseProgram(program);

        float[] ss = new float[16];
        float[] cc = new float[16];
        Matrix.rotateM(ss,0,mVMatrix,0,angleX,0,1,1);
        Matrix.rotateM(cc,0,ss,0,angleY,1,0,1);

        //矩阵变换
        mMatrixHandle = GLES20.glGetUniformLocation(program,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandle,1,false,ss,0);

        //设置纹理
        mTextureHandle = GLES20.glGetUniformLocation(program,"uTextureUnit");
        //激活纹理单元，GL_TEXTURE0代表纹理单元0，GL_TEXTURE1代表纹理单元1，以此类推。OpenGL使用纹理单元来表示被绘制的纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //把选定的纹理单元传给片段着色器中的u_TextureUnit，
        GLES20.glUniform1i(mTextureHandle, 0);

        //顶点坐标
        mPositionHandle = GLES20.glGetAttribLocation(program,"vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle,3,GLES20.GL_FLOAT,false,0,vertexBuffer);

        //纹理坐标
        mTextureCoordHandle = GLES20.glGetAttribLocation(program,"aTextureCoords");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle,2,GLES20.GL_FLOAT,false,0,textureCoordBuffer);

        // 绘制纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture1);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,9,GLES20.GL_FLOAT,m1stSB);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture2);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,9,GLES20.GL_FLOAT,m2ndSB);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture3);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,9,GLES20.GL_FLOAT,m3rdSB);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture4);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,9,GLES20.GL_FLOAT,m4thSB);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture5);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,9,GLES20.GL_FLOAT,m5stSB);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture6);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,9,GLES20.GL_FLOAT,m6ndSB);

        //绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,coords.length/3);

        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

    public int loadTexture(Context context, int resourceId) {

        //textureObjectIds用于存储OpenGL生成纹理对象的id
        final int[] textureObjectIds = new int[1];
        //加载纹理贴图
        GLES20.glGenTextures(1, textureObjectIds, 0);
        //判断是否生成成功
        if(textureObjectIds[0] == 0) {
            Log.e(TAG, "生产纹理对象失败！");
            return 0;
        }
        //加载纹理资源，解码成bitmap形式
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            Log.e(TAG, "Resource ID: " + resourceId + " decoded failed");
            //删除指定的纹理对象
            GLES20.glDeleteTextures(1,textureObjectIds, 0);
            return 0;
        }
        //第一个参数代表这是一个2D纹理，第二个参数就是OpenGL要绑定的纹理对象ID，也就是让OpenGL后面的纹理调用都使用此纹理对象
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);
        //设置纹理过滤参数，GL_TEXTURE_MIN_FILTER代表纹理缩写的情况，GL_LINEAR_MIPMAP_LINEAR代表缩小时使用三线性过滤的方式，至于过滤方式以后再详解
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        //GL_TEXTURE_MAG_FILTER代表纹理放大，GL_LINEAR代表双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //加载实际纹理图像数据到OpenGL ES的纹理对象中，这个函数是Android封装好的，可以直接加载bitmap格式，
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        //bitmap已经被加载到OpenGL了，所以bitmap可释放掉了，防止内存泄露
        bitmap.recycle();
        //我们为纹理生成MIP贴图，提高渲染性能，但是可占用较多的内存
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        //现在OpenGL已经完成了纹理的加载，不需要再绑定此纹理了，后面使用此纹理时通过纹理对象的ID即可
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        //返回OpenGL生成的纹理对象ID
        return textureObjectIds[0];
    }

    private void parseVertexInd(){
        m1stInd= Arrays.copyOfRange(coords,0,18);
        m1stSB=BufferTransformUtil.floatBufferUtil(m1stInd);
        m2ndInd=Arrays.copyOfRange(coords,18,36);
        m2ndSB=BufferTransformUtil.floatBufferUtil(m2ndInd);
        m3rdInd=Arrays.copyOfRange(coords,36,54);
        m3rdSB=BufferTransformUtil.floatBufferUtil(m3rdInd);
        m4thInd=Arrays.copyOfRange(coords,54,72);
        m4thSB=BufferTransformUtil.floatBufferUtil(m4thInd);
        m5stInd= Arrays.copyOfRange(coords,72,90);
        m5stSB=BufferTransformUtil.floatBufferUtil(m1stInd);
        m6ndInd=Arrays.copyOfRange(coords,90,108);
        m6ndSB=BufferTransformUtil.floatBufferUtil(m2ndInd);
    }

}
