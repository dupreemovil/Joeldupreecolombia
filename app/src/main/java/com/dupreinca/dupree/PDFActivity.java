package com.dupreinca.dupree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dupreinca.dupree.R;

public class PDFActivity extends AppCompatActivity {

    private final String TAG = "PDFActivity";
    public static String URL_FILE = "url_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        String pdfURL = getIntent().getStringExtra(URL_FILE);

        WebView webViewPDF = (WebView) findViewById(R.id.webViewPDF);

        webViewPDF.getSettings().setJavaScriptEnabled(true);


        webViewPDF.getSettings().setPluginState(WebSettings.PluginState.ON);

        webViewPDF.getSettings().setLoadWithOverviewMode(true);
        webViewPDF.getSettings().setUseWideViewPort(true);
        webViewPDF.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //progDailog.show();
                Log.e(TAG,"Loading");
                view.loadUrl(url);

                return false;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                //progDailog.dismiss();
                Log.e(TAG,"dismiss");
            }
        });

        //String pdfURL = "https://intranet.dupree.co/temporales/9624785.pdf";
        webViewPDF.loadUrl(
                "http://docs.google.com/gview?embedded=true&url=" + pdfURL);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
