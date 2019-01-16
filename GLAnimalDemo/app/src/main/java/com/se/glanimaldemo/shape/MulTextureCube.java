package com.se.glanimaldemo.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.se.glanimaldemo.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2019/1/14.
 */

public class MulTextureCube {

    private int[] textures;
    private Bitmap[] mBitmap=new Bitmap[6];

    private FloatBuffer vertexBuffer;
    private FloatBuffer texBuffer;

    private float[] vertices={
            -1.0f, -1.0f, 0.0f,  // 0. left-bottom-front
            1.0f, -1.0f, 0.0f,  // 1. right-bottom-front
            -1.0f,  1.0f, 0.0f,  // 2. left-top-front
            1.0f,  1.0f, 0.0f   // 3. right-top-front
    };

    float[] texCoords={
            0.0f, 1.0f,  // A. left-bottom (NEW)
            1.0f, 1.0f,  // B. right-bottom (NEW)
            0.0f, 0.0f,  // C. left-top (NEW)
            1.0f, 0.0f   // D. right-top (NEW)
    };

    int[] textureIDs=new int[1];

    public MulTextureCube(Context context){

        mBitmap[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.sea);
        mBitmap[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.water);
        mBitmap[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.pikachu);
        mBitmap[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tuzki);
        mBitmap[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ok);
        mBitmap[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.fengj);

        ByteBuffer vbb=ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer=vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer tbb=ByteBuffer.allocateDirect(texCoords.length*4);
        tbb.order(ByteOrder.nativeOrder());
        texBuffer=tbb.asFloatBuffer();
        texBuffer.put(texCoords);
        texBuffer.position(0);

    }

    public void loadTexture(GL10 gl){

        IntBuffer textBuffer=IntBuffer.allocate(6);
        gl.glGenTextures(6,textBuffer);
        textures=textBuffer.array();

        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,mBitmap[0],0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[1]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,mBitmap[1],0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[2]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,mBitmap[2],0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[3]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,mBitmap[3],0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[4]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,mBitmap[4],0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[5]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,mBitmap[5],0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);

    }

    public void draw(GL10 gl){

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,vertexBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2,GL10.GL_FLOAT,0,texBuffer);

        //front
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[0]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);
        gl.glPopMatrix();

        //left
        gl.glPushMatrix();
        gl.glRotatef(270.0f,0.0f,1.0f,0.0f);
        gl.glTranslatef(0.0f,0.0f,1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[1]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);
        gl.glPopMatrix();

        //back
        gl.glPushMatrix();
        gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[2]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();

        // right
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[3]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();

        // top
        gl.glPushMatrix();
        gl.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[4]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();

        // bottom
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[5]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);

    }
}
