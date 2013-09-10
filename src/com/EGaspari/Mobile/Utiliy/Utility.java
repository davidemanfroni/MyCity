/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Utiliy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import com.EGaspari.Mobile.Android.MyCity.DettaglioActivity;
import com.EGaspari.Mobile.Android.MyCity.ElencoActivity;
import com.EGaspari.Mobile.Android.MyCity.GalleryActivity;
import com.EGaspari.Mobile.Android.MyCity.HomeActivity;
import com.EGaspari.Mobile.Android.MyCity.MappaActivity;
import com.EGaspari.Mobile.Android.MyCity.MenuActivity;
import com.EGaspari.Mobile.Android.MyCity.QrCodeActivity;
import com.EGaspari.Mobile.Android.MyCity.SegnalazioneActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author Davide
 */
public class Utility {

    public static final int ID_VIEW_MENU_1 = 1;
    public static final int ID_VIEW_ELENCO = 3;
    public static final int ID_VIEW_DETTAGLIO = 5;
    public static final int ID_VIEW_GALLERY = 6;
    public static final int ID_VIEW_NEWS = 7;

    public static Typeface HelveticaNeueNormal(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/HelveticaNeueNormal.otf");
    }

    public static Typeface HelveticaNeueBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/HelveticaNeueBold.otf");
    }

    public static Typeface ClearfaceGothicMedium(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "font/ClearfaceGothicMedium.otf");
    }

    public static Intent getIntentFromId(int id, Context context) {
        Intent i = null;
        switch (id) {
            case ID_VIEW_MENU_1:
                i = new Intent(context, MenuActivity.class);
                break;
            case ID_VIEW_ELENCO:
                i = new Intent(context, ElencoActivity.class);
                i.putExtra(Key.KEY_ELENCO_SELECTOR_VIEW, Constant.ELENCO_ATTIVITA);
                break;
            case ID_VIEW_GALLERY:
                i = new Intent(context, GalleryActivity.class);
                break;
            case ID_VIEW_NEWS:
                i = new Intent(context, ElencoActivity.class);
                i.putExtra(Key.KEY_ELENCO_SELECTOR_VIEW, Constant.ELENCO_ATTIVITA);
                break;
            case ID_VIEW_DETTAGLIO:
                i = new Intent(context, DettaglioActivity.class);
                break;
        }
        return i;
    }

    public static Intent getIntentFromCommand(String comando, Context context) {
        Intent i = null;
        if (comando.equals(Key.KEY_COMANDO_MAPPA)) {
            i = new Intent(context, MappaActivity.class);
        } else if (comando.equals(Key.KEY_COMANDO_SEGNALAZIONE)) {
            i = new Intent(context, SegnalazioneActivity.class);
        } else if (comando.equals(Key.KEY_COMANDO_QR_CODE)) {
            i = new Intent(context, QrCodeActivity.class);
        } else if (comando.equals(Key.KEY_COMANDO_IMPOSTAZIONI)) {
            i = new Intent(context, ElencoActivity.class);
            i.putExtra(Key.KEY_ELENCO_SELECTOR_VIEW, Constant.ELENCO_IMPOSTAZIONI);
        } else if (comando.equals(Key.KEY_COMANDO_HOME)) {
            i = new Intent(context, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return i;
    }

    public static Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            final int REQUIRED_SIZE = 600;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static String toUpperCaseFirstLetter(String parola) {
        String firstLetter = parola.substring(0, 1);
        String lastLetter = parola.substring(1);
        return firstLetter.toUpperCase() + lastLetter.toLowerCase();
    }

    public static String tagliaStringa(String s) {
        int a = s.indexOf(">");
        s = s.substring(a + 1);
        int b = s.indexOf(">");
        s = s.substring(b + 1);
        int c = s.indexOf("<");
        s = s.substring(0, c);
        return s;
    }
}
