package com.devlupin.detectiontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PWFindSuccessActivity extends AppCompatActivity {

    private EditText pw_txt;
    private EditText pw_confirm_txt;

    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwfind_success);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");

        pw_txt = findViewById(R.id.pw_txt);
        pw_confirm_txt = findViewById(R.id.pw_confirm_txt);

        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pw = pw_txt.getText().toString();
                String pw_confirm = pw_confirm_txt.getText().toString();

                if(isInCorrect(pw) || isInCorrect(pw_confirm)) {
                    Toast.makeText(PWFindSuccessActivity.this, "모든 칸을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(pw.equals(pw_confirm)) {
                    Toast.makeText(PWFindSuccessActivity.this, "비밀번호가 동일하지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                // id 값에서 입력된 패스워드로 데이터베이스 업데이트 코드 삽입
                //

                Intent intent = new Intent(PWFindSuccessActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(PWFindSuccessActivity.this, "비밀번호가 재설정 되었습니다.", Toast.LENGTH_LONG).show();
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
}