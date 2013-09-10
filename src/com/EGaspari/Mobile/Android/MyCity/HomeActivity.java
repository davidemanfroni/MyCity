/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.View;
import com.EGaspari.Mobile.Fragment.HomeNewsFragment;
import com.EGaspari.Mobile.Fragment.MenuFragment;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Davide
 */
public class HomeActivity extends HeaderFragmentActivity {

    private AsyncTask<Void, Void, Void> mRegisterTask;
    private String url_menu, url_news, url_comuni, url_ricerca;
    private HomeNewsFragment home_news_fragment;
    private MenuFragment menu_fragment;
    private View progress_dialog;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);
        super.setMenuView(R.layout.home);
        progress_dialog = super.findView(R.id.loading_spinner);
        progress_dialog.setVisibility(View.VISIBLE);
        if (Network.isNetworkAvaliable(HomeActivity.this)) {
            if (Memory.getLanguage(getApplicationContext()) == Memory.ID_LANGUAGE_DEFAULT) {
                Intent i = new Intent(getApplicationContext(), ElencoActivity.class);
                i.putExtra(Key.KEY_ELENCO_SELECTOR_VIEW, Constant.ELENCO_IMPOSTAZIONI);
                i.putExtra(Key.KEY_ID_CONTENUTO, 1);
                startActivity(i);
                finish();
            } else {
                downloadContent();
            }

        } else {
            ErrorHelper.showNetworkError(HomeActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction().remove(menu_fragment);
        getSupportFragmentManager().beginTransaction().remove(home_news_fragment).commit();
        this.finish();
        super.onBackPressed();
    }

    private void downloadContent() {
        mRegisterTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    String url = Memory.getUrlRadice(getApplicationContext());
                    url = Network.addAction(url, 1);
                    url = Network.addUidLanguageServerTime(url, getApplicationContext());
                    JSONArray jsonArray = Network.getJSONArray(url);
                    url_menu = jsonArray.getJSONObject(0).getString(Key.KEY_URL_CONTENUTO);
                    url_ricerca = jsonArray.getJSONObject(0).getString(Key.KEY_URL_FILTRI);
                    url_news = jsonArray.getJSONObject(1).getString(Key.KEY_URL_CONTENUTO);
                    //url_comuni = jsonArray.getJSONObject(2).getString(Key.KEY_URL_CONTENUTO);




                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (!url_menu.equals("") || !url_ricerca.equals("") || !url_news.equals("")) {
                    saveUrls();
                    setMenuDrawerView();
                    menuDrawer.invalidate();
                    menuList.invalidate();
                    manageFragment();
                    progress_dialog.setVisibility(View.GONE);
                }
            }
        };
        mRegisterTask.execute(null, null, null);
    }

    private void manageFragment() {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            menu_fragment = (MenuFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_MENU);
            home_news_fragment = (HomeNewsFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_HOME_NEWS);
            if (menu_fragment == null) {
                menu_fragment = new MenuFragment(url_menu);
                ft.addToBackStack(null);
            ft.add(R.id.menu_layout, menu_fragment, Constant.FRAGMENT_MENU);
            }
            
            if (home_news_fragment == null) {
                home_news_fragment = new HomeNewsFragment(url_news);
                ft.addToBackStack(null);
            ft.add(R.id.home_news_layout, home_news_fragment, Constant.FRAGMENT_HOME_NEWS);
            }
            
            ft.commit();
        } catch (Exception ex) {
            ErrorHelper.showFatalError(HomeActivity.this);
        }
    }

    private void saveUrls() {
        AsyncTask<Void, Void, Void> mSaver = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    SharedPreferences.Editor pref = getSharedPreferences(Key.APPLICATION_KEY_STORAGE + "_" + Key.MENU_DRAWER_ITEMS, MODE_PRIVATE).edit();
                    JSONArray jarray = Network.getJSONArray(url_menu);
                    pref.putInt(Key.KEY_SIZE, jarray.length());
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject jobject = null;
                        try {
                            jobject = jarray.getJSONObject(i);
                            String nome = Utility.toUpperCaseFirstLetter(jobject.getString(Key.KEY_TITOLO));
                            String url = jobject.getString(Key.KEY_URL_CONTENUTO);
                            String comando = jobject.getString(Key.KEY_COMANDO);
                            String id_view = jobject.getString(Key.KEY_VIEW_SUCCESSIVA);
                            Bitmap bitmap = Network.downloadBitmap(jobject.getString(Key.KEY_URL_CONTENUTO_2));
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            String ba1 = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                            pref.putString(Key.KEY_TITOLO + String.valueOf(i), nome);
                            pref.putString(Key.KEY_URL_CONTENUTO + String.valueOf(i), url);
                            pref.putString(Key.KEY_COMANDO + String.valueOf(i), comando);
                            pref.putString(Key.KEY_VIEW_SUCCESSIVA + String.valueOf(i), id_view);
                            pref.putString(Key.KEY_URL_CONTENUTO_2 + String.valueOf(i), ba1);
                        } catch (Exception ex) {
                            Logger.getLogger(HomeActivity.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    pref.putString(Key.KEY_URL_FILTRI, url_ricerca);
                    pref.commit();
                    /*SharedPreferences.Editor pref_comuni = getSharedPreferences(Key.MULTI_COMUNE_ITEMS, MODE_PRIVATE).edit();
                     JSONArray jarray_comuni = Network.getJSONArray(url_comuni);
                     pref_comuni.putInt(Key.KEY_SIZE, jarray_comuni.length());
                     for (int i = 0; i < jarray_comuni.length(); i++) {
                     JSONObject jobject = null;
                     try {
                     jobject = jarray_comuni.getJSONObject(i);
                     String nome = Utility.toUpperCaseFirstLetter(jobject.getString(Key.KEY_TITOLO));
                     String url = jobject.getString(Key.KEY_URL_CONTENUTO);
                     String comando = jobject.getString(Key.KEY_COMANDO);
                     String id_view = jobject.getString(Key.KEY_VIEW_SUCCESSIVA);
                     Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_mycity);
                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                     byte[] bitmapdata = bos.toByteArray();
                     String ba1 = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                     pref_comuni.putString(Key.KEY_TITOLO + String.valueOf(i), nome);
                     pref_comuni.putString(Key.KEY_URL_CONTENUTO + String.valueOf(i), url);
                     pref_comuni.putString(Key.KEY_COMANDO + String.valueOf(i), comando);
                     pref_comuni.putString(Key.KEY_VIEW_SUCCESSIVA + String.valueOf(i), id_view);
                     pref_comuni.putString(Key.KEY_URL_CONTENUTO_2 + String.valueOf(i), ba1);
                     } catch (Exception ex) {
                     Logger.getLogger(HomeActivity.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     }
                     pref_comuni.commit();*/
                } catch (Exception ex) {
                }
                return null;
            }
        };
        mSaver.execute(null, null, null);
    }
}
