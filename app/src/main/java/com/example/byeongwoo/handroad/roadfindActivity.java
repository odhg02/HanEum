package com.example.byeongwoo.handroad;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class roadfindActivity extends AppCompatActivity {

    TMapView mapView;
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;
    EditText keywordView;
    ListView listView;
    ArrayAdapter<POI> mAdapter;
    TMapPoint start, end;
    RadioGroup typeView;
    public static double latitude;
    public static double longitude;

    //여기서 추가
    private static String URL_REGIST = "http://14.63.168.206/handroad/gpslist.php";
    private Button btn_ma;
    private String mName,mid;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        typeView = (RadioGroup)findViewById(R.id.group_type);
        keywordView = (EditText) findViewById(R.id.edit_keyword);
        listView = (ListView) findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<POI>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = (TMapView) findViewById(R.id.map_view);

        //여기 추가
        btn_ma = (Button) findViewById(R.id.btn_ma);
        btn_ma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Regist();
            }
        });

        mapView.setIconVisibility(true); // 버튼
        setGps();

        mapView.setTrackingMode(true);
        mapView.setSightVisible(true);
        mapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupMap();
                    }
                });
            }

            @Override
            public void SKTMapApikeyFailed(String s) {

            }
        });
        mapView.setSKTMapApiKey("25a3ba7e-4bd7-4a58-aaef-7c976cc6a378");
        mapView.setLanguage(TMapView.LANGUAGE_KOREAN);

        Button btn = (Button) findViewById(R.id.btn_add_marker);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TMapPoint point = mapView.getCenterPoint();
                addMarker(point.getLatitude(), point.getLongitude(), "My Marker");
            }
        });

        btn = (Button) findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPOI();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                POI poi = (POI) listView.getItemAtPosition(position);
                moveMap(poi.item.getPOIPoint().getLatitude(), poi.item.getPOIPoint().getLongitude());
            }
        });

        btn = (Button) findViewById(R.id.btn_route);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start != null && end != null) {
                    searchRoute(start, end);
                    start = end = null;
                } else {
                    Toast.makeText(roadfindActivity.this, "경로 설정이 안되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                mapView.setLocationPoint(longitude, latitude);
                mapView.setCenterPoint(longitude, latitude);

        }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }

    private void searchRoute(TMapPoint start, TMapPoint end) {
        TMapData data = new TMapData();
        data.findPathData(start, end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(final TMapPolyLine path) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path.setLineWidth(5);
                        path.setLineColor(Color.RED);
                        mapView.addTMapPath(path);
                        Bitmap s = ((BitmapDrawable) ContextCompat.getDrawable(roadfindActivity.this, android.R.drawable.ic_input_delete)).getBitmap();
                        Bitmap e = ((BitmapDrawable) ContextCompat.getDrawable(roadfindActivity.this, android.R.drawable.ic_input_get)).getBitmap();
                        mapView.setTMapPathIcon(s, e);

                    }
                });
            }
        });
    }

    private void searchPOI() {
        TMapData data = new TMapData();
        String keyword = keywordView.getText().toString();
        if (!TextUtils.isEmpty(keyword)) {
            data.findAllPOI(keyword, new TMapData.FindAllPOIListenerCallback() {
                @Override
                public void onFindAllPOI(final ArrayList<TMapPOIItem> arrayList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapView.removeAllMarkerItem();
                            mAdapter.clear();

                            for (TMapPOIItem poi : arrayList) {
                                addMarker(poi);
                                mAdapter.add(new POI(poi));
                            }

                            if (arrayList.size() > 0) {
                                TMapPOIItem poi = arrayList.get(0);
                                moveMap(poi.getPOIPoint().getLatitude(), poi.getPOIPoint().getLongitude());
                            }
                        }
                    });
                }
            });
        }
    }

    public void addMarker(TMapPOIItem poi) {
        TMapMarkerItem item = new TMapMarkerItem();
        item.setTMapPoint(poi.getPOIPoint());
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_add)).getBitmap();
        item.setIcon(icon);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(poi.getPOIName());
        item.setCalloutSubTitle(poi.getPOIContent());
        Bitmap left = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)).getBitmap();
        item.setCalloutLeftImage(left);
        Bitmap right = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_get)).getBitmap();
        item.setCalloutRightButtonImage(right);
        item.setCanShowCallout(true);
        mapView.addMarkerItem(poi.getPOIID(), item);
    }

    private void addMarker(double lat, double lng, String title) {
        TMapMarkerItem item = new TMapMarkerItem();
        TMapPoint point = new TMapPoint(lat, lng);
        item.setTMapPoint(point);
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_add)).getBitmap();
        item.setIcon(icon);
        item.setPosition(0.5f, 1);
        item.setCalloutTitle(title);
        item.setCalloutSubTitle("sub " + title);
        Bitmap left = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_alert)).getBitmap();
        item.setCalloutLeftImage(left);
        Bitmap right = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_input_get)).getBitmap();
        item.setCalloutRightButtonImage(right);
        item.setCanShowCallout(true);
        mapView.addMarkerItem("m" + id, item);
        id++;
    }

    int id = 0;

    boolean isInitialized = false;

    private void setupMap() {
        isInitialized = true;
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
        if (cacheLocation != null) {
            moveMap(cacheLocation.getLatitude(), cacheLocation.getLongitude());
            setMyLocation(cacheLocation.getLatitude(), cacheLocation.getLongitude());
        }
        mapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                String message = null;
                switch (typeView.getCheckedRadioButtonId()) {
                    case R.id.radio_start:
                        start = tMapMarkerItem.getTMapPoint();
                        message = "start";
                        break;
                    case R.id.radio_end:
                        end = tMapMarkerItem.getTMapPoint();
                        message = "end";
                        break;
                }
                Toast.makeText(roadfindActivity.this, message + " setting", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            mListener.onLocationChanged(location);
        }
        mLM.requestSingleUpdate(mProvider, mListener, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    Location cacheLocation = null;

    private void moveMap(double lat, double lng) {
        mapView.setCenterPoint(lng, lat);
    }

    private void setMyLocation(double lat, double lng) {
        Bitmap icon = ((BitmapDrawable) ContextCompat.getDrawable(this, android.R.drawable.ic_dialog_map)).getBitmap();
        mapView.setIcon(icon);
        mapView.setLocationPoint(lng, lat);
        mapView.setIconVisibility(true);
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (isInitialized) {
                moveMap(location.getLatitude(), location.getLongitude());
                setMyLocation(location.getLatitude(), location.getLongitude());
            } else {
                cacheLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);


    }
    //여기 추가
    private void Regist() {
        final double longitude = roadfindActivity.longitude;
        final double latitude = roadfindActivity.latitude;
        final String mid = "kjhkjkjh";
        final String mName = "okl";
        final String str_latitude = String.valueOf(latitude);
        final String str_longitude = String.valueOf(longitude);


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
                                Toast.makeText(roadfindActivity.this, ""+latitude, Toast.LENGTH_SHORT).show();
                                Toast.makeText(roadfindActivity.this, ""+longitude, Toast.LENGTH_SHORT).show();
                                Toast.makeText(roadfindActivity.this, "위치 정보저장띠", Toast.LENGTH_SHORT).show();
                            }//거짓값 == JSON 으로 받지못한 데이터들의 예외처리값
                        }catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(roadfindActivity.this, "님은 안됨요 !"+ e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(roadfindActivity.this, "못가져옴 ㅠ !"+ error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            //hash key 값으로 서버 php로 데이터 전송
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", mid);
                params.put("latitude", str_latitude);
                params.put("longitude", str_longitude);  //여기 고쳐볼것
                params.put("name", mName);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}