package com.google.mlkit.vision.demo.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.mlkit.vision.demo.R;

public class DuplicateAccountActivity extends AppCompatActivity {

    private TextView find_id_btn;
    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_account);

        find_id_btn = findViewById(R.id.find_id_btn);
        find_id_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        find_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DuplicateAccountActivity.this, IDFindActivity.class);
                startActivity(intent);
                finish();
            }
        });

        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DuplicateAccountActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}