package com.devlupin.detectiontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devlupin.detectiontest.sql.Database;
import com.devlupin.detectiontest.sql.InfoDBHelper;

public class PWFindSuccessActivity extends AppCompatActivity {

    private EditText pw_txt;
    private EditText pw_confirm_txt;

    private Button next_btn;

    private InfoDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwfind_success);

        // DB Init
        dbHelper = new InfoDBHelper(this);
        dbHelper.open();
        dbHelper.create();

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

                if(!pw.equals(pw_confirm)) {
                    Toast.makeText(PWFindSuccessActivity.this, "비밀번호가 동일하지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                // id 값에서 입력된 패스워드로 데이터베이스 업데이트 코드 삽입
                // public boolean update(String name, String id, String pw, String ph_num, String email)
                ContentValues values = find(id);

                if(values == null) {
                    Toast.makeText(PWFindSuccessActivity.this, "DB error in PWFindSuccess()", Toast.LENGTH_LONG).show();
                    return;
                }

                dbHelper.update(values, pw);

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

    private ContentValues find(String id) {
        ContentValues values = new ContentValues();
        Cursor cursor = dbHelper.selectColumns();

        while(cursor.moveToNext()) {
            String cur_name = cursor.getString(cursor.getColumnIndex(Database.Info.NAME));
            String cur_id = cursor.getString(cursor.getColumnIndex(Database.Info.ID));
            String cur_pw = cursor.getString(cursor.getColumnIndex(Database.Info.PW));
            String cur_ph_num = cursor.getString(cursor.getColumnIndex(Database.Info.PH_NUM));
            String cur_email = cursor.getString(cursor.getColumnIndex(Database.Info.EMAIL));

            if(cur_id.equals(id)) {
                values.put(Database.Info.NAME, cur_name);
                values.put(Database.Info.ID, cur_id);
                values.put(Database.Info.PW, cur_pw);
                values.put(Database.Info.PH_NUM, cur_ph_num);
                values.put(Database.Info.EMAIL, cur_email);

                return values;
            }
        }

        return null;
    }
}