package com.dupreinca.dupree.mh_fragments_login;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dupreinca.dupree.BuildConfig;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.mh_dialogs.MH_Dialogs_Login;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredAuth;
import com.dupreinca.dupree.mh_utilities.Validate;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthFragment extends Fragment {
    private final String TAG = "AuthFragment";

    private String tokenDevice=null;

    public AuthFragment() {
        // Required empty public constructor
    }

    public static AuthFragment newInstance() {
        Bundle args = new Bundle();

        AuthFragment fragment = new AuthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    EditText txtUsername, txtPwd;
    TextView txt_vers_name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_auth, container, false);


        txtUsername = (EditText) v.findViewById(R.id.txtUsername);
        txtPwd = (EditText) v.findViewById(R.id.txtPwd);
        txt_vers_name = (TextView) v.findViewById(R.id.txt_vers_name);
        Button btnLogin = (Button) v.findViewById(R.id.btnLogin);
        TextView tvForgot = (TextView) v.findViewById(R.id.tvForgot);

        //txtUsername.setText("1015434512"); //asesora
        //txtPwd.setText("1015434512");

        //txtUsername.setText("ana_yanquen");
        //txtPwd.setText("868Ac*4301");

        //txtUsername.setText("1015434512"); //Asesora
        //txtPwd.setText("1015434512");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getActivity(), MenuActivity.class));
                if(validateAuth()){
                    httpAuth();
                }
            }
        });
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gotoPage(PAGE_IDENTY);
                publishResult(MH_Dialogs_Login.BROACAST_LOGIN_BTNFORGOT);
            }
        });

        txt_vers_name.setText("Versión: "+getVersionName());
        return v;
    }

    public boolean validateAuth(){
        Validate valid=new Validate();
        if(txtUsername.getText().toString().isEmpty()){
            valid.setLoginError(getResources().getString(R.string.campo_requerido), txtUsername);
            return false;
        } else if(valid.isValidPwd(txtPwd.getText().toString())){
            valid.setLoginError(getResources().getString(R.string.clave_invalida), txtPwd);
            return false;
        }
        return true;
    }

    private void httpAuth(){
        tokenDevice = mPreferences.getTokenFirebase(getActivity());

        String fid = FirebaseInstallations.getInstance().getId().getResult().toString();
        if(tokenDevice==null) {//si no se ha generado un token nuevo
            tokenDevice = FirebaseInstanceId.getInstance().getToken();//se  obtiene el anterior
            Log.e("last tokenDevice","> "+tokenDevice);
        } else {
            Log.e("obtained tokenDevice","> "+tokenDevice);
        }

        System.out.println("El fid es "+fid);
        String versionName = BuildConfig.VERSION_NAME;
        new Http(getActivity()).Auth(new RequiredAuth(txtUsername.getText().toString(), txtPwd.getText().toString(), tokenDevice,fid,versionName));
    }

    private void publishResult(String object){
        Log.i(TAG,"publishResult: "+object);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
                new Intent(MH_Dialogs_Login.BROACAST_LOGIN)
                        .putExtra(MH_Dialogs_Login.BROACAST_LOGIN, object));
                        //.putExtra(MH_Dialogs_Login.BROACAST_LOGIN_DATA, data));

    }

    public String getVersionName(){
        Log.i("VERSION NAME", String.valueOf(BuildConfig.VERSION_NAME));
        return BuildConfig.VERSION_NAME;
    }

}
