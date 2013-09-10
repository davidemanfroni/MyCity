/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Utiliy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;

/**
 *
 * @author Davide
 */
public class Network {

    public static String downloadUrlRadice(Context context) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(Constant.URL_START).openConnection();
            conn.setUseCaches(true);
            InputStream in = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(in)));
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            String s = "";
            s += sb.toString();
            int a = s.indexOf(">");
            s = s.substring(a + 1);
            int b = s.indexOf(">");
            s = s.substring(b + 1);
            int c = s.indexOf("<");
            s = s.substring(0, c);
            s += "galservice.asmx/GetUrlCommand?urlcommand=%3F";
            if (!s.equalsIgnoreCase(Memory.getUrlRadice(context))) {
                Memory.setUrlRadice(context, s);
            }
            return s;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String setUrlWithCodSceltaCombo(int[] ids, String url) {
        if (ids.length > 0) {
            String param = "";
            for (int i = 0; i < ids.length; i++) {
                param += String.valueOf(ids[i]) + ",";
            }
            param = param.substring(0, param.length() - 1);
            url = url.replace("codsceltacombo%3D", "codsceltacombo%3D" + param);
            return url;
        } else {
            return url;
        }
    }

    public static String addIdComune(String url, int id) {
        url += "%26idcomune%3D" + id;
        return url;
    }

    public static String setUrlWithText(String search, String url) {
        url = url.replace("stringaricerca%3D", "stringaricerca%3D" + search);
        return url;
    }

    public static String addUidLanguageServerTime(String url, Context context) {
        int l = Memory.getLanguage(context);
        if (l == -1) {
            l = 1;
        }
        url += "%26uid%3D" + GCMRegistrar.getRegistrationId(context) + "%26lingua%3D" + l + "%26servertime%3D" + Memory.getUid(context);
        return url;
    }

    public static String addAction(String response, int i) {
        response += "action%3D" + String.valueOf(i);
        return response;
    }



    public static JSONArray getJSONArray(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setUseCaches(true);
            InputStream in = conn.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(new DoneHandlerInputStream(in)));
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }
            String s = sb.toString();
            s = Utility.tagliaStringa(s);
            
            return new JSONArray(s);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Bitmap downloadBitmap(String fileUrl) throws IOException {
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setUseCaches(true);
            conn.connect();
            is = conn.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (MalformedURLException e) {
            Log.i("DEBUG", e.getMessage() + "\n" + e.getCause());
        } catch (IOException e) {
             Log.i("DEBUG", e.getMessage() + "\n" + e.getCause());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static Bitmap downloadSmallBitmap(String fileUrl) throws IOException {
        InputStream is = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
            conn.setUseCaches(true);
            conn.connect();
            is = conn.getInputStream();
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(is, null, o);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static boolean isNetworkAvaliable(Context context) {
        boolean isAvaliable = true;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo.getState() != NetworkInfo.State.CONNECTED && wifiInfo.getState() != NetworkInfo.State.CONNECTED) {
            isAvaliable = false;

        }
        return isAvaliable;
    }

    public static void registrationPush(Context context) {
        String urlRegistration = Memory.getUrlRadice(context);
        urlRegistration = Network.addAction(urlRegistration, Constant.ID_SET_REGISTRATION_PUSH);
        urlRegistration = Network.addUidLanguageServerTime(urlRegistration, context);
        Network.getJSONArray(urlRegistration);
    }
}
