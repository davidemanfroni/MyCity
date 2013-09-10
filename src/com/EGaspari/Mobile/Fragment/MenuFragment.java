/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Davide
 */
public class MenuFragment extends Fragment {

    private List<Object> menu_list;
    private String url;
    private MenuAdapter adapter;
    private AsyncTask<Void, Void, Void> mMenuDownloader;
    private View progress_dialog;
    private ListView list_view;
    private JSONArray jsonArray;

    public MenuFragment(String url) {
        this.url = url;
    }

    public MenuFragment() {
    }

    public void download() {
        progress_dialog.setVisibility(View.VISIBLE);
        list_view.setVisibility(View.GONE);
        menu_list = new ArrayList<Object>();
        mMenuDownloader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (jsonArray == null) {
                        jsonArray = Network.getJSONArray(url);
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        MenuElement item = new MenuElement();
                        item.titolo = jsonObject.getString(Key.KEY_TITOLO);
                        String urlIcon = jsonObject.getString(Key.KEY_URL_IMMAGINE);
                        if (!"".equals(urlIcon)) {
                            item.immagine = Network.downloadBitmap(urlIcon);
                        } else {
                            item.immagine = BitmapFactory.decodeResource(getResources(), R.drawable.home_info);
                        }
                        item.comando = jsonObject.getString(Key.KEY_COMANDO);
                        item.url_next_view = jsonObject.getString(Key.KEY_URL_CONTENUTO);
                        item.id_next_view = jsonObject.getInt(Key.KEY_VIEW_SUCCESSIVA);
                        item.id = jsonObject.getInt(Key.KEY_ID_CONTENUTO);
                        menu_list.add(item);
                    }

                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                adapter = new MenuAdapter();
                list_view.setAdapter(adapter);
                progress_dialog.setVisibility(View.GONE);
                list_view.setVisibility(View.VISIBLE);
            }
        };
        mMenuDownloader.execute(null, null, null);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment, container, false);
        progress_dialog = view.findViewById(R.id.loading_spinner);
        list_view = (ListView) view.findViewById(R.id.menu_list_view);
        return view;
    }

    private class MenuAdapter extends BaseAdapter {

        public MenuAdapter() {
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            final Object item = menu_list.get(position);
            if (v == null) {
                v = getActivity().getLayoutInflater().inflate(R.layout.menu_item, parent, false);
            }
            TextView tv = (TextView) v;
            tv.setText(Utility.toUpperCaseFirstLetter(((MenuElement) item).titolo));
            tv.setTypeface(Utility.HelveticaNeueNormal(getActivity().getApplicationContext()));
            if (((MenuElement) item).immagine != null) {
                int height = (int) (tv.getPaint().getTextSize() + 15.0);
                Drawable d = resize(((MenuElement) item).immagine, height, height);
                tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            } else {
                tv.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            v.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Object item = menu_list.get(position);
                    if (item instanceof MenuElement) {
                        Intent i = null;
                        if (((MenuElement) item).comando.equals("")) {
                            i = Utility.getIntentFromId(((MenuElement) item).id_next_view, getActivity());
                        } else {
                            i = Utility.getIntentFromCommand(((MenuElement) item).comando, getActivity().getApplicationContext());
                        }
                        i.putExtra(Key.KEY_URL_CONTENUTO, ((MenuElement) item).url_next_view);
                        i.putExtra(Key.KEY_ID_CONTENUTO, ((MenuElement) item).id);
                        startActivity(i);
                    }
                }
            });
            return v;
        }

        public int getCount() {
            return menu_list.size();
        }

        public Object getItem(int position) {
            return menu_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
    }

    private class MenuElement {

        public Bitmap immagine;
        public String titolo, sotto_titolo, descrizione, url_next_view, comando;
        public int id_next_view, id;

        public MenuElement() {
            immagine = null;
            titolo = "";
            sotto_titolo = "";
            descrizione = "";
            url_next_view = "";
            id_next_view = 0;
            id = -1;
            comando = "";
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight && width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    private Drawable resize(Bitmap image, int h, int w) {
        // Converto l'immagine in un array di byte
        ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, output_stream);
        byte[] array_byte = output_stream.toByteArray();
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(array_byte, 0, array_byte.length, o);
        // Calculate inSampleSize

        o.inSampleSize = calculateInSampleSize(o, h, w);
        o.inJustDecodeBounds = false;
        return new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(array_byte, 0, array_byte.length, o));
    }
}
