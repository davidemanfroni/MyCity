/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Utiliy;

import android.view.View;

/**
 *
 * @author Davide
 */
public class Constant {
    public static final String URL_START = "http://www.mycitymobile.it/RedirService.asmx/GetUrlCommand?urlcommand=%3Fpar1%3D999";
    public static final String SENDER_ID = "278397883670";
    public static final int CAMERA_INTENT_REQUEST_CODE = 10;
    public static final int LOAD_IMAGE_INTENT_RQUEST_CODE = 20;
    public static final int QR_CODE_REQUEST_CODE = 100;
    public static String DATABASE_NAME = "MY_CITY_DB.db";
    public static String TABLE_QRCODE_NAME = "MY_CITY_QR_CODE_TABLE";
    public static String APP_NAME = "MyCity";
    public static final int ELENCO_ATTIVITA = 1;
    public static final int ELENCO_SEGNALAZIONI = 2;
    public static final int ELENCO_IMPOSTAZIONI = 3;
    public static final int ELENCO_QR_CODE = 4;
    public static int ID_COMUNE = 1;
    public static final int ID_ACTION_PREFERENZE = 50;
    public static final int ID_SET_IMPOSTAZIONI = 151;
    public static final int ID_SET_SEGNALAZIONI = 150;
    public static final int ID_SET_REGISTRATION_PUSH = 152;
    public static final boolean COMMON_BUTTON_ENABLE = false;
    public static final int WEB_VIEW_REQUEST_CODE = 99;
    public static final int SEGNALAZIONE_APPROVATA = 2;
    public static final int SEGNALAZIONE_APPROVAZIONE_IN_CORSO = 1;
    public static final int SEGNALAZIONE_NON_APPROVATA = 3;
    public static final int VISIBILITY_COMUNE_SEGNALAZIONE = View.GONE;
    public static final String FRAGMENT_HOME_NEWS = "home_news";
    public static final String FRAGMENT_MENU = "menu";
    public static final String FRAGMENT_MAP = "map";
    public static final String FRAGMENT_DIALOG = "dialog";
    public static final String FRAGMENT_ATTIVITA = "attivita";
    public static final String FRAGMENT_QRCODE = "qrcode";
    public static final String FRAGMENT_SEGNALAZIONI = "segnalazioni";
    public static final String FRAGMENT_IMPOSTAZIONI = "impostazioni";
}
