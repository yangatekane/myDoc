package com.mydoc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by yanga on 2013/05/22.
 */
public class FullImageActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);
        Intent i = getIntent();
        String file = i.getExtras().getString("File");
        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
        imageView.setImageBitmap(BitmapFactory.decodeFile(file));
    }
}