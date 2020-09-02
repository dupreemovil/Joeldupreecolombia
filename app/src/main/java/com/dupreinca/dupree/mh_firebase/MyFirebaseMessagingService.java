package com.dupreinca.dupree.mh_firebase;

/**
 * Created by cloudemotion on 20/9/17.
 */

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, ResponseVersion 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.dupreinca.dupree.FullscreenActivity;
import com.dupreinca.dupree.R;
//import com.firebase.jobdispatcher.Constraint;
//import com.firebase.jobdispatcher.FirebaseJobDispatcher;
//import com.firebase.jobdispatcher.GooglePlayDriver;
//import com.firebase.jobdispatcher.Job;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        int size = remoteMessage.getData().size();
        Log.e("FIREBASE", data + " - " + String.valueOf(size));

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        System.out.println("Mensaje recibido de "+remoteMessage.getFrom());

        System.out.println("Mensaje Contenido "+remoteMessage.getNotification().getBody());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String pedido = remoteMessage.getData().get(ConstantsFirebase.KEY_PEDIDO);
            String version = remoteMessage.getData().get(ConstantsFirebase.KEY_VERSION);

            if(pedido != null && version !=null){
                /*if(pedido.equals(ConstantsFirebase.VALUE_BANNER_PDF)) {
                    updateBannerAndPDF();
                } else if(pedido.equals(ConstantsFirebase.VALUE_ACTUALIZAR_CATALOGO)) {
                    updateBannerAndPDF();//temporalmente es la misma
                }*/
                //si es una nueva version, solo en ese caso actualizar
                if((!version.equals(mPreferences.getVersionCatalogo(getApplicationContext()))) && (pedido.equals(ConstantsFirebase.VALUE_BANNER_PDF) || pedido.equals(ConstantsFirebase.VALUE_ACTUALIZAR_CATALOGO))){

                    updateBannerAndPDF();
                }
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    /*
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

*/
    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {

        System.out.println("Se inicio firebasemessage");
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, FullscreenActivity
                .class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.desea_ver_catalogo);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Dupree")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void updateBannerAndPDF(){
        //esto hace que se actualice los catalogos al cerrar y abrir la app.
        //mPreferences.setCampanaActual(campanaActualServer, getApplicationContext());// se respalda la campañ actual
        //mPreferences.setChangeCampana(getApplicationContext(), true);
        mPreferences.setUpdateBannerAndPDF(getApplicationContext(), true);

        generateNotificationsPush("Lo invitamos a descargar la versión mas reciente de nuestro catálogo de productos");

        //sendNotification("Hay un nuevo catalogo disponible");
    }



    Intent moreInfoIntent;
    public void generateNotificationsPush(String campanaActualServer){
        moreInfoIntent=new Intent(this, FullscreenActivity.class);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        new MH_ManagerNotificacion().showNotificationCompat(
                getApplicationContext(),
                1,
                moreInfoIntent,
                R.drawable.ic_file_download_white_24dp,
                bm,
                "Actualizacion",
                //getResources().getString(R.string.app_name),
                campanaActualServer
                //getResources().getString(R.string.new_chat)
        );
    }

}
