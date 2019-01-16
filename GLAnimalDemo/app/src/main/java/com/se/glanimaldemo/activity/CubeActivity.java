package com.se.glanimaldemo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.se.glanimaldemo.R;
import com.se.glanimaldemo.gl.CubeSurfaceView;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yb on 2019/1/12.
 */

public class CubeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";
    GestureDetector gestureDetector;
    // 定义旋转角度
    private float anglex = 0f;
    private float angley = 0f;
    static final float ROTATE_FACTOR = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
//        glSurfaceView.setEGLContextClientVersion(2);
        MyRenderOne myRender = new MyRenderOne(this);
        glSurfaceView.setRenderer(myRender);
        setContentView(glSurfaceView);
        gestureDetector = new GestureDetector(this, this);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {  }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {  }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        velocityX = velocityX > 4000 ? 4000 : velocityX;
        velocityX = velocityX < -4000 ? -4000 : velocityX;
        velocityY = velocityY > 4000 ? 4000 : velocityY;
        velocityY = velocityY < -4000 ? -4000 : velocityY;
        // 根据横向上的速度计算沿Y轴旋转的角度
        angley += velocityX * ROTATE_FACTOR / 4000;
        // 根据纵向上的速度计算沿X轴旋转的角度
        anglex += velocityY * ROTATE_FACTOR / 4000;
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将该Activity上的触碰事件交给GestureDetector处理
        return gestureDetector.onTouchEvent(event);
    }

    public class MyRenderOne implements GLSurfaceView.Renderer {
        // 立方体的顶点座标（一共是36个顶点，组成12个三角形）
        private float[] cubeVertices = { -0.4f, -0.4f, -0.4f, -0.4f, 0.4f,
                -0.4f, 0.4f, 0.4f, -0.4f, 0.4f, 0.4f, -0.4f, 0.4f, -0.4f, -0.4f,
                -0.4f, -0.4f, -0.4f, -0.4f, -0.4f, 0.4f, 0.4f, -0.4f, 0.4f, 0.4f,
                0.4f, 0.4f, 0.4f, 0.4f, 0.4f, -0.4f, 0.4f, 0.4f, -0.4f, -0.4f,
                0.4f, -0.4f, -0.4f, -0.4f, 0.4f, -0.4f, -0.4f, 0.4f, -0.4f, 0.4f,
                0.4f, -0.4f, 0.4f, -0.4f, -0.4f, 0.4f, -0.4f, -0.4f, -0.4f, 0.4f,
                -0.4f, -0.4f, 0.4f, 0.4f, -0.4f, 0.4f, 0.4f, 0.4f, 0.4f, 0.4f,
                0.4f, 0.4f, -0.4f, 0.4f, 0.4f, -0.4f, -0.4f, 0.4f, 0.4f, -0.4f,
                -0.4f, 0.4f, -0.4f, -0.4f, 0.4f, 0.4f, -0.4f, 0.4f, 0.4f, 0.4f,
                0.4f, 0.4f, 0.4f, 0.4f, -0.4f, -0.4f, 0.4f, -0.4f, -0.4f, -0.4f,
                -0.4f, -0.4f, -0.4f, 0.4f, -0.4f, -0.4f, 0.4f, -0.4f, 0.4f, 0.4f,
                -0.4f, 0.4f, -0.4f, };
        // 定义立方体所需要的6个面（一共是12个三角形所需的顶点）
        private byte[] cubeFacets = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, };
        // 定义纹理贴图的72个座标数据
        private float[] cubeTextures = { 1.0000f, 1.0000f, 1.0000f, 0.0000f,
                0.0000f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f, 1.0000f,
                1.0000f, 0.0000f, 1.0000f, 1.0000f, 1.0000f, 1.0000f, 0.0000f,
                1.0000f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f, 0.0000f,
                1.0000f, 1.0000f, 1.0000f, 1.0000f, 0.0000f, 1.0000f, 0.0000f,
                0.0000f, 0.0000f, 0.0000f, 1.0000f, 0.0000f, 1.0000f, 1.0000f,
                1.0000f, 1.0000f, 0.0000f, 1.0000f, 0.0000f, 0.0000f, 0.0000f,
                0.0000f, 1.0000f, 0.0000f, 1.0000f, 1.0000f, 1.0000f, 1.0000f,
                0.0000f, 1.0000f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f,
                0.0000f, 1.0000f, 1.0000f, 1.0000f, 1.0000f, 0.0000f, 1.0000f,
                0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f };
        private Context context;
        private int texture;
        public MyRenderOne(Context con)
        {
            this.context = con;
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glDisable(GL10.GL_DITHER); //关闭抗抖动
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); //设置系统对透视进行修正
            gl.glClearColor(0, 0, 0, 0); //黑色背景
            gl.glShadeModel(GL10.GL_SMOOTH); //设置平滑模式
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL); //设置深度测试(opengl es会跟踪z轴的深度，以避免后面的图像挡住前面的图像)的类型
            gl.glEnable(GL10.GL_TEXTURE_2D); //开启2D纹理贴图
            loadTexture(gl);
        }
        private void loadTexture(GL10 gl) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sea);
                int textures[] = new int[1];
                gl.glGenTextures(1,textures,0); //创建纹理
                texture = textures[0];
                gl.glBindTexture(GL10.GL_TEXTURE_2D, texture); //将纹理绑定到目标
                // 设置纹理被缩小（距离视点很远时被缩小）时候的滤波方式
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
                // 设置纹理被放大（距离视点很近时被放大）时候的滤波方式
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
                // 设置在横向、纵向上都是平铺纹理
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
                gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_T,GL10.GL_REPEAT);
                // 加载位图生成纹理
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            }  finally {
                if (bitmap != null){
                    bitmap.recycle();
                }
            }
        }
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i(TAG, "onSurfaceChanged enter...");
            gl.glViewport(0, 0, width, height); //设置视窗的大小及位置
            gl.glMatrixMode(GL10.GL_PROJECTION); //设置为投影矩阵
            gl.glLoadIdentity(); // 初始化单位矩阵
            float ratio = (float) width / height;
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10); // 设置透视视窗的空间大小。
        }
        @Override
        public void onDrawFrame(GL10 gl) {
            // 清除屏幕缓存和深度缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            //开启顶点设置和纹理设置功能
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glMatrixMode(GL10.GL_MODELVIEW); //设置为模型矩阵
            Log.i(TAG, "onDrawFrame enter...");
            gl.glLoadIdentity();
            gl.glTranslatef(0f, 0.0f, -2.0f); // 把绘图中心移入屏幕2个单位
            gl.glRotatef(angley, 0, 1, 0); // 旋转图形
            gl.glRotatef(anglex, 1, 0, 0);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufferUtil(cubeVertices)); //设置立方体的顶点
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufferUtil(cubeTextures)); //设置纹理顶点
            //gl.glBindTexture(GL10.GL_TEXTURE_2D, texture); //将纹理绑定到目标(执行纹理贴图)
            //绘制立方体
            gl.glDrawElements(GL10.GL_TRIANGLES, cubeFacets.length, GL10.GL_UNSIGNED_BYTE, bufferUtil2(cubeFacets));
            gl.glFinish(); //绘制完成
            // 禁用顶点和纹理设置
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        /*
         * OpenGL 是一个非常底层的画图接口，它所使用的缓冲区存储结构是和我们的 java 程序中不相同的。
         * Java 是大端字节序(BigEdian)，而 OpenGL 所需要的数据是小端字节序(LittleEdian)。
         * 所以，我们在将 Java 的缓冲区转化为 OpenGL 可用的缓冲区时需要作一些工作。建立buff的方法如下
         * */
        public Buffer bufferUtil(float []arr){
            FloatBuffer mBuffer ;
            //先初始化buffer,数组的长度*4,因为一个float占4个字节
            ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
            //数组排列用nativeOrder
            qbb.order(ByteOrder.nativeOrder());
            mBuffer = qbb.asFloatBuffer();
            mBuffer.put(arr);
            mBuffer.position(0);
            return mBuffer;
        }
        public Buffer bufferUtil1(int []arr){
            IntBuffer mBuffer ;
            //先初始化buffer,数组的长度*4,因为一个int占4个字节
            ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
            //数组排列用nativeOrder
            qbb.order(ByteOrder.nativeOrder());
            mBuffer = qbb.asIntBuffer();
            mBuffer.put(arr);
            mBuffer.position(0);
            return mBuffer;
        }
        public Buffer bufferUtil2(byte []arr){
            //先初始化buffer
            ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length);
            //数组排列用nativeOrder
            qbb.order(ByteOrder.nativeOrder());
            qbb.put(arr);
            qbb.position(0);
            return qbb;
        }
    }
}
