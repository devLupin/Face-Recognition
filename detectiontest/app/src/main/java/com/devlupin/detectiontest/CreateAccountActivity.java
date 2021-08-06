package com.devlupin.detectiontest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devlupin.detectiontest.sql.InfoDBHelper;
import com.devlupin.detectiontest.sql.SQLite;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText first_name_txt;
    private EditText last_name_txt;
    private EditText id_txt;
    private EditText pw_txt;
    private EditText pw_confirm_txt;
    private EditText ph_num_txt;
    private EditText email_id_txt;
    private EditText email_addr_txt;

    private InfoDBHelper dbHelper;

    private CheckBox check;
    private TextView terms_and_condition_btn;
    private TextView policy_btn;

    private Button next_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        dbHelper = new InfoDBHelper(this);

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

        first_name_txt = findViewById(R.id.first_name_txt);
        last_name_txt = findViewById(R.id.last_name_txt);
        id_txt = findViewById(R.id.id_txt);
        next_btn = findViewById(R.id.next_btn);
        pw_txt = findViewById(R.id.pw_txt);
        ph_num_txt = findViewById(R.id.ph_num_txt);
        pw_confirm_txt = findViewById(R.id.pw_confirm_txt);
        email_id_txt = findViewById(R.id.email_id_txt);
        email_addr_txt = findViewById(R.id.email_addr_txt);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first_name = first_name_txt.getText().toString();
                String last_name = last_name_txt.getText().toString();
                String id = id_txt.getText().toString();
                String pw = pw_txt.getText().toString();
                String pw_confirm = pw_confirm_txt.getText().toString();
                String ph_num = ph_num_txt.getText().toString();
                String email_id = email_id_txt.getText().toString();
                String email_addr = email_addr_txt.getText().toString();

                if(isInCorrect(first_name) || isInCorrect(last_name) || isInCorrect(id) ||
                        isInCorrect(pw) || isInCorrect(pw_confirm) || isInCorrect(ph_num) ||
                        isInCorrect(email_id) || isInCorrect(email_addr)) {
                    Toast.makeText(CreateAccountActivity.this, "모든 필드는 비워두거나, 공백을 포함할 수 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!idCheck(id)) {
                    Toast.makeText(CreateAccountActivity.this, "아이디는 영문 소문자, 숫자로만 구성 가능합니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!pw.equals(pw_confirm)) {
                    Toast.makeText(CreateAccountActivity.this, "입력한 비밀번호가 동일하지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                int pw_stat = pwCheck(pw);
                if(pw_stat == -1) {
                    Toast.makeText(CreateAccountActivity.this, "8자 이상의 비밀번호로 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(pw_stat == -2) {
                    Toast.makeText(CreateAccountActivity.this, "비밀번호는 영문 소문자, 숫자로만 구성 가능합니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(phCheck(ph_num)) {
                    Toast.makeText(CreateAccountActivity.this, "전화 번호는 숫자만 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                check = findViewById(R.id.check);
                if(check.isChecked() == false) {
                    Toast.makeText(CreateAccountActivity.this, "약관에 동의해주셔야 합니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(isDuplicate(SQLite.Info.ID, id) ||
                        isDuplicate(SQLite.Info.EMAIL, email_id+"@"+email_addr) ||
                        isDuplicate(SQLite.Info.PH_NUM, ph_num)) {
                    Intent intent = new Intent(CreateAccountActivity.this, DuplicateAccountActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                dbHelper.insert(first_name + "_" + last_name,
                        id,
                        pw,
                        ph_num,
                        email_id + "@" + email_addr);

                Intent intent = new Intent(CreateAccountActivity.this, AccountSuccessActivity.class);
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

    private boolean idCheck(String id) {
        for(int i=0; i<id.length(); i++) {
            if((int)id.charAt(i) >= '0' && (int)id.charAt(i) <= '9')
                continue;
            else if((int)id.charAt(i) >= 'a' && (int)id.charAt(i) <= 'z')
                continue;
            else
                return false;
        }

        return true;
    }

    private int pwCheck(String pw) {
        if(pw.length() < 8) {
            return -1;
        }

        for(int i=0; i<pw.length(); i++) {
            if((int)pw.charAt(i) >= '0' && (int)pw.charAt(i) <= '9')
                continue;
            else if((int)pw.charAt(i) >= 'a' && (int)pw.charAt(i) <= 'z')
                continue;
            else
                return -2;
        }

        return 0;
    }

    private boolean phCheck(String ph_num) {
        for(int i=0; i<ph_num.length(); i++) {
            if((int)ph_num.charAt(i) >= '0' && (int)ph_num.charAt(i) <= '9')
                continue;
            else
                return false;
        }
        return true;
    }

    // SELECT field FROM TABLE WHERE field="arg";
    private boolean isDuplicate(String field, String arg) {
        // 데이터베이스 조회해서 중복되면 true 리턴

        Cursor results = dbHelper.select(field, arg);

        if(results.moveToNext())
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}