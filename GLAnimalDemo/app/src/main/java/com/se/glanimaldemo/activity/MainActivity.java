package com.se.glanimaldemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.se.glanimaldemo.R;
import com.se.glanimaldemo.image.SGLViewActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.square).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SquareActivity.class));
            }
        });

        findViewById(R.id.triangles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TrianglesActivity.class));
            }
        });

        findViewById(R.id.texture_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TextureImgActivity.class));
            }
        });

        findViewById(R.id.change_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SGLViewActivity.class));
            }
        });

        findViewById(R.id.show_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,GlideImgActivity.class));
            }
        });

        findViewById(R.id.more_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MoreTextureActivity.class));
            }
        });

        findViewById(R.id.flow_water).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FlowWaterActivity.class));
            }
        });
        findViewById(R.id.cube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CubeActivity.class));
            }
        });
        findViewById(R.id.cube_texture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CubeTextureActivity.class));
            }
        });
        findViewById(R.id.multi_texture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MultTexActivity.class));
            }
        });
        findViewById(R.id.cus_texture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MultiMActivity.class));
            }
        });
    }

}
