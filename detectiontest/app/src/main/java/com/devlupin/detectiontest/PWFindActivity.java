package com.devlupin.detectiontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PWFindActivity extends AppCompatActivity {

    private EditText email_txt;
    private EditText ph_num_txt;
    private EditText id_txt;

    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwfind);

        email_txt = findViewById(R.id.email_txt);
        ph_num_txt = findViewById(R.id.ph_num_txt);
        id_txt = findViewById(R.id.id_txt);

        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_txt.getText().toString();
                String ph_num = ph_num_txt.getText().toString();
                String id = id_txt.getText().toString();

                if(isInCorrect(email) || isInCorrect(ph_num) || isInCorrect(id)) {
                    Toast.makeText(PWFindActivity.this, "모든 칸을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                //존재하면?
                Intent intent = new Intent(PWFindActivity.this, PWFindSuccessActivity.class);
                startActivity(intent);
                intent.putExtra("id", id);
                finish();

                //없다면
                //Intent intent = new Intent(PWFindActivity.this, FindFailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isInCorrect(String str) {
        if(str.isEmpty() || str == null || str.contains(" ")) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}