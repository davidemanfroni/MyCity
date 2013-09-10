/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.EGaspari.Mobile.Objects.LoaderImageView;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Davide
 */
public class SegnalazioniListFragment extends Fragment {

    private List<Object> segnalazioni_list, old_segnalazioni_list;
    private ListAdapter adapter;
    private String url;
    private View progress_dialog;
    private ListView list_view;
    private JSONArray jArray;
    private AsyncTask<Void, Void, Void> mDownloader;

    private void download() {
        list_view.setVisibility(View.GONE);
        progress_dialog.setVisibility(View.VISIBLE);
        segnalazioni_list = new ArrayList<Object>();
        mDownloader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (jArray == null) {
                        jArray = Network.getJSONArray(url);
                    }
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject obj = jArray.getJSONObject(i);
                        Segnalazione s = new Segnalazione();
                        s.titolo = obj.getString(Key.KEY_TITOLO);
                        s.data = obj.getString(Key.KEY_SOTTOTITOLO);
                        s.descrizione = obj.getString(Key.KEY_DESCRIZIONE);
                        s.stato = obj.getInt(Key.KEY_APPROVATO);
                        s.comando = obj.getString(Key.KEY_COMANDO);
                        s.view_successiva = obj.getInt(Key.KEY_VIEW_SUCCESSIVA);
                        s.url = obj.getString(Key.KEY_URL_CONTENUTO);
                        s.url_immagine = obj.getString(Key.KEY_URL_IMMAGINE);
                            
                        
                        segnalazioni_list.add(s);
                    }
                } catch (Exception ex) {
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                show();
            }
        };
        mDownloader.execute(null, null, null);
    }

    private void show() {
        adapter = new ListAdapter();
        progress_dialog.setVisibility(View.GONE);
        list_view.setAdapter(adapter);
        list_view.setVisibility(View.VISIBLE);
    }

    public SegnalazioniListFragment(String url) {
        super();
        this.url = url;
    }

    public SegnalazioniListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                jArray = new JSONArray(savedInstanceState.getString(Key.BUNDLE_KEY_JSON_ARRAY));
            }
            if (Network.isNetworkAvaliable(getActivity())) {
                download();
            } else {
                ErrorHelper.showNetworkError(getActivity());
            }
        } catch (Exception ex) {
            //...
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        if (savedInstance == null) {
            savedInstance = new Bundle();
        }
        if (jArray != null) {
            savedInstance.putString(Key.BUNDLE_KEY_JSON_ARRAY, jArray.toString());
        }
        
        super.onSaveInstanceState(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.elenco_list_fragment, container, false);
        list_view = (ListView) view.findViewById(R.id.elenco_list_view);
        progress_dialog = view.findViewById(R.id.loading_spinner);
        return view;
    }

    public void search(String textSearch) {
        progress_dialog.setVisibility(View.VISIBLE);
        list_view.setVisibility(View.GONE);
        if (old_segnalazioni_list == null) {
            old_segnalazioni_list = segnalazioni_list;
        }
        segnalazioni_list = new ArrayList<Object>();
        for (int i = 0; i < old_segnalazioni_list.size(); i++) {
            Object item = old_segnalazioni_list.get(i);

            if ((((Segnalazione) item).descrizione.toLowerCase().contains(textSearch.toLowerCase()))
                    || (((Segnalazione) item).titolo.toLowerCase().contains(textSearch.toLowerCase()))
                    || (((Segnalazione) item).data.toLowerCase().contains(textSearch.toLowerCase()))) {
                segnalazioni_list.add(item);
            }
        }
        show();
    }

    private class Segnalazione {

        public String data, titolo, descrizione;
        public Bitmap immagine;
        public int stato;
        public String comando;
        public int view_successiva;
        public String url;
        public String url_immagine;

        public Segnalazione() {
            data = "";
            titolo = "";
            descrizione = "";
            immagine = null;
            stato = -1;
            comando = "";
            view_successiva = 0;
            url = "";
            url_immagine = "";
        }
    }

    private class ListAdapter extends BaseAdapter {

        public ListAdapter() {
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = null;
            final Segnalazione item = (Segnalazione) segnalazioni_list.get(position);
            try {
                if (view == null) {
                    view = getActivity().getLayoutInflater().inflate(R.layout.elenco_segnalazioni_item, parent, false);
                }
                TextView data, titolo, descrizione, stato, stato_testo;
                LoaderImageView immagine;
                data = (TextView) view.findViewById(R.id.segnalazione_item_data);
                titolo = (TextView) view.findViewById(R.id.segnalazione_item_titolo);
                descrizione = (TextView) view.findViewById(R.id.segnalazione_item_descrizione);
                stato = (TextView) view.findViewById(R.id.segnalazione_item_stato);
                stato_testo = (TextView) view.findViewById(R.id.segnalazione_item_stato_testo);
                immagine = (LoaderImageView) view.findViewById(R.id.segnalazione_item_immagine);
                data.setText(Utility.toUpperCaseFirstLetter(item.data.trim()));
                data.setTypeface(Utility.HelveticaNeueNormal(getActivity().getApplicationContext()));
                titolo.setText(Utility.toUpperCaseFirstLetter(item.titolo.trim()));
                titolo.setTypeface(Utility.HelveticaNeueBold(getActivity().getApplicationContext()));
                descrizione.setText(Utility.toUpperCaseFirstLetter(item.descrizione.trim()));
                descrizione.setTypeface(Utility.HelveticaNeueNormal(getActivity().getApplicationContext()));
                if (((Segnalazione) item).immagine == null) {
                    immagine.setImageDrawable(((Segnalazione) item).url_immagine);
                }
                stato.setText("Stato:");
                stato.setTypeface(Utility.HelveticaNeueBold(getActivity().getApplicationContext()));
                stato.setTextColor(R.color.black);
                switch (item.stato) {
                    case Constant.SEGNALAZIONE_APPROVATA:
                        stato_testo.setText("Risolto");
                        stato_testo.setTextColor(R.color.verde);
                        break;
                    case Constant.SEGNALAZIONE_APPROVAZIONE_IN_CORSO:
                        stato_testo.setText("Presa in consegna");
                        stato_testo.setTextColor(getResources().getColor(R.color.giallo));

                        break;
                    case Constant.SEGNALAZIONE_NON_APPROVATA:
                        stato_testo.setText("Non approvata");
                        stato_testo.setTextColor(R.color.rosso);
                        break;
                }
                stato_testo.setTypeface(Utility.HelveticaNeueNormal(getActivity().getApplicationContext()));
                view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (item instanceof Segnalazione) {
                            Intent i = null;
                            if (((Segnalazione) item).comando.equals("")) {
                                i = Utility.getIntentFromId(((Segnalazione) item).view_successiva, getActivity());
                            } else {
                                i = Utility.getIntentFromCommand(((Segnalazione) item).comando, getActivity().getApplicationContext());
                            }
                            i.putExtra(Key.KEY_URL_CONTENUTO, ((Segnalazione) item).url);
                            startActivity(i);
                        }
                    }
                });
            } catch (Exception ex) {
            }
            return view;
        }

        public int getCount() {
            return segnalazioni_list.size();
        }

        public Object getItem(int position) {
            return segnalazioni_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    }
}
