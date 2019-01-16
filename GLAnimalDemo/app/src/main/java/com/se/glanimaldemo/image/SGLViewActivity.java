/*
 *
 * SGLViewActivity.java
 * 
 * Created by Wuwang on 2016/10/15
 */
package com.se.glanimaldemo.image;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.se.glanimaldemo.R;
import com.se.glanimaldemo.image.filter.ColorFilter;
import com.se.glanimaldemo.image.filter.ContrastColorFilter;


/**
 * Description:
 */
public class SGLViewActivity extends AppCompatActivity{

    private SGLView mGLView;
    private boolean isHalf=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        mGLView= (SGLView) findViewById(R.id.glView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mDeal:
                isHalf=!isHalf;
                if(isHalf){
                    item.setTitle("处理一半");
                }else{
                    item.setTitle("全部处理");
                }
                mGLView.getRender().refresh();
                break;
            case R.id.mDefault:
                mGLView.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.NONE));
                break;
            case R.id.mGray:
                mGLView.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.GRAY));
                break;
            case R.id.mCool:
                mGLView.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.COOL));
                break;
            case R.id.mWarm:
                mGLView.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.WARM));
                break;
            case R.id.mBlur:
                mGLView.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.BLUR));
                break;
            case R.id.mMagn:
                mGLView.setFilter(new ContrastColorFilter(this, ColorFilter.Filter.MAGN));
                break;

        }
        mGLView.getRender().getFilter().setHalf(isHalf);
        mGLView.requestRender();
        return super.onOptionsItemSelected(item);
    }

}
