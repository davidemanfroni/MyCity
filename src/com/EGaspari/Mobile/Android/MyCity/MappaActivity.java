/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.EGaspari.Mobile.Android.MyCity.HeaderFragmentActivity;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.EGaspari.Mobile.Objects.Poi;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Davide
 */
public class MappaActivity extends HeaderFragmentActivity {

    private GoogleMap map;
    private SupportMapFragment fragment_map;
    private View progress_dialog;
    private AsyncTask<Void, Void, Void> mDownloader, mDownloaderFiltri;
    private List<Poi> marker_list, old_marker_list;
    private String url, url_filtri, text_search, url_base;
    private LinearLayout layout;
    private ImageButton filtri_button, move_camera_button;
    private Filtro filtri[];
    private EditText search_box;
    private int id_dialog_title, id_dialog_button_conferma, id_dialog_button_annulla, id_dialog_text_search;
    private ImageButton.OnClickListener filtra_listener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            createFilterDialog();
        }
    };
    private ImageButton.OnClickListener move_camera = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            moveCamera();
        }
    };
    private int id_dialog_empty_message;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);
        super.setMenuView(R.layout.mappa);
        search_box = (EditText) super.findView(R.id.map_search_textview);
        progress_dialog = super.findView(R.id.loading_spinner);
        filtri_button = (ImageButton) super.findView(R.id.map_filtri_button);
        move_camera_button = (ImageButton) super.findView(R.id.map_there_button);
        layout = (LinearLayout) super.findView(R.id.map_layout);
        filtri_button.setOnClickListener(filtra_listener);
        move_camera_button.setOnClickListener(move_camera);
        selectionLanguage();
        search_box.setHint(id_dialog_text_search);
        search_box.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.search_grey), null, null, null);
        search_box.setCompoundDrawablePadding(10);
        search_box.setOnEditorActionListener(new DoneOnEditorActionListener());
        fragment_map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        map = fragment_map.getMap();
        url = getIntent().getExtras().getString(Key.KEY_URL_CONTENUTO);
        url_base = url;
        marker_list = new ArrayList<Poi>();
        if (Network.isNetworkAvaliable(MappaActivity.this)) {
            downloadContent();
        } else {
            ErrorHelper.showNetworkError(MappaActivity.this);
        }
    }

    private void downloadContent() {
        progress_dialog.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        mDownloader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    marker_list.clear();
                    JSONArray json_array = Network.getJSONArray(url);
                    for (int i = 0; i < json_array.length(); i++) {
                        Poi marker = new Poi();
                        JSONObject json_object = json_array.getJSONObject(i);
                        if (url_filtri == null) {
                            url_filtri = json_object.getString(Key.KEY_URL_FILTRI);
                        }
                        marker.titolo = json_object.getString(Key.KEY_TITOLO);
                        String lat = json_object.getString(Key.KEY_LATITUDINE);
                        String lng = json_object.getString(Key.KEY_LONGITUDINE);                      
                        if(!lat.trim().equals("") && !lng.trim().equals("")){
                            marker.lat = Double.parseDouble(lat);
                            marker.lon = Double.parseDouble(lng);
                            marker_list.add(marker);
                        }
                    }
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                showMap();
            }
        };
        mDownloader.execute(null, null, null);
    }

    private void showMap() {
        progress_dialog.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        map.setMyLocationEnabled(true);
        map.clear();
        for (int i = 0; i < marker_list.size(); i++) {
            MarkerOptions opt = new MarkerOptions();
            opt.title(marker_list.get(i).titolo);
            opt.snippet(marker_list.get(i).sotto_titolo);
            opt.position(new LatLng(marker_list.get(i).lat, marker_list.get(i).lon));
            map.addMarker(opt);
        }
        moveCamera();
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                LatLng pos = marker.getPosition();
                String label = marker.getTitle();
                String uriBegin = "geo:" + pos.latitude + "," + pos.longitude;
                String query = pos.latitude + "," + pos.longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=17";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        if (filtri == null) {
            mDownloaderFiltri = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        JSONArray json_array = Network.getJSONArray(url_filtri);
                        filtri = new Filtro[json_array.length()];
                        for (int i = 0; i < json_array.length(); i++) {
                            Filtro f = new Filtro();
                            JSONObject json_object = json_array.getJSONObject(i);
                            f.id = json_object.getInt(Key.KEY_ID_CONTENUTO);
                            f.titolo = json_object.getString(Key.KEY_TITOLO);
                            f.set = false;
                            filtri[i] = f;
                        }
                    } catch (Exception ex) {
                    }
                    return null;
                }
            };
            mDownloaderFiltri.execute(null, null, null);
        }
    }

    private void moveCamera() {
        if (marker_list.size() > 0) {
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < marker_list.size(); i++) {
                LatLng ll = new LatLng(marker_list.get(i).lat, marker_list.get(i).lon);
                builder.include(ll);
            }
            OnCameraChangeListener cam = new OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition arg0) {
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                    // Remove listener to prevent position reset on camera move.
                    map.setOnCameraChangeListener(null);
                }
            };
            map.setOnCameraChangeListener(cam);
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(marker_list.get(0).lat, marker_list.get(0).lon)));
        }
    }

    private class Filtro {

        String titolo;
        int id;
        boolean set;

        public Filtro() {
        }
    }

    private void createFilterDialog() {
        
            AlertDialog.Builder dialogFiltra = new AlertDialog.Builder(this);
            dialogFiltra.setCancelable(true);
            dialogFiltra.setTitle(id_dialog_title);
            dialogFiltra.setNegativeButton(id_dialog_button_annulla, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog.cancel();
                }
            });
            if (filtri != null) {
            final String[] item_title = new String[filtri.length];
            final boolean[] item_state = new boolean[filtri.length];
            for (int i = 0; i < filtri.length; i++) {
                item_title[i] = filtri[i].titolo;
                item_state[i] = filtri[i].set;
            }
            
                dialogFiltra.setMultiChoiceItems(item_title, item_state, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        filtri[which].set = isChecked;
                    }
                });

                dialogFiltra.setPositiveButton(id_dialog_button_conferma, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int count = 0;
                        for (int i = 0; i < filtri.length; i++) {
                            if (filtri[i].set) {
                                count++;
                            }
                        }
                        int[] chooses = new int[count];
                        int indexChooses = 0;
                        for (int j = 0; j < filtri.length; j++) {
                            if (filtri[j].set) {
                                chooses[indexChooses] = filtri[j].id;
                                indexChooses++;
                            }
                        }
                        if (chooses.length == 0) {
                            url = url_base;
                        } else {
                            url = Network.setUrlWithCodSceltaCombo(chooses, url);
                        }
                        if (old_marker_list == null) {
                            old_marker_list = new ArrayList<Poi>();
                        }
                        old_marker_list.clear();
                        for (int i = 0; i < marker_list.size(); i++) {
                            old_marker_list.add(marker_list.get(i));
                        }
                        dialog.dismiss();
                        dialog.cancel();
                        downloadContent();
                    }
                });
            }else{
                dialogFiltra.setMessage(id_dialog_empty_message);
            }
            dialogFiltra.show();
       
    }

    private class DoneOnEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                text_search = v.getText().toString();
                if (!text_search.equals("")) {
                    search(text_search);
                } else {
                    if (old_marker_list != null) {
                        for (int i = 0; i < old_marker_list.size(); i++) {
                            marker_list.add(old_marker_list.get(i));
                        }
                        showMap();
                    }
                }
            }
            return true;
        }
    }

    public void search(String textSearch) {
        progress_dialog.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        if (old_marker_list == null) {
            old_marker_list = new ArrayList<Poi>();
        }
        for (int i = 0; i < marker_list.size(); i++) {
            old_marker_list.add(marker_list.get(i));
        }
        marker_list.clear();
        for (int i = 0; i < old_marker_list.size(); i++) {

            Poi item = old_marker_list.get(i);
            if (item.titolo.toLowerCase().contains(textSearch.toLowerCase()) || item.sotto_titolo.toLowerCase().contains(textSearch.toLowerCase())) {
                marker_list.add(item);
            }
        }
        map.clear();
        showMap();
    }

    private void selectionLanguage() {
        switch (Memory.getLanguage(getApplicationContext())) {
            case Memory.ID_LANGUAGE_ITA:
                id_dialog_button_annulla = R.string.annulla_it;
                id_dialog_button_conferma = R.string.conferma_it;
                id_dialog_text_search = R.string.cerca_mappa_it;
                id_dialog_title = R.string.cerca_mappa_it;
                id_dialog_empty_message = R.string.nessun_contenuto_it;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_dialog_button_annulla = R.string.annulla_en;
                id_dialog_button_conferma = R.string.conferma_en;
                id_dialog_text_search = R.string.cerca_mappa_en;
                id_dialog_title = R.string.cerca_mappa_en;
                id_dialog_empty_message = R.string.nessun_contenuto_en;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_dialog_button_annulla = R.string.annulla_de;
                id_dialog_button_conferma = R.string.conferma_de;
                id_dialog_text_search = R.string.cerca_mappa_de;
                id_dialog_title = R.string.cerca_mappa_de;
                id_dialog_empty_message = R.string.nessun_contenuto_de;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_dialog_button_annulla = R.string.annulla_fr;
                id_dialog_button_conferma = R.string.conferma_fr;
                id_dialog_text_search = R.string.cerca_mappa_fr;
                id_dialog_title = R.string.cerca_mappa_fr;
                id_dialog_empty_message = R.string.nessun_contenuto_fr;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_dialog_empty_message = R.string.nessun_contenuto_es;
                id_dialog_button_annulla = R.string.annulla_es;
                id_dialog_button_conferma = R.string.conferma_es;
                id_dialog_text_search = R.string.cerca_mappa_es;
                id_dialog_title = R.string.cerca_mappa_es;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_dialog_button_annulla = R.string.annulla_it;
                id_dialog_button_conferma = R.string.conferma_it;
                id_dialog_text_search = R.string.cerca_mappa_it;
                id_dialog_title = R.string.cerca_mappa_it;
                id_dialog_empty_message = R.string.nessun_contenuto_it;
                break;
        }
    }
}
