package com.se.glanimaldemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.se.glanimaldemo.gl.CubeTextureSurfaceView;

import static com.se.glanimaldemo.activity.CubeActivity.ROTATE_FACTOR;

/**
 * Created by yb on 2019/1/12.
 */

public class CubeTextureActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private GestureDetector gestureDetector;
    private CubeTextureSurfaceView surfaceView;
    private float anglex,angley;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surfaceView = new CubeTextureSurfaceView(this);

        setContentView(surfaceView);

        gestureDetector = new GestureDetector(this, this);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

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

        surfaceView.setAngle(anglex,angley);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将该Activity上的触碰事件交给GestureDetector处理
        return gestureDetector.onTouchEvent(event);
    }
}
