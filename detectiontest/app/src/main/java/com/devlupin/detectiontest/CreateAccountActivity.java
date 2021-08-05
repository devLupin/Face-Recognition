package com.devlupin.detectiontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText first_name_txt;
    private EditText last_name_txt;
    private EditText id_txt;
    private EditText pw_txt;
    private EditText pw_confirm_txt;
    private EditText ph_num_txt;
    private EditText email_id_txt;
    private EditText email_addr_txt;

    private CheckBox check;
    private TextView terms_and_condition_btn;
    private TextView policy_btn;

    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        terms_and_condition_btn = findViewById(R.id.terms_and_condition_btn);
        terms_and_condition_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        terms_and_condition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccountActivity.this, TermsAndCondtionActivity.class);
                startActivity(intent);
            }
        });

        policy_btn = findViewById(R.id.policy_btn);
        policy_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        policy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccountActivity.this, PolicyActivity.class);
                startActivity(intent);
            }
        });


        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                first_name_txt = findViewById(R.id.first_name_txt);
                String first_name = first_name_txt.toString();
                if(isCorrect(first_name)) {
                    return;
                }

                last_name_txt = findViewById(R.id.last_name_txt);
                String last_name = last_name_txt.toString();
                if(isCorrect(last_name)) {
                    return;
                }

                id_txt = findViewById(R.id.id_txt);
                String id = id_txt.toString();
                if(isCorrect(id)) {
                    return;
                }

                pw_txt = findViewById(R.id.pw_txt);
                String pw = pw_txt.toString();
                if(isCorrect(pw)) {
                    return;
                }

                pw_confirm_txt = findViewById(R.id.pw_confirm_txt);
                String pw_confirm = pw_confirm_txt.toString();
                if(isCorrect(pw_confirm)) {
                    return;
                }

                ph_num_txt = findViewById(R.id.ph_num_txt);
                String ph_num = ph_num_txt.toString();
                if(isCorrect(ph_num)) {
                    return;
                }

                email_id_txt = findViewById(R.id.email_id_txt);
                String email_id = email_id_txt.toString();
                if(isCorrect(email_id)) {
                    return;
                }

                email_addr_txt = findViewById(R.id.email_addr_txt);
                String email_addr = email_addr_txt.toString();
                if(isCorrect(email_addr)) {
                    return;
                }

                if(isDuplicate(email_id+"@"+email_addr) || isDuplicate(ph_num)) {
                    return;
                }

                if(!pw.equals(pw_confirm)) {
                    return;
                }

                check = findViewById(R.id.check);
                if(check.isChecked() == false) {
                    return;
                }
            }
        });
    }

    private boolean isCorrect(String str) {
        if(str.isEmpty() || str == null || str.contains(" ")) {
            return true;
        }
        return false;
    }

    private boolean isDuplicate(String arg) {
        // 데이터베이스 조회해서 중복되면 true 리턴
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}