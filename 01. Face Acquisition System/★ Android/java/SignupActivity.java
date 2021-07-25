package com.example.cam;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText id_txt;
    private Button sign_up_btn;
    private CheckBox terms_conditions;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 회원가입 버튼 클릭 시 수행
        sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_txt = findViewById(R.id.id_txt);
                terms_conditions = findViewById(R.id.terms_conditions);

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

                request_validate(userID);

                if(validate)
                    show(userID);
            }
        });

    }

    private void request_validate(String userId)
    {
        if(validate)
            return;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        validate = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "ID that exists.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ValidateRequest validateRequest = new ValidateRequest(userId, responseListener);
        RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
        queue.add(validateRequest);
    }

    private void show(String userID)
    {
        boolean ret;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        register(userID);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No 버튼을 클릭했을때 처리
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Is the entered '"+ userID + "' correct?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void register(String userID)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject( response );
                    boolean success = jsonObject.getBoolean( "success" );

                    //회원가입 성공시
                    if(success) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to sign up", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        //서버로 Volley를 이용해서 요청
        RegisterRequest registerRequest = new RegisterRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue( SignupActivity.this );
        queue.add( registerRequest );
    }
}
