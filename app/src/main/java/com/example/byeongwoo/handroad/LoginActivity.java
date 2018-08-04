package com.example.byeongwoo.handroad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText id , password;
    private Button btn_login;
    private TextView link_regist;
    private ProgressBar loading;
    private static String URL_Login = "http://14.63.168.206/handroad/login.php";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        loading = findViewById(R.id.loading);
        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        link_regist = findViewById(R.id.link_regist);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mid = id.getText().toString().trim();
                final String mpassword = password.getText().toString().trim();

                if(!mid.isEmpty() || !mpassword.isEmpty()) {
                    Login(mid, mpassword);
                } else {
                    id.setError("아이디를 넣어주세요");
                    password.setError("비밀번호를 넣어주세요");
                }
            }
        });

        link_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, registActivity.class));
            }
        });
    }

        private void Login(final String id , final String password) {

            loading.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.GONE);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Login,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String success = jsonObject.getString("success");
                                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                                    if (success.equals("1")){

                                        for (int i = 0 ; i < jsonArray.length() ; i++ ) {

                                            JSONObject object = jsonArray.getJSONObject(i);
                                            //intent 이용해서 로그인으로 정보값을 넘김
                                            String name = object.getString("name").trim();
                                            String id = object.getString("id").trim();
                                            //세션 생성
                                            sessionManager.createSession(name, id);
                                            Intent intent =new Intent(LoginActivity.this, mainActivity.class);
                                            intent.putExtra("name",name);
                                            intent.putExtra("id",id);
                                            startActivity(intent);
                                            finish();
                                            loading.setVisibility(View.GONE);
                                        }

                                    }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                loading.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Error" +e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error){
                    loading.setVisibility(View.GONE);
                    btn_login.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Error" +error.toString(), Toast.LENGTH_SHORT).show();
                }
            })

            {
                @Override
                protected Map<String , String > getParams() throws AuthFailureError {
                    Map<String , String > params = new HashMap<>();
                    params.put("id", id);
                    params.put("password", password);
                    return  params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

}
