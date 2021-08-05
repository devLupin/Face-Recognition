package com.example.aquisition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Terms_and_conditions extends AppCompatActivity {

    private static final String KOR_MSG = "출입 관리 시스템을 위한 얼굴 영상 데이터 수집\n" +
            " [출입 관리 시스템]\n" +
            " - AI 기술 적용을 통한 연구실 출입 관리 시스템 개발\n" +
            " - 애플리케이션을 통해 얼굴 비디오 데이터 수집\n" +
            " - 영상 데이터는 향후 전처리를 통해 출입 시스템의 자원으로 활용\n" +
            " - 사전 훈련된 모델을 애플리케이션에 적용하여 실시간 인원 식별\n\n" +
            " [영상 촬영]\n" +
            " - 약 5초간 지정된 프레임에 맞춰 얼굴을 촬영합니다.\n" +
            " - 한번 저장된 데이터는 삭제/수정이 불가합니다.\n" +
            " - 수집된 데이터는 연구목적으로만 사용됩니다.\n\n\n\n";
    private static final String ENG_MSG = "Face image data collection for access management system\n" +
            " [Access control system]\n" +
            " - Development of laboratory access management system through application of AI technology\n" +
            " - Collect facial video data through the application\n" +
            " - The image data will be used as a resource for the access system through pre-processing in the future\n" +
            " - Real-time personnel identification by applying pre-trained models to applications\n\n" +
            " [Video shooting]\n" +
            " - Shoots a face in the designated frame for about 5 seconds.\n" +
            " - Once saved, data cannot be deleted/modified.\n" +
            " - The collected data is used for research purposes only.";

    private TextView contents;
    private RadioButton rg_btn_agree, rg_btn_disagree;
    private RadioGroup radioGroup;
    private Button check_btn;

//    private TextToSpeech eng_tts;
//    private TextToSpeech kor_tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        contents = (TextView)findViewById(R.id.contents);
        contents.setMovementMethod(new ScrollingMovementMethod());
        contents.setText(KOR_MSG + ENG_MSG);

        /* TTS, radio button
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

        rg_btn_agree = (RadioButton)findViewById(R.id.rg_btn_agree);
        rg_btn_disagree = (RadioButton)findViewById(R.id.rg_btn_disagree);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        */

        check_btn = (Button)findViewById(R.id.check_btn);
        check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* 라디오 버튼 관련 코드
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)findViewById(id);

                if(rb.getText().toString().equals("agree")) {
                    Toast.makeText(getApplicationContext(), "Agreed.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Terms_and_conditions.this, SignupActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show();
                    return;
                }
                */

                /* 음성 지원
                kor_tts.speak(KOR_MSG, TextToSpeech.QUEUE_FLUSH, null);
                eng_tts.speak(ENG_MSG, TextToSpeech.QUEUE_FLUSH, null);

                AlertDialog show = new AlertDialog.Builder(SignupActivity.this)
                        .setMessage(KOR_MSG + ENG_MSG)
                        .setPositiveButton(android.R.string.ok, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                kor_tts.stop();
                                eng_tts.stop();
                            }
                        }).show();
                */

                Intent intent = new Intent(Terms_and_conditions.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}