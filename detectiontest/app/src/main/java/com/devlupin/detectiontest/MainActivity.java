package com.devlupin.detectiontest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devlupin.detectiontest.sql.Database;
import com.devlupin.detectiontest.sql.InfoDBHelper;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    private long backKeyPressedTime = 0;
    private Toast toast;

    private Button next_btn;

    private EditText id_txt;
    private EditText pw_txt;
    private TextView find_id_btn;
    private TextView find_pw_btn;
    private TextView create_account_btn;
    private TextView terms_and_condition_btn;
    private TextView policy_btn;

    private InfoDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DB Init
        dbHelper = new InfoDBHelper(this);
        dbHelper.open();
        dbHelper.create();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        id_txt = findViewById(R.id.id_txt);
        pw_txt = findViewById(R.id.pw_txt);
        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = id_txt.getText().toString();
                String pw = pw_txt.getText().toString();

                if(isInCorrect(id) || isInCorrect(pw)) {
                    Toast.makeText(MainActivity.this, "모든 칸을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isLogin(id, pw)) {
                    Intent intent = new Intent(MainActivity.this, FindFailActivity.class);
                    startActivity(intent);
                    return;
                }

                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivity(intent);
                intent.putExtra("id", id);
            }
        });

        find_id_btn = findViewById(R.id.find_id_btn);
        find_id_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        find_id_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IDFindActivity.class);
                startActivity(intent);
                finish();
            }
        });

        find_pw_btn = findViewById(R.id.find_pw_btn);
        find_pw_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        find_pw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PWFindActivity.class);
                startActivity(intent);
                finish();
            }
        });

        create_account_btn = findViewById(R.id.create_account_btn);
        create_account_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });

        terms_and_condition_btn = findViewById(R.id.terms_and_condition_btn);
        terms_and_condition_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        terms_and_condition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TermsAndCondtionActivity.class);
                startActivity(intent);
            }
        });

        policy_btn = findViewById(R.id.policy_btn);
        policy_btn.setTextColor(Color.parseColor("#FFBB86FC"));
        policy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PolicyActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isInCorrect(String str) {
        if(str.isEmpty() || str == null || str.contains(" ")) {
            return true;
        }
        return false;
    }

    private boolean isLogin(String id, String pw) {
        Cursor cursor = dbHelper.selectColumns();

        while(cursor.moveToNext()) {
            //String cur_name = cursor.getString(cursor.getColumnIndex(Database.Info.NAME));
            String cur_id = cursor.getString(cursor.getColumnIndex(Database.Info.ID));
            String cur_pw = cursor.getString(cursor.getColumnIndex(Database.Info.PW));
            //String cur_ph_num = cursor.getString(cursor.getColumnIndex(Database.Info.PH_NUM));
            //String cur_email = cursor.getString(cursor.getColumnIndex(Database.Info.EMAIL));

            if(cur_id.equals(id) && cur_pw.equals(pw)) {
                return true;
            }
        }

        return false;
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
}