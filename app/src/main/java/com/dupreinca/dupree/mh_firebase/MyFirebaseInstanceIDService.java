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



import android.util.Log;

import com.dupreinca.dupree.mh_http.Http;
import com.dupreinca.dupree.mh_required_api.RequiredRefreshToken;
import com.dupreeinca.lib_api_rest.model.view.Profile;
import com.dupreinca.dupree.mh_utilities.mPreferences;
import com.google.firebase.iid.FirebaseInstanceId;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);

        String refreshedToken = s;


        System.out.println("El refresh token"+s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(s);

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]

    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        mPreferences.setTokenFirebase(getApplicationContext(), token);

        if(mPreferences.isLoggedIn(getApplicationContext())) {/// solo si esta logueado, actualiza su token en la nube
            Log.d(TAG, "Sending token: " + token);

            String jsonPerfil = mPreferences.getJSON_TypePerfil(getApplicationContext());
            Profile perfil = new Gson().fromJson(jsonPerfil, Profile.class);

            new Http(getApplicationContext()).refreshTokenFCM(new RequiredRefreshToken(
                    token,
                    perfil.getValor()
            ));
        }
    }
}