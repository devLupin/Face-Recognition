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
    private TextToSpeech eng_tts;
    private TextToSpeech kor_tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        eng_tts = new TextToSpeech(SignupActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    eng_tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
        kor_tts = new TextToSpeech(SignupActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    eng_tts.setLanguage(Locale.KOREAN);
                }
            }
        });

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
                String kor_msg = "출입 관리 시스템 개발을 위해 얼굴 이미지를 수집하고자 합니다.\n" +
                        "수집된 데이터는 서버에 저장되어 특정 인원 식별 목적으로 사용됩니다.\n" +
                        "연구목적으로만 사용됩니다.\n\n";
                String eng_msg = "We want to collect face images for the development of an access management system.\n" +
                        "The collected data is stored on the server and used for the purpose of identifying specific persons.\n" +
                        "For research purposes only.";
                String msg = kor_msg + eng_msg;

                kor_tts.speak(kor_msg, TextToSpeech.QUEUE_FLUSH, null);
                eng_tts.speak(eng_msg, TextToSpeech.QUEUE_FLUSH, null);

                AlertDialog show = new AlertDialog.Builder(SignupActivity.this)
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.ok, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                kor_tts.stop();
                                eng_tts.stop();
                            }
                        }).show();
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