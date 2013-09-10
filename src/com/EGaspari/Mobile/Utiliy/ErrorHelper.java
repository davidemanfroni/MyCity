/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Utiliy;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.EGaspari.Mobile.Fragment.ErrorFragment;

/**
 *
 * @author Davide
 */
public class ErrorHelper {

    public static final int id_error_network = 0;

    public static void showNetworkError(FragmentActivity context) {
        int id_message = R.string.errore_connessione_it;
        switch (Memory.getLanguage(context)) {
            case Memory.ID_LANGUAGE_ITA:
                id_message = R.string.errore_connessione_it;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_message = R.string.errore_connessione_de;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_message = R.string.errore_connessione_es;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_message = R.string.errore_connessione_en;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_message = R.string.errore_connessione_fr;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_message = R.string.errore_connessione_it;
                break;
        }       
        showError(context.getString(id_message), context);
    }
    
    public static void showFatalError(FragmentActivity context){
        int id_message = R.string.errore_connessione_it;
        switch (Memory.getLanguage(context)) {
            case Memory.ID_LANGUAGE_ITA:
                id_message = R.string.general_error_ita;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_message = R.string.general_error_de;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_message = R.string.general_error_es;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_message = R.string.general_error_en;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_message = R.string.general_error_fr;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_message = R.string.general_error_ita;
                break;
        }       
        showError(context.getString(id_message), context);
    }

    private static void showError(String message, FragmentActivity context) {
        FragmentTransaction ft = context.getSupportFragmentManager().beginTransaction();
        Fragment prev = context.getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_DIALOG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ErrorFragment newFragment = new ErrorFragment(message);
        newFragment.show(ft, Constant.FRAGMENT_DIALOG);
    }
}
