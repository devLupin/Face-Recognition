package com.example.aquisition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button sign_in_btn;
    private Button sign_up_btn;

    private TextView id_txt;

    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // 화면 가로 고정

        sign_in_btn = findViewById(R.id.sign_in_btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_txt = findViewById(R.id.id_txt);
                String userID = id_txt.getText().toString();

                if(userID.isEmpty()){
                    Toast.makeText(getApplicationContext(), "ID cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!idCheck(userID)) {
                    Toast.makeText(getApplicationContext(), "ID does not exist.", Toast.LENGTH_SHORT).show();
                    Log.d("", "ABS Path: " + getCacheDir().getPath());
                    return;

                }

                Toast.makeText(getApplicationContext(), "'" + userID + "'" + " Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "Press once more to exit.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();
            toast = Toast.makeText(this,"Thank you",Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean idCheck(String id) {
        File dir = new File(getCacheDir(), id);     // Internal storage path

        if(dir.exists()) {
            return true;
        }
        else {
            return false;
        }
    }
}