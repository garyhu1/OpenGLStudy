package com.se.glanimaldemo.shape;

import android.opengl.GLES20;

import com.se.glanimaldemo.gl.renderer.OneGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by yb on 2018/12/27.
 * 正方形
 */

public class Square {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private FloatBuffer colorBuffer;

    private int mProgram;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.5f, 0.5f, 0.0f, // top left
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f, // bottom right
            0.5f, 0.5f, 0.0f // top right
    };
    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

//    float color[] = { 255, 0, 0, 1.0f };

    float color[] = {
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f
    };

//    private String vertexShaderCode =
//            "attribute vec4 vPosition;" +
//            "uniform mat4 uMVPMatrix;"+
//            "void main() {" +
//            "  gl_Position = uMVPMatrix * vPosition;"+
//            "}";
//    private String fragmentShaderCode =
//            "precision mediump float;" +
//            "uniform vec4 vColor;" +
//            "void main() {" +
//            "  gl_FragColor = vColor;" +
//            "}";

    private String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "uniform mat4 uMVPMatrix;"+
            "varying vec4 vColor;"+
            "attribute vec4 aColor;"+
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;"+
            "  vColor = aColor;"+
            "}";
    private String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    public Square() {
        // 初始化ByteBuffer，长度为arr数组的长度*4，因为一个float占4个字节
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
        // 初始化ByteBuffer，长度为arr数组的长度*2，因为一个short占2个字节
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = OneGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = OneGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

        // 创建空GLES20程序
        mProgram = GLES20.glCreateProgram();
        // 把顶点着色器和片段着色器添加到程序中
        GLES20.glAttachShader(mProgram,vertexShader);
        GLES20.glAttachShader(mProgram,fragmentShader);

        // GLES20链接可执行程序
        GLES20.glLinkProgram(mProgram);

        // 颜色数据转换
        ByteBuffer cc = ByteBuffer.allocateDirect(color.length*4);
        cc.order(ByteOrder.nativeOrder());
        colorBuffer = cc.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

    }

    public void draw(float[] mvpMatrix){
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);
        //获取顶点着色器的句柄
        int positionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
        // 启用正方形顶点位置句柄
        GLES20.glEnableVertexAttribArray(positionHandle);
        // 准备正方形的顶点数据
        GLES20.glVertexAttribPointer(positionHandle,COORDS_PER_VERTEX,GLES20.GL_FLOAT,
                false,vertexStride,vertexBuffer);

//        // 获取片段着色器的句柄
//        int colorHandle = GLES20.glGetUniformLocation(mProgram,"vColor");
//        // 设置绘制正方形的颜色
//        GLES20.glUniform4fv(colorHandle,1,color,0);

        int colorHandle = GLES20.glGetAttribLocation(mProgram,"aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle,4,GLES20.GL_FLOAT,false,0,colorBuffer);

        // 得到形状的变换矩阵的句柄
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 将投影和视图转换传递给着色器
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0,vertexCount);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
