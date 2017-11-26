package com.smartiot.hamzajguerim.challenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static com.smartiot.hamzajguerim.challenge.StartScreenActivity.REQUEST_IMAGE_CAPTURE;

public class SignUpActivity extends AppCompatActivity {

    private Button btnAddFace;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAddFace = findViewById(R.id.add_face);
        imageView = findViewById(R.id.imageView2);
        btnAddFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;
    private void dispatchTakePictureIntent() {
        Intent i = new Intent(SignUpActivity.this, TakePictureActivity.class);

        startActivityForResult(i, REQUEST_SELECT_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}
