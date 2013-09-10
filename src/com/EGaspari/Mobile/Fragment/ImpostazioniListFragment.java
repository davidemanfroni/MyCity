/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Davide
 */
public class ImpostazioniListFragment extends Fragment {

    private List<Object> impostazioni_list;
    private ListAdapter adapter;
    private String url;
    private View progress_dialog;
    private ListView list_view;
    private int oldPosition;
    private ImageView old_image_state;
    private int selected_language, id_comune;
    private List<ImageView> list_image;
    private int id_lingua, id_preferenze, id_salvataggio, id_salvataggio_fallito, id_seleziona_lingua;

    private class mDownloader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ImpostazioneCategory header_1 = new ImpostazioneCategory();
            header_1.titolo = "LINGUA";
            impostazioni_list.add(header_1);

            /*Preferenze*/
            ImpostazioneLingua[] prefsLng = new ImpostazioneLingua[5];
            prefsLng[0] = new ImpostazioneLingua();
            prefsLng[0].titolo = "Italiano";
            prefsLng[0].icona = getResources().getDrawable(R.drawable.ic_context_flag_it);
            prefsLng[0].id = Memory.ID_LANGUAGE_ITA;
            prefsLng[1] = new ImpostazioneLingua();
            prefsLng[1].titolo = "English";
            prefsLng[1].icona = getResources().getDrawable(R.drawable.ic_context_flag_en);
            prefsLng[1].id = Memory.ID_LANGUAGE_EN;
            prefsLng[2] = new ImpostazioneLingua();
            prefsLng[2].titolo = "Deutsch";
            prefsLng[2].icona = getResources().getDrawable(R.drawable.ic_context_flag_de);
            prefsLng[2].id = Memory.ID_LANGUAGE_DE;
            prefsLng[3] = new ImpostazioneLingua();
            prefsLng[3].titolo = "Francais";
            prefsLng[3].icona = getResources().getDrawable(R.drawable.ic_context_flag_fr);
            prefsLng[3].id = Memory.ID_LANGUAGE_FR;
            prefsLng[4] = new ImpostazioneLingua();
            prefsLng[4].titolo = "Espanol";
            prefsLng[4].icona = getResources().getDrawable(R.drawable.ic_context_flag_es);
            prefsLng[4].id = Memory.ID_LANGUAGE_ES;

            impostazioni_list.add(prefsLng[0]);
            impostazioni_list.add(prefsLng[1]);
            impostazioni_list.add(prefsLng[2]);
            impostazioni_list.add(prefsLng[3]);
            impostazioni_list.add(prefsLng[4]);

            ImpostazioneCategory header_2 = new ImpostazioneCategory();
            header_2.titolo = "PREFERENZE";
            impostazioni_list.add(header_2);

            JSONArray array = Network.getJSONArray(url);
            JSONArray arrayPref = null;
            try {
                arrayPref = Network.getJSONArray(array.getJSONObject(0).getString(Key.KEY_URL_FILTRI));
                for (int j = 0; j < arrayPref.length(); j++) {
                    try {
                        ImpostazioneElement p = new ImpostazioneElement();
                        JSONObject object = arrayPref.getJSONObject(j);
                        p.titolo = object.getString(Key.KEY_TITOLO);
                        p.id = object.getInt(Key.KEY_ID_CONTENUTO);
                        String set = object.getString(Key.KEY_APPROVATO);
                        if (set.toLowerCase().equals("true")) {
                            p.set = true;
                        } else {
                            p.set = false;
                        }
                        impostazioni_list.add(p);
                    } catch (JSONException ex) {
                        Logger.getLogger(ImpostazioniListFragment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            selected_language = Memory.getLanguage(getActivity().getApplicationContext());
            if (selected_language != -1) {
                ((ImpostazioneLingua) impostazioni_list.get(selected_language)).set = true;

            } else {
                oldPosition = -1;
                old_image_state = null;
            }
            list_view.setVisibility(View.VISIBLE);
            list_view.setAdapter(adapter);
            progress_dialog.setVisibility(View.GONE);
        }
    }

    public ImpostazioniListFragment(String url, int id_comune) {
        super();
        this.url = url;
        this.id_comune = id_comune;

    }

    public ImpostazioniListFragment() {
        super();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Network.isNetworkAvaliable(getActivity())) {
            new mDownloader().execute(null, null, null);
        } else {
            ErrorHelper.showNetworkError(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.elenco_list_fragment, container, false);
        list_view = (ListView) view.findViewById(R.id.elenco_list_view);
        progress_dialog = view.findViewById(R.id.loading_spinner);
        list_view.setVisibility(View.GONE);
        list_image = new ArrayList<ImageView>();
        adapter = new ListAdapter();
        impostazioni_list = new ArrayList<Object>();
        return view;
    }

    private class ListAdapter extends BaseAdapter {

        public ListAdapter() {
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            final Object item = impostazioni_list.get(position);
            try {
                if (item instanceof ImpostazioneCategory) {
                    if (view == null) {
                        view = getActivity().getLayoutInflater().inflate(R.layout.elenco_impostazioni_category, parent, false);
                        view.setClickable(false);
                    }
                    TextView tv = (TextView) view;
                    tv.setText(((ImpostazioneCategory) item).titolo);
                    tv.setClickable(false);
                } else if (item instanceof ImpostazioneElement) {
                    if (view == null) {
                        view = getActivity().getLayoutInflater().inflate(R.layout.elenco_impostazioni_item, parent, false);
                    }
                    TextView testo = (TextView) view.findViewById(R.id.elenco_impostazioni_title);
                    final ImageView image_state = (ImageView) view.findViewById(R.id.elenco_impostazioni_state);
                    testo.setText(((ImpostazioneElement) item).titolo);
                    if (!((ImpostazioneElement) item).set) {
                        image_state.setVisibility(View.GONE);
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            ((ImpostazioneElement) item).set = !((ImpostazioneElement) item).set;
                            if (!((ImpostazioneElement) item).set) {
                                image_state.setVisibility(View.GONE);
                            } else {
                                image_state.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } else if (item instanceof ImpostazioneLingua) {
                    if (view == null) {
                        view = getActivity().getLayoutInflater().inflate(R.layout.elenco_impostazioni_item, parent, false);
                    }
                    TextView testo = (TextView) view.findViewById(R.id.elenco_impostazioni_title);
                    final ImageView image_state = (ImageView) view.findViewById(R.id.elenco_impostazioni_state);
                    testo.setText(((ImpostazioneLingua) item).titolo);
                    testo.setCompoundDrawablesWithIntrinsicBounds(((ImpostazioneLingua) item).icona, null, null, null);
                    if (!((ImpostazioneLingua) item).set) {
                        image_state.setVisibility(View.GONE);
                    } else {
                        image_state.setVisibility(View.VISIBLE);

                        oldPosition = ((ImpostazioneLingua) item).id;
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {
                            if (item instanceof ImpostazioneLingua) {
                                selected_language = ((ImpostazioneLingua) item).id;
                                if (oldPosition == -1) {
                                    ((ImpostazioneLingua) item).set = true;
                                    oldPosition = selected_language;
                                } else if (oldPosition != ((ImpostazioneLingua) item).id) {
                                    ((ImpostazioneLingua) item).set = true;
                                    //Log.i("DEBUG", String.valueOf(oldPosition));
                                    ((ImpostazioneLingua) impostazioni_list.get(oldPosition)).set = false;
                                    oldPosition = selected_language;


                                }
                                Memory.setLanguage(getActivity().getApplicationContext(), selected_language);
                            }
                            adapter = new ListAdapter();
                            list_view.setAdapter(adapter);
                        }
                    });
                }
            } catch (Exception ex) {
            }
            return view;
        }

        public int getCount() {
            return impostazioni_list.size();
        }

        public Object getItem(int position) {
            return impostazioni_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    }

    private class ImpostazioneElement {

        public String titolo;
        public int id;
        public boolean set;

        public ImpostazioneElement() {
            titolo = "";
            id = 0;
            set = false;
        }
    }

    private class ImpostazioneLingua {

        public String titolo;
        public int id;
        public boolean set;
        public Drawable icona;

        public ImpostazioneLingua() {
            titolo = "";
            id = 0;
            set = false;
            icona = null;
        }
    }

    private class ImpostazioneCategory {

        public String titolo;

        public ImpostazioneCategory() {
            titolo = "";
        }
    }

    public String getUrlSaver() {
        if (Memory.getLanguage(getActivity().getApplicationContext()) == -1) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity()).setCancelable(false).setMessage("Selezionare la lingua").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
            return null;
        } else {
            String urlSetPref = Memory.getUrlRadice(getActivity().getApplicationContext());
            urlSetPref = Network.addAction(urlSetPref, Constant.ID_SET_IMPOSTAZIONI);
            int j = 7;
            urlSetPref += "%26codsceltacombo%3D";
            while (j < impostazioni_list.size()) {
                Object p = (Object) impostazioni_list.get(j);
                if (p instanceof ImpostazioneElement) {
                    if (!((ImpostazioneElement) p).set) {
                        urlSetPref += String.valueOf(((ImpostazioneElement) p).id);

                        urlSetPref += ",";

                    }
                    j++;
                }
            }
            if (urlSetPref.endsWith(",")) {
                urlSetPref = urlSetPref.substring(0, urlSetPref.length() - 1);
            }
            urlSetPref = Network.addUidLanguageServerTime(urlSetPref, getActivity().getApplicationContext());
            urlSetPref = Network.addIdComune(urlSetPref, id_comune);
            Log.i("DEBUG", urlSetPref);
            return urlSetPref;

        }

    }

    private void selectionLanguage() {
        switch (Memory.getLanguage(getActivity())) {
            case Memory.ID_LANGUAGE_ITA:
                id_lingua = R.string.lingua_it;
                id_preferenze = R.string.preferenze_it;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_lingua = R.string.lingua_en;
                id_preferenze = R.string.preferenze_en;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_lingua = R.string.lingua_en;
                id_preferenze = R.string.preferenze_en;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_lingua = R.string.lingua_en;
                id_preferenze = R.string.preferenze_en;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_lingua = R.string.lingua_en;
                id_preferenze = R.string.preferenze_en;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_lingua = R.string.lingua_en;
                id_preferenze = R.string.preferenze_en;
                break;
        }
    }
}
