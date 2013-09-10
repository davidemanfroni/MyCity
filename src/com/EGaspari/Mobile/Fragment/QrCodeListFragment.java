/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.EGaspari.Mobile.Objects.QrCodeCategory;
import com.EGaspari.Mobile.Objects.QrCodeElement;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.DatabaseConnector;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Davide
 */
public class QrCodeListFragment extends Fragment {

    private List<Object> qrcode_list, old_qrcode_list;
    private ListAdapter adapter;
    DatabaseConnector data_base;
    private class mDownloader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Cursor c = data_base.getAllContacts();

            String nome, data, url, id;
            String prevDate = "";
            if (c.moveToFirst()) {
                do {
                    id = c.getString(0);
                    nome = c.getString(1);
                    data = c.getString(2);
                    url = c.getString(3);
                    if (!data.equals(prevDate)) {
                        prevDate = data;
                        QrCodeCategory category = new QrCodeCategory();
                        category.testo = prevDate;
                        qrcode_list.add(category);
                    }
                    QrCodeElement qr = new QrCodeElement();
                    qr.nome = nome;
                    qr.data = data;
                    qr.url = url;
                    qrcode_list.add(qr);
                } while (c.moveToNext());
                data_base.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            show();
        }
    }

    private void show() {
        adapter = new ListAdapter();
        progress_dialog.setVisibility(View.GONE);
        list_view.setAdapter(adapter);
        list_view.setVisibility(View.VISIBLE);
    }
    private ListView list_view;
    private View progress_dialog;

    public QrCodeListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        data_base = new DatabaseConnector(getActivity().getApplicationContext());
        new mDownloader().execute(null, null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.elenco_list_fragment, container, false);
        list_view = (ListView) view.findViewById(R.id.elenco_list_view);
        qrcode_list = new ArrayList<Object>();
        progress_dialog = view.findViewById(R.id.loading_spinner);
        list_view.setVisibility(View.GONE);
        return view;
    }

    public void search(String textSearch) {
        progress_dialog.setVisibility(View.VISIBLE);
        list_view.setVisibility(View.GONE);
        if (old_qrcode_list == null) {
            old_qrcode_list = qrcode_list;
        }
        qrcode_list = new ArrayList<Object>();
        for (int i = 0; i < old_qrcode_list.size(); i++) {
            Object item = old_qrcode_list.get(i);
            if (item instanceof QrCodeElement) {
                if ((((QrCodeElement) item).data.toLowerCase().contains(textSearch.toLowerCase()))
                        || (((QrCodeElement) item).url.toLowerCase().contains(textSearch.toLowerCase()))
                        || (((QrCodeElement) item).nome.toLowerCase().contains(textSearch.toLowerCase()))) {
                    qrcode_list.add(item);
                }
            } else if (item instanceof QrCodeCategory) {
                qrcode_list.add(item);
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
            final Object item = qrcode_list.get(position);
            if (item instanceof QrCodeCategory) {
                if (v == null) {
                    v = getActivity().getLayoutInflater().inflate(R.layout.elenco_qrcode_category, parent, false);
                }
                TextView txt = (TextView) v;
                txt.setText(((QrCodeCategory) item).testo);

            } else if (item instanceof QrCodeElement) {
                if (v == null) {
                    v = getActivity().getLayoutInflater().inflate(R.layout.elenco_qrcode_item, parent, false);
                }

                TextView txt = (TextView) v;
                txt.setText(((QrCodeElement) item).nome);
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String content = ((QrCodeElement) item).url;
                        if (content.startsWith("http://") || content.startsWith("https://")) {
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(content)), Constant.WEB_VIEW_REQUEST_CODE);
                        } else {
                            if (content.startsWith("www.")) {
                                content = "http://" + content;
                                startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(content)), Constant.WEB_VIEW_REQUEST_CODE);
                            } else {
                                new AlertDialog.Builder(getActivity()).setTitle(((QrCodeElement) item).nome)
                                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setMessage(content).create().show();
                            }
                        }
                    }
                });
            }

            return v;
        }

        public int getCount() {
            return qrcode_list.size();
        }

        public Object getItem(int position) {
            return qrcode_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    }
}
