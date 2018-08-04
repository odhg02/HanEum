package com.example.byeongwoo.handroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class registActivity extends AppCompatActivity {

    private EditText name,age,ID,password,c_password,address;
    private Button btn_regist;
    private ProgressBar loading;
    private static String URL_REGIST = "http://14.63.168.206/handroad/regist.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        loading = findViewById(R.id.loading);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        ID = findViewById(R.id.ID);
        password = findViewById(R.id.password);
        c_password = findViewById(R.id.c_password);
        address = findViewById(R.id.address);
        btn_regist = findViewById(R.id.btn_regist);


        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 텍스트view 누를시
                Regist();
            }
        });

    }
    
    private void Regist() {
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);
        
        final String name = this.name.getText().toString().trim();
        final String age = this.age.getText().toString().trim();
        final String ID = this.ID.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String address = this.address.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        //json 으로 보낸 데이터값이 조건문에 의해 success 으로 갔을경우
                        String success = jsonObject.getString("success");
                        //성공값
                        if (success.equals("1")) {
                            Toast.makeText(registActivity.this, "회원가입 성공하셨습니다 !", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(registActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }//거짓값 == JSON 으로 받지못한 데이터들의 예외처리값
                    }catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(registActivity.this, "회원가입에 실패하셨습니다 !"+ e.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);
                    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(registActivity.this, "회원가입에 실패하셨습니다 !"+ error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            //hash key 값으로 서버 php로 데이터 전송
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("age", age);
                params.put("ID", ID);
                params.put("password", password);
                params.put("address", address);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
