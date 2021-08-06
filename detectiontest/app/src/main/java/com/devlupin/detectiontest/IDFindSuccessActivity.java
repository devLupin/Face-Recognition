package com.devlupin.detectiontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IDFindSuccessActivity extends AppCompatActivity {

    private TextView textView;
    private TextView find_pw_btn;
    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idfind_success);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");

        textView = findViewById(R.id.textView);
        textView.setText("아이디 : " + id);

        find_pw_btn = findViewById(R.id.find_pw_btn);
        find_pw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IDFindSuccessActivity.this, PWFindActivity.class);
                startActivity(intent);
                finish();
            }
        });

        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IDFindSuccessActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}