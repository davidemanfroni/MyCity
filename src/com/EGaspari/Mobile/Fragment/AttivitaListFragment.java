/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.EGaspari.Mobile.Objects.AttivitaCategory;
import com.EGaspari.Mobile.Objects.AttivitaElement;
import com.EGaspari.Mobile.Objects.LoaderImageView;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Helper;
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
public class AttivitaListFragment extends Fragment {

    private List<Object> attivita_list, old_attivita_list;
    private ListAdapter adapter;
    private String url;
    private ListView list_view;
    private View progress_dialog;
    private int mode = 0;
    private JSONArray jsonArray;

    private void show() {
        adapter = new ListAdapter();
        progress_dialog.setVisibility(View.GONE);
        list_view.setAdapter(adapter);
        list_view.setVisibility(View.VISIBLE);
        if (mode == 1) {
            Helper.getListViewSize(list_view);
        }
    }

    public AttivitaListFragment(String url) {
        super();
        this.url = url;
        this.mode = 0;

    }

    public AttivitaListFragment(String url, int mode) {
        super();
        this.url = url;
        this.mode = mode;
    }

    public AttivitaListFragment() {
        super();

        this.mode = 0;
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
                jsonArray = new JSONArray(savedInstanceState.getString(Key.BUNDLE_KEY_JSON_ARRAY));
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
        if (jsonArray != null) {
            savedInstance.putString(Key.BUNDLE_KEY_JSON_ARRAY, jsonArray.toString());
        }
        super.onSaveInstanceState(savedInstance);
    }

    private void download() {
        progress_dialog.setVisibility(View.VISIBLE);
        list_view.setVisibility(View.GONE);
        attivita_list = new ArrayList<Object>();
        AsyncTask<Void, Void, Void> mDownloader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (jsonArray == null) {
                        jsonArray = Network.getJSONArray(url);
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String dettagli = jsonObject.getString(Key.KEY_DETTAGLI);
                        if ((!dettagli.equals(""))) {
                            AttivitaCategory categoria = new AttivitaCategory();
                            categoria.text = jsonObject.getString(Key.KEY_TITOLO);
                            categoria.id = jsonObject.getInt(Key.KEY_ID_CONTENUTO);
                            categoria.icona = getResources().getDrawable(R.drawable.separatore);
                            attivita_list.add(categoria);
                            JSONArray jsonArrayDett = new JSONArray(dettagli);
                            for (int j = 0; j < jsonArrayDett.length(); j++) {
                                JSONObject jsonObjectDett = jsonArrayDett.getJSONObject(j);
                                AttivitaElement item = new AttivitaElement();
                                item.titolo = jsonObjectDett.getString(Key.KEY_TITOLO);
                                item.sotto_titolo = jsonObjectDett.getString(Key.KEY_SOTTOTITOLO);
                                item.descrizione = jsonObjectDett.getString(Key.KEY_DESCRIZIONE);
                                item.url_next_view = jsonObjectDett.getString(Key.KEY_URL_CONTENUTO);
                                item.id_next_view = jsonObjectDett.getInt(Key.KEY_VIEW_SUCCESSIVA);
                                item.url_immagine = jsonObjectDett.getString(Key.KEY_URL_IMMAGINE);
                                attivita_list.add(item);
                            }
                        } else {
                            AttivitaElement item = new AttivitaElement();
                            item.titolo = jsonObject.getString(Key.KEY_TITOLO);
                            item.sotto_titolo = jsonObject.getString(Key.KEY_SOTTOTITOLO);
                            item.descrizione = jsonObject.getString(Key.KEY_DESCRIZIONE);
                            item.url_next_view = jsonObject.getString(Key.KEY_URL_CONTENUTO);
                            item.id_next_view = jsonObject.getInt(Key.KEY_VIEW_SUCCESSIVA);
                            item.url_immagine = jsonObject.getString(Key.KEY_URL_IMMAGINE);
                            attivita_list.add(item);
                        }
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
        if (old_attivita_list == null) {
            old_attivita_list = attivita_list;
        }
        attivita_list = new ArrayList<Object>();
        for (int i = 0; i < old_attivita_list.size(); i++) {
            Object item = old_attivita_list.get(i);
            if (item instanceof AttivitaElement) {
                if ((((AttivitaElement) item).descrizione.toLowerCase().contains(textSearch.toLowerCase()))
                        || (((AttivitaElement) item).titolo.toLowerCase().contains(textSearch.toLowerCase()))
                        || (((AttivitaElement) item).sotto_titolo.toLowerCase().contains(textSearch.toLowerCase()))) {
                    attivita_list.add(item);
                }
            } else if (item instanceof AttivitaCategory) {
                attivita_list.add(item);
            }
        }
        show();
    }

    private class ListAdapter extends BaseAdapter {

        public ListAdapter() {
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = null;
            final Object item = attivita_list.get(position);
            if (item instanceof AttivitaCategory) {
                if (v == null) {
                    v = getActivity().getLayoutInflater().inflate(R.layout.elenco_category, parent, false);
                }
                TextView tv = (TextView) v.findViewById(R.id.elenco_category_textview);
                tv.setText(((AttivitaCategory) item).text);
                tv.setTypeface(Utility.ClearfaceGothicMedium(getActivity().getApplicationContext()));
            } else if (item instanceof AttivitaElement) {
                if (v == null) {
                    v = getActivity().getLayoutInflater().inflate(R.layout.elenco_item, parent, false);
                }
                TextView titolo = (TextView) v.findViewById(R.id.elenco_row_item_titolo);
                TextView sotto_titolo = (TextView) v.findViewById(R.id.elenco_row_item_sotto_titolo);
                LoaderImageView immagine = (LoaderImageView) v.findViewById(R.id.elenco_row_item_immagine);
                titolo.setText(Utility.toUpperCaseFirstLetter(((AttivitaElement) item).titolo));
                titolo.setTypeface(Utility.HelveticaNeueBold(getActivity().getApplicationContext()));
                sotto_titolo.setText((((AttivitaElement) item).sotto_titolo + "\n" + ((AttivitaElement) item).descrizione).trim());
                sotto_titolo.setTypeface(Utility.HelveticaNeueNormal(getActivity().getApplicationContext()));
                if (((AttivitaElement) item).immagine == null) {
                    immagine.setImageDrawable(((AttivitaElement) item).url_immagine);
                   //((AttivitaElement) item).immagine = immagine.getDrawable();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    immagine.setImageLayoutParams(params);
                    immagine.setScaledType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    immagine.setImageDrawableWithoutUrl(((AttivitaElement) item).immagine);
                }
                //immagine.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (item instanceof AttivitaElement) {
                            Intent i = Utility.getIntentFromId(((AttivitaElement) item).id_next_view, getActivity());
                            i.putExtra(Key.KEY_URL_CONTENUTO, ((AttivitaElement) item).url_next_view);
                            startActivity(i);
                        }
                    }
                });
            }
            return v;
        }

        public int getCount() {
            return attivita_list.size();
        }

        public Object getItem(int position) {
            return attivita_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    }
}
