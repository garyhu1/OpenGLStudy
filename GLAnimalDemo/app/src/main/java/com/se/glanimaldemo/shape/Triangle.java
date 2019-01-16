package com.se.glanimaldemo.shape;

import android.opengl.GLES20;
import android.util.Log;

import com.se.glanimaldemo.gl.renderer.OneGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by yb on 2018/12/27.
 * 三角形
 */

public class Triangle {

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private final int mProgram;

    static float triangleCoords[] = {
            // in counterclockwise order:
            0.0f, 0.5f, 0.0f,// top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
//    float color[] = { 255, 0, 0, 1.0f };

    float color[] = {
            1.0f, 0f, 0f, 1.0f ,
            0f, 1.0f, 0f, 1.0f ,
            0f, 0f, 1.0f, 1.0f
    };

//    private final String vertexShaderCode = "attribute vec4 vPosition;" +
//            "void main() {" +
//            "  gl_Position = vPosition;" +
//            "}";

//    private final String vertexShaderCode =
//            // This matrix member variable provides a hook to manipulate
//            // the coordinates of the objects that use this vertex shader
//            "uniform mat4 uMVPMatrix;" +
//            "attribute vec4 vPosition;" +
//            "void main() {" +
//            // the matrix must be included as a modifier of gl_Position
//            // Note that the uMVPMatrix factor *must be first* in order
//            // for the matrix multiplication product to be correct.
//            "  gl_Position = uMVPMatrix * vPosition;" +
//            "}";

//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//            "uniform vec4 vColor;" +
//            "void main() {" +
//            "  gl_FragColor = vColor;" +
//            "}";

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 uMVPMatrix;"+
            "varying  vec4 vColor;"+
            "attribute vec4 aColor;"+
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  vColor=aColor;"+
            "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;

    private int mPositionHandle;
    private int mColorHandle;
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex


    //这里有一点需要注意，因为着色器的代码执行是很昂贵滴，所以避免多次执行，
    // 需要我们一般将执行代码的逻辑写带图形类的构造方法中
    public Triangle() {
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // 数组排列用nativeOrder
        bb.order(ByteOrder.nativeOrder());
        // 从ByteBuffer创建一个浮点缓冲区
        vertexBuffer = bb.asFloatBuffer();
        // 将坐标添加到FloatBuffer
        vertexBuffer.put(triangleCoords);
        // 设置缓冲区来读取第一个坐标
        vertexBuffer.position(0);

        int vertexShader = OneGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = OneGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        // 创建空的OpenGL ES程序
        mProgram = GLES20.glCreateProgram();
        // 添加顶点着色器到程序中
        GLES20.glAttachShader(mProgram, vertexShader);
        // 添加片段着色器到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 创建OpenGL ES程序可执行文件
        GLES20.glLinkProgram(mProgram);

        // 验证链接状态
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram,GLES20.GL_LINK_STATUS,linkStatus,0);
        Log.e("GARYHU","status = "+linkStatus[0]);
        // 为1成功，0不成功
        if(linkStatus[0] == 0){
            GLES20.glDeleteProgram(mProgram);
            Log.e("GARYHU","error = "+GLES20.glGetProgramInfoLog(mProgram));
        }

        // 转换数据
        ByteBuffer dd = ByteBuffer.allocateDirect( color.length * 4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);


    }

    public void draw(float[] mvpMatrix) {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);
        // 获取顶点着色器的位置的句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用三角形顶点位置的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
//        // 获取片段着色器的颜色的句柄
//        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//        // 设置绘制三角形的颜色
//        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        //设置绘制三角形的颜色
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4, GLES20.GL_FLOAT,false, 0,colorBuffer);

        // 得到形状的变换矩阵的句柄
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 将投影和视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


}
