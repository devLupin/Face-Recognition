package com.example.aquisition;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    private EditText id_txt;
    private Button sign_up_btn;
    private CheckBox terms_conditions;
    private Button policy_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 회원가입 버튼 클릭 시 수행
        sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terms_conditions = findViewById(R.id.terms_conditions);
                id_txt = findViewById(R.id.id_txt);
                id_txt.setMovementMethod(new ScrollingMovementMethod());

                String userID = id_txt.getText().toString();
                boolean isAgree = terms_conditions.isChecked();

                // 약관 미동의 시
                if(isAgree == false){
                    Toast.makeText(getApplicationContext(), "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 아이디가 공백이거나 띄어쓰기가 포함된 경우
                if(userID.isEmpty() || userID == null || userID.contains(" ")){
                    Toast.makeText(getApplicationContext(), "ID cannot be blank or contain spaces.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 영문 알파벳만 가능
                for(int i=0; i<userID.length(); i++) {
                    int idx = userID.charAt(i);
                    if(idx < 97 || idx > 122){
                        Toast.makeText(getApplicationContext(), "ID can only consist of lowercase letters.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // 폴더 생성하고 중복
                if(!createFolder(userID)) {
                    Toast.makeText(getApplicationContext(), "Duplicate ID.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        policy_btn = findViewById(R.id.policy_btn);
        policy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, Terms_and_conditions.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean createFolder(String id) {
        File dir = new File(getCacheDir(), id);     // Internal storage path

        if(!dir.exists()) {
            dir.mkdirs();
            return true;
        }
        else {
            return false;
        }
    }
}