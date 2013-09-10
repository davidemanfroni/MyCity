/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.EGaspari.Mobile.Fragment.AttivitaListFragment;
import com.EGaspari.Mobile.Fragment.ImpostazioniListFragment;
import com.EGaspari.Mobile.Fragment.QrCodeListFragment;
import com.EGaspari.Mobile.Fragment.SegnalazioniListFragment;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author Davide
 */
public class ElencoActivity extends HeaderFragmentActivity {

    private String url;
    private int SELECTOR_VIEW;
    private EditText search_box;
    private ImpostazioniListFragment impostazione_fragment;
    private AttivitaListFragment attivita_fragment;
    private SegnalazioniListFragment segnalazioni_fragment;
    private QrCodeListFragment qrcode_fragment;
    private String textSearch = "";

    private class mSaver extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url_saver = impostazione_fragment.getUrlSaver();
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(new URI(url_saver));
                HttpResponse response = httpClient.execute(httpGet);
            } catch (Exception e) {
            }
            return null;
        }
    }
    private ImageButton.OnClickListener image_button_listener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            mSaver s = new mSaver();
            s.execute(null, null, null);
            Intent i = new Intent(ElencoActivity.this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);
        super.setMenuView(R.layout.elenco);
        search_box = (EditText) super.findView(R.id.elenco_search_textview);
        selectionLanguage();
        search_box.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.search_grey), null, null, null);
        search_box.setCompoundDrawablePadding(10);
        search_box.setOnEditorActionListener(new DoneOnEditorActionListener());
        SELECTOR_VIEW = getIntent().getExtras().getInt(Key.KEY_ELENCO_SELECTOR_VIEW);
        chooseAction();
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (SELECTOR_VIEW) {
            case Constant.ELENCO_IMPOSTAZIONI:
                transaction.remove(impostazione_fragment);
                break;
            case Constant.ELENCO_ATTIVITA:
                transaction.remove(attivita_fragment);
                break;
            case Constant.ELENCO_QR_CODE:
                transaction.remove(qrcode_fragment);
                break;
            case Constant.ELENCO_SEGNALAZIONI:
                transaction.remove(segnalazioni_fragment);
                break;
        }
        transaction.commit();
        this.finish();
        super.onBackPressed();
    }

    private void chooseAction() {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (SELECTOR_VIEW) {
                case Constant.ELENCO_IMPOSTAZIONI:
                    url = Memory.getUrlRadice(getApplicationContext());
                    url = Network.addAction(url, Constant.ID_ACTION_PREFERENZE);
                    url = Network.addUidLanguageServerTime(url, getApplicationContext());
                    url = Network.addIdComune(url, getIntent().getExtras().getInt(Key.KEY_ID_CONTENUTO));
                    search_box.setVisibility(View.GONE);

                    impostazione_fragment = (ImpostazioniListFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_IMPOSTAZIONI);
                    if (impostazione_fragment == null) {
                        impostazione_fragment = new ImpostazioniListFragment(url, getIntent().getExtras().getInt(Key.KEY_ID_CONTENUTO));
                        ft.addToBackStack(null);
                        ft.add(R.id.elenco_fragment_container, impostazione_fragment, Constant.FRAGMENT_IMPOSTAZIONI);
                    }
                    super.setMenuButton(BitmapFactory.decodeResource(getResources(), R.drawable.send), image_button_listener, View.VISIBLE);
                    break;
                case Constant.ELENCO_ATTIVITA:
                    url = getIntent().getExtras().getString(Key.KEY_URL_CONTENUTO);
                    attivita_fragment = (AttivitaListFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_ATTIVITA);
                    if (attivita_fragment == null) {
                        attivita_fragment = new AttivitaListFragment(url);
                        ft.addToBackStack(null);
                        ft.add(R.id.elenco_fragment_container, attivita_fragment, Constant.FRAGMENT_ATTIVITA);
                    }
                    break;
                case Constant.ELENCO_QR_CODE:
                    qrcode_fragment = (QrCodeListFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_QRCODE);
                    if (qrcode_fragment == null) {
                        qrcode_fragment = new QrCodeListFragment();
                        ft.addToBackStack(null);
                        ft.add(R.id.elenco_fragment_container, qrcode_fragment, Constant.FRAGMENT_QRCODE);
                    }
                    break;
                case Constant.ELENCO_SEGNALAZIONI:
                    url = getIntent().getExtras().getString(Key.KEY_URL_CONTENUTO);
                    segnalazioni_fragment = (SegnalazioniListFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_SEGNALAZIONI);
                    if (segnalazioni_fragment == null) {
                        segnalazioni_fragment = new SegnalazioniListFragment(url);
                        ft.addToBackStack(null);
                        ft.add(R.id.elenco_fragment_container, segnalazioni_fragment, Constant.FRAGMENT_SEGNALAZIONI);
                    }
                    break;
            }
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } catch (Exception ex) {
            ErrorHelper.showFatalError(ElencoActivity.this);
        }
    }

    private class DoneOnEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                textSearch = v.getText().toString();
                switch (SELECTOR_VIEW) {
                    case Constant.ELENCO_ATTIVITA:
                        attivita_fragment.search(textSearch);
                        break;
                    case Constant.ELENCO_QR_CODE:
                        qrcode_fragment.search(textSearch);
                        break;
                    case Constant.ELENCO_SEGNALAZIONI:
                        segnalazioni_fragment.search(textSearch);
                        break;
                }
            }
            return true;
        }
    }

    private void selectionLanguage() {
        int id_search = 0;
        switch (Memory.getLanguage(getApplicationContext())) {
            case Memory.ID_LANGUAGE_ITA:
                id_search = R.string.search_box_hint_it;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_search = R.string.search_box_hint_en;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_search = R.string.search_box_hint_de;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_search = R.string.search_box_hint_fr;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_search = R.string.search_box_hint_es;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_search = R.string.search_box_hint_it;
                break;
        }
        search_box.setHint(id_search);
    }
}