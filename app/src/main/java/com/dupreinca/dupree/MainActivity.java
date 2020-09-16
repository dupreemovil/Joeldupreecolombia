package com.dupreinca.dupree;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;


import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVersion;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dupreinca.dupree.mh_adapters.MainPagerAdapter;
import com.dupreinca.dupree.mh_adapters.AuthenticatePagerAdapter;
import com.dupreinca.dupree.mh_dialogs.MH_Dialogs_Login;
import com.dupreeinca.lib_api_rest.model.dto.response.DataAuth;
import com.dupreeinca.lib_api_rest.model.dto.response.GenericDTO;
import com.dupreinca.dupree.mh_utilities.mPreferences;



import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity {

    private final String TAG="MCHD->MainActivity";

    ViewPager mViewPager;

    private BottomNavigationView bottomNavigation;
    /*
    int version;
    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();*/

    //Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inflando SwipeRefreshLayout
        SwipeRefreshLayout swipeRecycler = findViewById(R.id.SwipeRefreshL_ActMenu);
        swipeRecycler.setOnRefreshListener(mOnRefreshListener);
        swipeRecycler.setEnabled(false);

        //inflando view pager
        mViewPager = swipeRecycler.findViewById(R.id.pager);
        String versionName = BuildConfig.VERSION_NAME;
        RequiredVersion req = new RequiredVersion(versionName);
        new Http(this).control(req,this);


        //mViewPager.setAdapter(avp);
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        //inflando barra inferior
        bottomNavigation = findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        localBroadcastReceiver = new LocalBroadcastReceiver();

        System.out.println("Llamando a main");

    }


    /**
     * Selecciona una opcion en el menu inferior
     * @param idMenu
     */
    public void setSelectedItem(int idMenu) {
        if (bottomNavigation.getSelectedItemId()!=idMenu) {
            View view = bottomNavigation.findViewById(idMenu);
            view.performClick();
        }
    }

    private int oldItem=R.id.navigation_home;
    /**
     * Eventos de BottomNavigationView
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                    case R.id.navigation_asesora:
                    case R.id.navigation_atencion:
                    case R.id.navigation_catalogos:
                        if(oldItem!=item.getItemId()) {
                            oldItem = item.getItemId();
                            changePage(oldItem);
                        }
                        return true;
                    case R.id.navigation_login:
                        showLoginDialog();
                        return true;
                }
                return false;
            };

    /**
     * Eventos SwipeRefreshLayout
     */
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener
            = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {

        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener
            = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.i(TAG,"PAGE_MAIN Page: "+position);
            switch (position){
                case MainPagerAdapter.PAGE_MAIN:
                    setSelectedItem(R.id.navigation_home);
                    //showMenuCatalogo(false);
                    break;
                case MainPagerAdapter.PAGE_ASESORA:
                    setSelectedItem(R.id.navigation_asesora);
                    //showMenuCatalogo(false);
                    break;
                case MainPagerAdapter.PAGE_ATENCION:
                    setSelectedItem(R.id.navigation_atencion);
                    //showMenuCatalogo(false);
                    break;
                case MainPagerAdapter.PAGE_CATALOGOS:
                    setSelectedItem(R.id.navigation_catalogos);
                    //showMenuCatalogo(true);
                    break;
                case MainPagerAdapter.PAGE_LOGIN:
                    setSelectedItem(R.id.navigation_login);
                    //showMenuCatalogo(false);
                    break;
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void changePage(int r_id){
        if (r_id == R.id.navigation_home) {
            if (mViewPager.getCurrentItem() != MainPagerAdapter.PAGE_MAIN) {
                mViewPager.setCurrentItem(MainPagerAdapter.PAGE_MAIN);

            }
        } else if (r_id == R.id.navigation_asesora || r_id == R.id.imgVuelveteAsesora) {
            if (mViewPager.getCurrentItem() != MainPagerAdapter.PAGE_ASESORA) {
                mViewPager.setCurrentItem(MainPagerAdapter.PAGE_ASESORA);

            }
        } else if (r_id == R.id.navigation_atencion || r_id == R.id.imgAtencionCliente) {
            if (mViewPager.getCurrentItem() != MainPagerAdapter.PAGE_ATENCION) {
                mViewPager.setCurrentItem(MainPagerAdapter.PAGE_ATENCION);

            }
        } else if (r_id == R.id.navigation_catalogos || r_id == R.id.imgCatalogos) {
            if (mViewPager.getCurrentItem() != MainPagerAdapter.PAGE_CATALOGOS) {
                mViewPager.setCurrentItem(MainPagerAdapter.PAGE_CATALOGOS);

            }
        }
    }

    MH_Dialogs_Login showLogin;
    public void showLoginDialog() {
        showLogin = MH_Dialogs_Login.newInstance();
        showLogin.show(getSupportFragmentManager(), "fragment_edit_name");
    }

    public void registerBroadcat(){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                localBroadcastReceiver,
                new IntentFilter(MH_Dialogs_Login.BROACAST_LOGIN));
    }

    public void unregisterBroadcat(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                localBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        validaPermisos();
        super.onResume();
        registerBroadcat();
        Log.i(TAG,"onResume()");


        //Actualiza version app firebase
        /*PackageInfo packageInfo;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            version = packageInfo.versionCode;
        } catch (Exception e){
            e.printStackTrace();
        }

        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build());
        HashMap<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("versioncode",version);
        Task<Void> fetch = remoteConfig.fetch(0);
        fetch.addOnSuccessListener(this,aVoid -> {
            remoteConfig.activateFetched();
            version(version);
        } );*/
    }


    /*private void version(int version) {
        int    versioncode      = (int) remoteConfig.getLong("versioncode");
        String urlplaystore     = remoteConfig.getString("urlplaystore");
        String versionname      = remoteConfig.getString("versioname");
        if (versioncode>version){
            new actualizarApp(this,urlplaystore);
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBroadcat();
    }

    private BroadcastReceiver localBroadcastReceiver;
    private class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // safety check
            if (intent == null || intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case MH_Dialogs_Login.BROACAST_LOGIN:
                    switch (intent.getStringExtra(MH_Dialogs_Login.BROACAST_LOGIN)) {
                        case MH_Dialogs_Login.BROACAST_LOGIN_BTNENTRAR:
                            Log.i(TAG, "BROACAST_LOGIN_ENTRAR");
                            //startActivity(new Intent(MainActivity.this, MenuActivity.class));
                            break;
                        case MH_Dialogs_Login.BROACAST_LOGIN_BTNFORGOT:
                            Log.i(TAG, "BROACAST_LOGIN_FORGOT");
                            showLogin.gotoPage(AuthenticatePagerAdapter.PAGE_FORGOT);
                            break;
                        case MH_Dialogs_Login.BROACAST_LOGIN_EXIT:
                            Log.i(TAG, "BROACAST_LOGIN_EXIT");
                            setSelectedItem(oldItem);
                            break;

                    }
                break;

            }

        }
    }

    /**
     * METODOS DE RESPUESTAS HTTP
     */
    public void successfulAuth(DataAuth responseAuth){

        msgToast(responseAuth.getStatus());

        System.out.println("Mi informacion "+responseAuth.getPerfil().get(0).getValor());
        mPreferences.setJSON_TypePerfil(MainActivity.this, responseAuth.getPerfil().get(0));
        mPreferences.setLoggedIn(MainActivity.this, true);

        startActivity(new Intent(MainActivity.this, MenuActivity.class));
        finish();
    }
    public void successfulNotifyForgot(GenericDTO responseGeneric){
        msgToast(responseGeneric.getResult());
        showLogin.gotoPage(AuthenticatePagerAdapter.PAGE_CODE);
    }
    public void successfulValidateCode(GenericDTO responseGeneric, String codigo){
        msgToast(responseGeneric.getResult());
        showLogin.gotoPage(AuthenticatePagerAdapter.PAGE_PASSWORD);
        mPreferences.setCodeSMS(MainActivity.this, codigo);
    }
    public void successfulNewPwd(GenericDTO responseGeneric){
        msgToast(responseGeneric.getResult());
        showLogin.gotoPage(AuthenticatePagerAdapter.PAGE_LOGIN);
    }

    private void msgToast(String msg){
        Log.e("Toast", msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

        
    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            return true;
        }
        //Si cuenta con permisos de camara y almacenamiento
        if((checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},100);

        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},100);
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]== PackageManager.PERMISSION_GRANTED){
            }
        }
    }

}
