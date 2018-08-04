package com.example.byeongwoo.handroad;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;


public class mainActivity extends AppCompatActivity {

    private TextView name, id;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Button btnShowNavigationDrawer;


    //세션을 라이브브러리 사용으로인해 정보를 SesssionManager 클래스에 함수로 선언했기때문에
    //위방법과 같이 선언을하면 사용하는 클래스에서 로그인에 대한 세션 정보를 가져올수 있다.
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);
        //로그인 여부 메서드

        //세션 로그인 여부 체크 로그인 안되있으면 로그인 시켜버림
        sessionManager.checkLogin();
        name = findViewById(R.id.nav_name);
        id = findViewById(R.id.nav_id);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(SessionManager.NAME);
        String mid = user.get(SessionManager.ID);


        //Style에서 NoActionBar로 ActionBar를 Disable 시켰으니
        //우리가 ActionBar처럼 사용 할 toolbar를 ActionBar처럼
        //사용하기위해 setSupportActionBar에 설정해준다.
        //주의 할 점은 xml에 <include>의 id를 가져와서 설정하는것에 유의
        toolbar = (Toolbar) findViewById(R.id.toolbarInclude);
        setSupportActionBar(toolbar);

        //여기서 setContentView로 설정되어있는건 activity_main이므로
        //toolbar에 구현해둔 컴포넌트를 findViewById로 가져오기위해
        //toolbar.findViewById로 찾아준다
        btnShowNavigationDrawer = (Button) toolbar.findViewById(R.id.btnShowNavigationDrawer);
        btnShowNavigationDrawer.setOnClickListener(onClickListener);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = setUpActionBarToggle();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        setUpDrawerContent(navigationView);

        View nav_header_view = navigationView.inflateHeaderView(R.layout.header_nav);

        //회원 아이디 이름 값을 넘겨주는 메소드
        TextView nav_header_id_text = (TextView) nav_header_view.findViewById(R.id.nav_id);
        TextView nav_header_name_text = (TextView) nav_header_view.findViewById(R.id.nav_name);
        nav_header_id_text.setText(mName);
        nav_header_name_text.setText(mid);

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        ActionBarDrawerToggle actionBarDrawerToggle = null;
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ActionBarDrawerToggle actionBarDrawerToggle = null;
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnShowNavigationDrawer:
                    drawerLayout.openDrawer(GravityCompat.START);
                    break;
            }
        }
    };

    private void setUpDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //길찾
                    case R.id.first_navigation_item:
                        Intent intent = new Intent(mainActivity.this, roadfindActivity.class);
                        startActivity(intent);
                        break;
                    //도움주기
                    case R.id.second_navigation_item:
                        Intent intent1 = new Intent(mainActivity.this, helpgiveActivity.class);
                        startActivity(intent1);
                        break;
                    //도움받기
                    case R.id.third_navigation_item:
                        Intent intent2 = new Intent(mainActivity.this, helpreceiveActivity.class);
                        startActivity(intent2);
                        break;
                    //내정보 관리
                    case R.id.fourth_navigation_item:

                        break;
                    //긴급호출
                    case R.id.fifth_navigation_item:

                        break;

                    case R.id.logout:
                        sessionManager.logout();
                        break;
                }
                return false;
            }
        });
    }

    private ActionBarDrawerToggle setUpActionBarToggle(){
        return new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }
}

