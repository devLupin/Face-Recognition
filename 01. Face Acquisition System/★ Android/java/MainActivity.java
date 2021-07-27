package com.example.aquisition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button sign_in_btn;
    private Button sign_up_btn;

    private TextView id_txt;
    public static String _id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sign_in_btn = findViewById(R.id.sign_in_btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_txt = findViewById(R.id.id_txt);
                String userID = id_txt.getText().toString();

                if(idCheck(userID)) {
                    _id = userID;

                    Toast.makeText(getApplicationContext(), "'" + userID + "'" + " Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please check your ID", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

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