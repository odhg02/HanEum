package com.example.byeongwoo.handroad;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by odjh0 on 2018-08-01.
 */

public class gpsActivity extends AppCompatActivity {
    private static String URL_REGIST = "http://14.63.168.206/handroad/gpslist.php";
    private Button btn_ma;
    private String mName,mid;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road);
        sessionManager = new SessionManager(this);


        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        final String mName = user.get(SessionManager.NAME);
        String mid = user.get(SessionManager.ID);
        btn_ma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Registd();
            }
        });
    }

    private void Registd() {
        final double longitude = roadfindActivity.longitude;
        final double latitude = roadfindActivity.latitude;
        final String name = this.mName.toString().trim();
        final String ID = this.mid.toString().trim();
        final String lon2;
        lon2 = Double.toString(longitude);

        Toast.makeText(gpsActivity.this, name, Toast.LENGTH_SHORT).show();
        Toast.makeText(gpsActivity.this, lon2, Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(gpsActivity.this, "위치 정보저장띠", Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(gpsActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }//거짓값 == JSON 으로 받지못한 데이터들의 예외처리값
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(gpsActivity.this, "님은 안됨요 !"+ e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(gpsActivity.this, "못가져옴 ㅠ !"+ error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            //hash key 값으로 서버 php로 데이터 전송
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("ID", mid);
//                params.put("latitude", String.valueOf(latitude));
//                params.put("longitude", String.valueOf(longitude));
//                params.put("name", mName);
                params.put("ID", "1");
                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("name", "2");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}




