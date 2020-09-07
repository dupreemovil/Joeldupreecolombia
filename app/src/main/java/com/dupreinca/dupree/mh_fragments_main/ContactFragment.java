package com.dupreinca.dupree.mh_fragments_main;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.R;
import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredVisit;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.google.gson.Gson;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private final String TAG = "ContactFragment";

    public ContactFragment() {
        // Required empty public constructor
    }
    public long timeinit=0;
    public long timeend=0;
    public String userid="";

    Profile perfil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);
        WebView webView = (WebView) v.findViewById(R.id.webView);
        //webView.loadUrl("https://azzorti.bo");

        timeinit =System.currentTimeMillis();

        perfil = getPerfil();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //progDailog.show();
                Log.e(TAG,"Loading");
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                //progDailog.dismiss();
                Log.e(TAG,"dismiss");
            }
        });

        String url = mPreferences.getUrlEmbeddedChat(getActivity());
        webView.loadUrl(url != null && !url.isEmpty() ? url : "https://azzorti.bo/dupreeWS/chatAPP/chat.html");



        return v;
    }

    public static ContactFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Profile getPerfil(){
        String jsonPerfil = mPreferences.getJSON_TypePerfil(getActivity());
        if(jsonPerfil!=null)
            return new Gson().fromJson(jsonPerfil, Profile.class);

        return null;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG,"onDestroy()");

        if(perfil!=null){

            timeend = System.currentTimeMillis();
            long finaltime= timeend-timeinit;
            int timesec = (int)finaltime/1000;

            RequiredVisit req = new RequiredVisit(perfil.getValor(),Integer.toString(timesec),"quejas");
            //   System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);


        }
        else{
            timeend = System.currentTimeMillis();
            long finaltime= timeend-timeinit;
            int timesec = (int)finaltime/1000;

            RequiredVisit req = new RequiredVisit("",Integer.toString(timesec),"quejas");
            //   System.out.println("Se destruyo bandeja"+Long.toString(finaltime) + " para "+perfil.getValor());

            new Http(getActivity()).Visit(req);


        }


        super.onDestroy();
    }

}
