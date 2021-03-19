package com.dupreinca.dupree;

import android.app.Application;
import android.content.Context;

import androidx.multidex.*;

import com.crashlytics.android.Crashlytics;
import com.indigitall.android.Indigitall;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by marwuinh@gmail.com on 5/9/17.
 */
//MultiDexApplication
public class BaseAPP extends Application {

    private static final String TAG = BaseAPP.class.getName();
    private static Context context;


    private static RealmConfiguration context1;
    private static String campana;

    public String fcm = "625076487360";

    public String appkey = "1cddb358-b37b-46b3-b295-074f36172b41";

    public String getCampana() {

        return campana;
    }

    public void setCampana(String campana) {
        campana = campana;
    }

    public static Context getContext() {
        return context;
    }

    public static RealmConfiguration getContext1() {
        return context1;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = getApplicationContext();



        System.out.println("Se inicio indigital");

        Indigitall.init(this, appkey, fcm);

        // The default Realm file is "default.realm" in Context.getFilesDir();
        // we'll change it to "myrealm.realm"
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(3)
                .deleteRealmIfMigrationNeeded()// si hay un cambio en la estructura de la base de
                // datos, la elimina, tras una actualizacion de la app
                .name("myrealm2.realm")
                .build();

        context1 = config;

        Realm.setDefaultConfiguration(config);


    }
}
