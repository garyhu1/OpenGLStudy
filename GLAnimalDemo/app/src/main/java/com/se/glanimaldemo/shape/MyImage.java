package com.se.glanimaldemo.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.se.glanimaldemo.gl.renderer.OneGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by yb on 2018/12/28.
 * 绘制一个图片
 */

public class MyImage {

    private FloatBuffer bPos;
    private FloatBuffer bCoord;

    private int glHMatrix;
    private int glHPosition;
    private int glHTexture;
    private int glHCoordinate;
    private int mProgram;

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

    private int textureId;

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
            "uniform mat4 vMatrix;"+
            "varying vec2 aCoordinate;"+
            "attribute vec2 vCoordinate;"+
            "void main() {" +
            "  gl_Position = vMatrix * vPosition;"+
            "  aCoordinate = vCoordinate;"+
            "}";
    private String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D vTexture;"+
            "varying vec2 aCoordinate;" +
            "void main() {" +
            "  gl_FragColor = texture2D(vTexture,aCoordinate);" +
            "}";


    private String vertex = "filter/half_color_vertex.sh";
    private String fragment = "filter/half_color_fragment.sh";

    public MyImage(Context context) {

        ByteBuffer bb=ByteBuffer.allocateDirect(sPos.length*4);
        bb.order(ByteOrder.nativeOrder());
        bPos=bb.asFloatBuffer();
        bPos.put(sPos);
        bPos.position(0);
        ByteBuffer cc=ByteBuffer.allocateDirect(sCoord.length*4);
        cc.order(ByteOrder.nativeOrder());
        bCoord=cc.asFloatBuffer();
        bCoord.put(sCoord);
        bCoord.position(0);

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
//        mProgram = ShaderUtils.createProgram(context.getResources(),vertex,fragment);

    }

    public void draw(float[] mvpMatrix, Bitmap bitmap){
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(glHMatrix,1,false,mvpMatrix,0);
        GLES20.glEnableVertexAttribArray(glHPosition);
        GLES20.glEnableVertexAttribArray(glHCoordinate);
        GLES20.glUniform1i(glHTexture, 0);
        textureId=createTexture(bitmap);
        GLES20.glVertexAttribPointer(glHPosition,2, GLES20.GL_FLOAT,false,0,bPos);
        GLES20.glVertexAttribPointer(glHCoordinate,2, GLES20.GL_FLOAT,false,0,bCoord);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(glHPosition);
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
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
