/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Utiliy;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import com.EGaspari.Mobile.Android.MyCity.R;
import java.util.List;

/**
 *
 * @author Davide
 */
public class Memory {
    public static final int ID_LANGUAGE_ITA = 1;
    public static final int ID_LANGUAGE_EN = 2;
    public static final int ID_LANGUAGE_DE = 3;
    public static final int ID_LANGUAGE_FR = 4;
    public static final int ID_LANGUAGE_ES = 5;
    public static final int ID_LANGUAGE_DEFAULT = -1;
    
    public static String getUrlRadice(Context context){
        SharedPreferences pref = context.getSharedPreferences(Key.APPLICATION_KEY_STORAGE, context.MODE_PRIVATE);
        return pref.getString(Key.KEY_URL_RADICE, "");
    }
    
    public static void setUrlRadice(Context applicationContext, String response) {
        SharedPreferences.Editor edit = applicationContext.getSharedPreferences(Key.APPLICATION_KEY_STORAGE, Context.MODE_PRIVATE).edit();
        edit.putString(Key.KEY_URL_RADICE, response);
        edit.commit();
    }
    
    public static String getUid(Context context){
         return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }
    
    public static int getLanguage(Context context){
        int lang = context.getSharedPreferences(Key.APPLICATION_KEY_STORAGE, Context.MODE_PRIVATE).getInt(Key.LANGUAGE_KEY_STORAGE, ID_LANGUAGE_DEFAULT);
        return lang;
    }
    
    public static void setLanguage(Context context, int lang){
        SharedPreferences.Editor edit = context.getSharedPreferences(Key.APPLICATION_KEY_STORAGE, Context.MODE_PRIVATE).edit();
        edit.putInt(Key.LANGUAGE_KEY_STORAGE, lang);
        edit.commit();
    }
    
    public static String getUrlByTag(Context context, String key){
        String url = context.getSharedPreferences(Key.APPLICATION_KEY_STORAGE, Context.MODE_PRIVATE).getString(key, "");
        return url;
    }
    
    public static void setUrlByTag(Context context, String key, String url){
        SharedPreferences.Editor edit = context.getSharedPreferences(Key.APPLICATION_KEY_STORAGE, Context.MODE_PRIVATE).edit();
        edit.putString(key, url);
        edit.commit();
    }

    public static void saveMenuDrawerItem(List<Object> list_home_menu, Context applicationContext) {
        /*
         * Protocolt: 
         * Line : Command; url; id_view; titolo; id; id_icon
         */
        String saving = "";
        saving += "home; ;-1;HOME;" + String.valueOf(R.drawable.menu_home);
        
    }
    
}
