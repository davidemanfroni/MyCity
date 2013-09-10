package com.EGaspari.Mobile.Android.MyCity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.ImageView;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Network;
import com.google.android.gcm.GCMRegistrar;

public class SplashActivity extends FragmentActivity {


    private ImageView splash;
    private AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.splash);
            splash = (ImageView) findViewById(R.id.splash);
            splash.setScaleType(ImageView.ScaleType.FIT_XY);
            if (Network.isNetworkAvaliable(getApplicationContext())) {
                GCMRegistrar.checkDevice(getApplicationContext());
                GCMRegistrar.checkManifest(getApplicationContext());
                download();
            } else {
                ErrorHelper.showNetworkError(SplashActivity.this);
            }
        } catch (Exception ex) {
        }
    }

    private void download() {
        mRegisterTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                GCMRegistrar.register(getApplicationContext(), Constant.SENDER_ID);
                Network.downloadUrlRadice(getApplicationContext());
                Network.registrationPush(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                startNextActivity();
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void startNextActivity() {
        Intent intent;
        intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
