package com.dupreinca.dupree;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;


import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredSms;
import com.dupreinca.dupree.mh_required_api.RequiredValida;
import com.dupreinca.dupree.mh_required_api.RequiredVersion;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dupreinca.dupree.mh_adapters.MainPagerAdapter;
import com.dupreinca.dupree.mh_adapters.AuthenticatePagerAdapter;
import com.dupreinca.dupree.mh_dialogs.MH_Dialogs_Login;
import com.dupreeinca.lib_api_rest.model.dto.response.DataAuth;
import com.dupreeinca.lib_api_rest.model.dto.response.GenericDTO;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.google.gson.Gson;
import com.google.rpc.context.AttributeContext;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApi;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;
import static com.dupreinca.dupree.BaseAPP.getContext;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "MCHD->MainActivity";

    ViewPager mViewPager;
    private final static int RESOLVE_HINT = 1011;
    private BottomNavigationView bottomNavigation;
    private static final int  REQUEST_ACCESS_FINE_LOCATION = 111;

    String act_cel="";
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

        act_cel = mPreferences.getActcel(getApplicationContext());



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

        System.out.println("Llamando a main" +act_cel);
        boolean hasPermissionLocation = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionLocation) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_ACCESS_FINE_LOCATION);
        }
        else{

        }


        String name_user=mPreferences.getNameUser(getApplicationContext());

        String ced_user=mPreferences.getCedUser(getApplicationContext());

        String tel_user=mPreferences.getTelUser(getApplicationContext());

        if(name_user!=null && ced_user!=null && tel_user!=null){
            if(name_user.length()==0 || ced_user.length()==0 || tel_user.length()==0)  {
                if(act_cel.contains("1")){
                    System.out.println("Enter name us"+act_cel);
                    showentername();
                }
                else{

                }

            }
            else{


            }
        }
        else{
            if(act_cel.contains("1")){
                System.out.println("Enter name sus"+act_cel);
                showentername();
            }
            else{

            }

        }






    }

    public void showentersms(){


        System.out.println("Enter name main");

        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View deleteDialogView = factory.inflate(R.layout.datos_sms, null);


        final android.app.AlertDialog deleteDialog = new AlertDialog.Builder(MainActivity.this).create();
        deleteDialog.setView(deleteDialogView);
        Rect displayRectangle = new Rect();
        Window window = MainActivity.this.getWindow();


        deleteDialog.setCancelable(false);


        EditText edtsms = (EditText)deleteDialogView.findViewById(R.id.datos_sms);


        Button btnnext = deleteDialogView.findViewById(R.id.btnnext);

        Button btnresend = deleteDialogView.findViewById(R.id.btnresend);

        btnresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cedula = mPreferences.getCedUser(MainActivity.this);
                String tel  = mPreferences.getTelUser(MainActivity.this);
                String name = mPreferences.getNameUser(MainActivity.this);

                RequiredValida req = new RequiredValida(cedula,tel,name);
              //  setSelectedItem(R.id.navigation_home);
                        new Http(MainActivity.this).validac1(req,MainActivity.this);

            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(edtsms.getText().length()>0 ){


                    String num_ide = mPreferences.getCedUser(MainActivity.this);


                    RequiredSms req = new RequiredSms(num_ide,edtsms.getText().toString());
                    new Http(MainActivity.this).validas(req,MainActivity.this);


                    deleteDialog.dismiss();

                }
                else{


                        edtsms.setError("Debe ingresar su Codigo");


                }


            }
        });

        deleteDialog.show();

        int width = (int)(displayRectangle.width() * 7/8);
        int heigth = (int)(displayRectangle.height() * 6/8);

        deleteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //  deleteDialog.getWindow().setLayout(width, heigth);

    }


    public void showentername(){

        System.out.println("Enter name main");

        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View deleteDialogView = factory.inflate(R.layout.datos_layout, null);


        final android.app.AlertDialog deleteDialog = new AlertDialog.Builder(MainActivity.this).create();
        deleteDialog.setView(deleteDialogView);
        Rect displayRectangle = new Rect();
        Window window = MainActivity.this.getWindow();


        deleteDialog.setCancelable(false);


        EditText edtname = (EditText)deleteDialogView.findViewById(R.id.datos_name);
        EditText edtced = (EditText)deleteDialogView.findViewById(R.id.datos_cedula);
        EditText edttel = (EditText)deleteDialogView.findViewById(R.id.datos_telefono);


        Button btnnext = deleteDialogView.findViewById(R.id.btnnext);

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(edtname.getText().length()>0 && edtced.getText().length()>0){


                    mPreferences.setCedUser(edtced.getText().toString(),MainActivity.this);
                    mPreferences.setNameUser(edtname.getText().toString(),MainActivity.this);
                    mPreferences.setTelUser(edttel.getText().toString(),MainActivity.this);

                    RequiredValida req = new RequiredValida(edtced.getText().toString(),edttel.getText().toString(),edtname.getText().toString());
                    new Http(MainActivity.this).validac(req,MainActivity.this);


                    deleteDialog.dismiss();

                }
                else{

                    if(edtname.getText().length()==0){
                        edtname.setError("Debe ingresar su Nombre");
                    }

                    if(edtced.getText().length()==0){

                        edtced.setError("Debe ingresar cedula");
                    }

                }


            }
        });

        deleteDialog.show();

        int width = (int)(displayRectangle.width() * 7/8);
        int heigth = (int)(displayRectangle.height() * 6/8);

        deleteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

      //  deleteDialog.getWindow().setLayout(width, heigth);

    }

    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)


                .build();
        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();
        PendingIntent intent = Credentials.getClient(getContext(), options)
                .getHintPickerIntent(hintRequest);

        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0,null);
        } catch (IntentSender.SendIntentException e) {
            Toast.makeText(getApplicationContext(),"Error "+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    /**
     * Selecciona una opcion en el menu inferior
     * @param idMenu
     */
    public void setSelectedItem(int idMenu) {
        if (bottomNavigation.getSelectedItemId()!=idMenu) {
            if(idMenu==R.id.navigation_login){

                if(act_cel.contains("1")){


                    String validsms = mPreferences.getValidSms(MainActivity.this);

                    if(validsms!=null){
                        if(validsms.length()==0){
                            String cedula = mPreferences.getCedUser(MainActivity.this);
                            String tel  = mPreferences.getTelUser(MainActivity.this);
                            String name = mPreferences.getNameUser(MainActivity.this);

                            RequiredValida req = new RequiredValida(cedula,tel,name);
                            setSelectedItem(R.id.navigation_home);
                            //        new Http(this).validac(req,this);
                            showentersms();

                        }
                        else{


                            if(validsms.contains("si")){

                                View view = bottomNavigation.findViewById(idMenu);
                                view.performClick();

                            }
                            else{
                                Toast.makeText(MainActivity.this,"No se valido el codigo sms",Toast.LENGTH_LONG).show();
                                String cedula = mPreferences.getCedUser(MainActivity.this);
                                String tel  = mPreferences.getTelUser(MainActivity.this);
                                String name = mPreferences.getNameUser(MainActivity.this);

                                RequiredValida req = new RequiredValida(cedula,tel,name);
                                setSelectedItem(R.id.navigation_home);
                                //      new Http(this).validac(req,this);

                                showentersms();


                            }

                        }
                    }
                    else{

                        String cedula = mPreferences.getCedUser(MainActivity.this);
                        String tel  = mPreferences.getTelUser(MainActivity.this);
                        String name = mPreferences.getNameUser(MainActivity.this);
                        setSelectedItem(R.id.navigation_home);
                        RequiredValida req = new RequiredValida(cedula,tel,name);

                        //  new Http(this).validac(req,this);
                        showentersms();

                    }
                }
                else{
                    View view = bottomNavigation.findViewById(idMenu);
                    view.performClick();
                }




            }
            else{
                View view = bottomNavigation.findViewById(idMenu);
                view.performClick();

            }



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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        System.out.println("El result connect "+connectionResult.getErrorMessage());
    }

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

        System.out.println("Mi informacion ced"+responseAuth.getPerfil().get(0).toString());


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


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]== PackageManager.PERMISSION_GRANTED){
            }
        }
        else if(requestCode==REQUEST_ACCESS_FINE_LOCATION){



        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {

                    System.out.println("El id phone "+credential.getId());
                    Toast.makeText(getApplicationContext(),"El id"+credential.getId(),Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),"Error Null",Toast.LENGTH_LONG).show();

                    System.out.println("El id phone null");
                }
            }
        }
    }

}
