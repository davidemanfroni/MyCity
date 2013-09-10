/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String tags = "***********Servizio GCM*******";

    public GCMIntentService() {
        super(Constant.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {  
        
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        //su commonUtilities cè il metodo per mandare un messaggio broadcast sull'activity corrente.
        //l'activity la riceve con il brodcast receiver
        //nell'intent bisogna fare un get Extras() con chiave per tirare fuori il valore.
        //nel back office deve mettere nel campo data la coppia chiave/valore
        String action = intent.getExtras().getString("action");
        String message = intent.getExtras().getString("message");
        String tipoView = intent.getExtras().getString("tipoview");
        String tabella = intent.getExtras().getString("tabella");
        String idcontenuto = intent.getExtras().getString("idcontenuto");
        String url = Memory.getUrlRadice(context);
        url = Network.addAction(url, Integer.valueOf(action));
        url += "%26tabella%3D" + tabella;
        url = Network.addUidLanguageServerTime(url, context);
        url += "%26idcontenuto%3D" + idcontenuto;
        generateNotification(context, message, Integer.valueOf(tipoView), url);
        Log.i("DEBUG", url);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
    }

    @Override
    public void onError(Context context, String errorId) {
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return false;

    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message, int tipoview, String url) {
        int icon = R.drawable.ic_launcher_mycity;
        long when = System.currentTimeMillis();
        String title = Constant.APP_NAME;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = null;
        notificationIntent = Utility.getIntentFromId(tipoview, context);
        if(notificationIntent == null){
            notificationIntent = new Intent(context, HomeActivity.class);
        }    
        notificationIntent.putExtra(Key.KEY_URL_CONTENUTO, url);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(context).setContentTitle(title).setContentIntent(intent).
                setWhen(when).setContentText(message).setSmallIcon(icon).
                setDefaults(Notification.DEFAULT_SOUND).getNotification();
        notification.ledOffMS = 200;
        notification.ledOnMS = 200;
        notification.ledARGB = 0xE95A1A;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
