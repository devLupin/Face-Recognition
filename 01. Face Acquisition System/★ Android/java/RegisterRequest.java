package com.example.cam;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    //서버 URL 설정(php 파일 연동)
    final static private String URL = "http://devlupin.dothome.co.kr/Register.php";
    private Map<String, String> map;
    //private Map<String, String>parameters;

    public RegisterRequest(String id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id", id);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}
