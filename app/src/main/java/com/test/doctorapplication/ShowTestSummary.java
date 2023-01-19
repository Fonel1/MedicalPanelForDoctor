package com.test.doctorapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShowTestSummary extends AppCompatActivity {

    Unbinder unbinder;

    @BindView(R.id.receivedImage)
    ImageView sentImage;
    @BindView(R.id.txt_testSummary)
    TextView txt_testSummary;
    long startTimeDB = System.nanoTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test_summary);

        long endTime = System.nanoTime();
        long duration = (endTime - startTimeDB)/1000000;
        System.out.println("Czas odbierania zdjęcia: " + duration + " ms");

        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        String summary = intent.getStringExtra("summaryTxt");
        String imageUrl = intent.getStringExtra("photo");

        Glide.with(this)
                .load(imageUrl)
                .into(sentImage);

        txt_testSummary.setText(summary);
    }
}