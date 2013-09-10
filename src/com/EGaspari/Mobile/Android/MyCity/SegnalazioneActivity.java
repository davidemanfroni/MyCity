/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Davide
 */
public class SegnalazioneActivity extends HeaderActivity {

    private ImageButton nuova_segnalazione, informazioni, tutte_le_segnalazioni, le_mie_segnalazioni;
    private String url;
    private View progressDialog;
    private View layout;
    private String url_archivio, username, password, categorie[], comuni[];
    private int id_categorie[], id_comuni[];
    private AsyncTask<Void, Void, Void> mDownloaderUrls = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url_categorie, url_comuni;
                JSONArray jsonArray = Network.getJSONArray(url);
                JSONObject jsonObjectScrivi = jsonArray.getJSONObject(0);
                JSONObject jsonObjectArchivio = jsonArray.getJSONObject(1);
                /* Dati per nuova segnalazione*/
                url_categorie = jsonObjectScrivi.getString(Key.KEY_URL_FILTRI);
                url_comuni = jsonObjectScrivi.getString(Key.KEY_URL_CONTENUTO);
                JSONArray jsonArrayCategorie = Network.getJSONArray(url_categorie);
                JSONArray jsonArrayComuni = Network.getJSONArray(url_comuni);
                String[] dati = jsonObjectScrivi.getString(Key.KEY_DESCRIZIONE).split(";");
                username = dati[1];
                password = dati[2];
                categorie = new String[jsonArrayCategorie.length()];
                id_categorie = new int[jsonArrayCategorie.length()];
                comuni = new String[jsonArrayComuni.length()];
                id_comuni = new int[jsonArrayComuni.length()];
                for (int i = 0; i < categorie.length; i++) {
                    categorie[i] = jsonArrayCategorie.getJSONObject(i).getString(Key.KEY_TITOLO);
                    id_categorie[i] = jsonArrayCategorie.getJSONObject(i).getInt(Key.KEY_ID_CONTENUTO);
                }
                for(int j = 0; j < comuni.length; j++){
                    comuni[j] = jsonArrayComuni.getJSONObject(j).getString(Key.KEY_TITOLO);
                    id_comuni[j] = jsonArrayComuni.getJSONObject(j).getInt(Key.KEY_ID_CONTENUTO);
                }

                /*Dati per mie_segnalazioni*/
                url_archivio = jsonObjectArchivio.getString(Key.KEY_URL_CONTENUTO);
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            layout.setVisibility(View.VISIBLE);
            progressDialog.setVisibility(View.GONE);
        }
    };
    private ImageButton.OnClickListener listener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.segnalazione_nuova_segnalazione_button:
                    intent = new Intent(SegnalazioneActivity.this, NuovaSegnalazioneActivity.class);
                    intent.putExtra(Key.KEY_USERNAME, username);
                    intent.putExtra(Key.KEY_PASSWORD, password);
                    intent.putExtra(Key.KEY_CATEGORIE, categorie);
                    intent.putExtra(Key.KEY_ID_CATEGORIE, id_categorie);
                    intent.putExtra(Key.KEY_COMUNI, comuni);
                    intent.putExtra(Key.KEY_ID_COMUNI, id_comuni);
                    break;
                //case R.id.segnalazione_informazioni_button:
                //    break;
                case R.id.segnalazione_le_mie_segnalazioni_button:
                    intent = new Intent(SegnalazioneActivity.this, ElencoActivity.class);
                    intent.putExtra(Key.KEY_ELENCO_SELECTOR_VIEW, Constant.ELENCO_SEGNALAZIONI);
                    intent.putExtra(Key.KEY_URL_CONTENUTO, url_archivio);
                    break;
                //case R.id.segnalazione_tutte_le_segnalazioni_button:
                //    break;         
            }
            startActivity(intent);
        }
    };
    
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);     
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);     
        super.setMenuView(R.layout.segnalazione);
        layout = super.findView(R.id.segnalazione_layout);
        layout.setVisibility(View.GONE);
        progressDialog = super.findView(R.id.loading_spinner);
        nuova_segnalazione = (ImageButton) super.findView(R.id.segnalazione_nuova_segnalazione_button);
        informazioni = (ImageButton) super.findView(R.id.segnalazione_informazioni_button);
        tutte_le_segnalazioni = (ImageButton) super.findView(R.id.segnalazione_tutte_le_segnalazioni_button);
        le_mie_segnalazioni = (ImageButton) super.findView(R.id.segnalazione_le_mie_segnalazioni_button);
        selectionLanguage();
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        int w = p.x / 2;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, w);
        nuova_segnalazione.setLayoutParams(params);
        nuova_segnalazione.setScaleType(ImageView.ScaleType.FIT_XY);
        informazioni.setLayoutParams(params);
        informazioni.setScaleType(ImageView.ScaleType.FIT_XY);
        tutte_le_segnalazioni.setLayoutParams(params);
        tutte_le_segnalazioni.setScaleType(ImageView.ScaleType.FIT_XY);
        le_mie_segnalazioni.setLayoutParams(params);
        le_mie_segnalazioni.setScaleType(ImageView.ScaleType.FIT_XY);
        nuova_segnalazione.setOnClickListener(listener);
        informazioni.setOnClickListener(listener);
        tutte_le_segnalazioni.setOnClickListener(listener);
        le_mie_segnalazioni.setOnClickListener(listener);
        url = getIntent().getExtras().getString(Key.KEY_URL_CONTENUTO);
        mDownloaderUrls.execute(null, null, null);
    }

    private void selectionLanguage() {
        int id_nuova_segnalazione = 0, id_le_mie_segnalazioni = 0, id_tutte_le_segnalazioni = 0, id_informazioni = 0;
        switch (Memory.getLanguage(getApplicationContext())) {
            case Memory.ID_LANGUAGE_ITA:
                id_informazioni = R.drawable.segnalazioni_info_it;
                id_nuova_segnalazione = R.drawable.segnalazioni_nuova_it;
                id_le_mie_segnalazioni = R.drawable.segnalazioni_mie_it;
                id_tutte_le_segnalazioni = R.drawable.segnalazioni_tutte_it;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_informazioni = R.drawable.segnalazioni_info_en;
                id_nuova_segnalazione = R.drawable.segnalazioni_nuova_en;
                id_le_mie_segnalazioni = R.drawable.segnalazioni_mie_en;
                id_tutte_le_segnalazioni = R.drawable.segnalazioni_tutte_en;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_informazioni = R.drawable.segnalazioni_info_de;
                id_nuova_segnalazione = R.drawable.segnalazioni_nuova_de;
                id_le_mie_segnalazioni = R.drawable.segnalazioni_mie_de;
                id_tutte_le_segnalazioni = R.drawable.segnalazioni_tutte_de;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_informazioni = R.drawable.segnalazioni_info_fr;
                id_nuova_segnalazione = R.drawable.segnalazioni_nuova_fr;
                id_le_mie_segnalazioni = R.drawable.segnalazioni_mie_fr;
                id_tutte_le_segnalazioni = R.drawable.segnalazioni_tutte_fr;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_informazioni = R.drawable.segnalazioni_info_es;
                id_nuova_segnalazione = R.drawable.segnalazioni_nuova_es;
                id_le_mie_segnalazioni = R.drawable.segnalazioni_mie_es;
                id_tutte_le_segnalazioni = R.drawable.segnalazioni_tutte_es;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_informazioni = R.drawable.segnalazioni_info_it;
                id_nuova_segnalazione = R.drawable.segnalazioni_nuova_it;
                id_le_mie_segnalazioni = R.drawable.segnalazioni_mie_it;
                id_tutte_le_segnalazioni = R.drawable.segnalazioni_tutte_it;
                break;
        }
        le_mie_segnalazioni.setImageResource(id_le_mie_segnalazioni);
        informazioni.setImageResource(id_informazioni);
        tutte_le_segnalazioni.setImageResource(id_tutte_le_segnalazioni);
        nuova_segnalazione.setImageResource(id_nuova_segnalazione);
    }
}
