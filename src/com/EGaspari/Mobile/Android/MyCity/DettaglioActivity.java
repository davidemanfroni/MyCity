/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.EGaspari.Mobile.Fragment.AttivitaListFragment;
import com.EGaspari.Mobile.Objects.Dettaglio;
import com.EGaspari.Mobile.Objects.LoaderImageView;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.LinePageIndicator;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Davide
 */
public class DettaglioActivity extends HeaderFragmentActivity {

    private String url;
    private AsyncTask<Void, Void, Void> mDownloaderDettaglio;
    private Dettaglio dettaglio;
    private ArrayList<String> list_image;
    private List<Object> list_marker;
    private AsyncTask<Void, Void, Void> mDownloadMap;
    private TextView titolo, sotto_titolo, descrizione;
    private ScrollView scroll_view;
    private View progressDialog;
    private SupportMapFragment fragment_map;
    private ViewPager view_pager;
    private PagerAdapter view_pager_adapter;
    private AsyncTask<Void, Void, Void> mDownloadGallery;
    private GoogleMap map;
    private AttivitaListFragment attivita_fragment;
    private FrameLayout map_container, scroll_view_container, attivita_container;
    private LinePageIndicator view_pager_indicator;
    private ImageButton.OnClickListener share = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, dettaglio.titolo + "\n" + dettaglio.sottoTitolo + "\n" + dettaglio.descrizione);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.condividi_it)));
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
        super.setMenuView(R.layout.dettaglio);
        super.setMenuButton(BitmapFactory.decodeResource(getResources(), R.drawable.share), share, View.VISIBLE);
        titolo = (TextView) super.findView(R.id.dettaglio_titolo);
        map_container = (FrameLayout) super.findView(R.id.dettaglio_map_container);
        attivita_container = (FrameLayout) super.findView(R.id.dettaglio_elenco_fragment_container);
        scroll_view_container = (FrameLayout) super.findView(R.id.dettaglio_scroll_view_framelayout);
        sotto_titolo = (TextView) super.findView(R.id.dettaglio_sotto_titolo);
        descrizione = (TextView) super.findView(R.id.dettaglio_descrizione);
        view_pager = (ViewPager) super.findView(R.id.dettaglio_view_pager);
        scroll_view = (ScrollView) super.findView(R.id.dettaglio_scroll_view);
        progressDialog = super.findView(R.id.loading_spinner);
        
        view_pager_indicator = (LinePageIndicator) super.findView(R.id.dettaglio_pager_indicator);
        scroll_view.setVisibility(View.GONE);
        url = getIntent().getExtras().getString(Key.KEY_URL_CONTENUTO);
        dettaglio = new Dettaglio();
        list_image = new ArrayList<String>();
        list_marker = new ArrayList<Object>();
        if (Network.isNetworkAvaliable(DettaglioActivity.this)) {
            downloadDettaglio();
        } else {
            ErrorHelper.showNetworkError(DettaglioActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        if (attivita_fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(attivita_fragment);
        }
        if (fragment_map != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment_map);
        }
        this.finish();
        super.onBackPressed();
    }

    private void downloadDettaglio() {
        mDownloaderDettaglio = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONArray jsonArray = Network.getJSONArray(url);
                    if (jsonArray != null) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String titolo = jsonObject.getString(Key.KEY_TITOLO);
                        String sottotitolo = jsonObject.getString(Key.KEY_SOTTOTITOLO);
                        String descrizione = jsonObject.getString(Key.KEY_DESCRIZIONE);
                        String urlGallery = jsonObject.getString(Key.KEY_URL_GALLERY);
                        String urlFooter = jsonObject.getString(Key.KEY_URL_FOOTER_DETTAGLI);
                        String urlMappe = jsonObject.getString(Key.KEY_URL_MAPPA);
                        String lat = jsonObject.getString(Key.KEY_LATITUDINE).trim();
                        String lon = jsonObject.getString(Key.KEY_LONGITUDINE).trim();
                        if ((lat.equals("") || lon.equals(""))) {
                            dettaglio.lat = 0.0;
                            dettaglio.lon = 0.0;
                        } else {
                            dettaglio.lat = Double.parseDouble(lat);
                            dettaglio.lon = Double.parseDouble(lon);
                        }
                        dettaglio.titolo = titolo;
                        dettaglio.sottoTitolo = sottotitolo;
                        dettaglio.descrizione = descrizione;
                        dettaglio.linkFotoGallery = urlGallery;
                        dettaglio.linkMappe = urlMappe;
                        dettaglio.linkFooter = urlFooter;
                    }
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setViewAfterFirstDownload();
            }
        };
        mDownloaderDettaglio.execute(null, null, null);

    }

    private void setViewAfterFirstDownload() {
        try {
            /*Rendo visibile la view*/

            progressDialog.setVisibility(View.GONE);
            scroll_view.setVisibility(View.VISIBLE);

            /*Testo*/
            if (dettaglio.titolo != null && !dettaglio.titolo.equals("")) {
                titolo.setText(Utility.toUpperCaseFirstLetter(dettaglio.titolo));
                titolo.setVisibility(View.VISIBLE);
                titolo.setTypeface(Utility.HelveticaNeueBold(getApplicationContext()));
            } else {
                titolo.setVisibility(View.GONE);
            }
            if (dettaglio.sottoTitolo != null && !dettaglio.sottoTitolo.equals("")) {
                sotto_titolo.setText(Utility.toUpperCaseFirstLetter(dettaglio.sottoTitolo));
                sotto_titolo.setVisibility(View.VISIBLE);
                sotto_titolo.setTypeface(Utility.HelveticaNeueNormal(getApplicationContext()));
            } else {
                sotto_titolo.setVisibility(View.GONE);
            }
            if (dettaglio.descrizione != null && !dettaglio.descrizione.equals("")) {
                descrizione.setText(Utility.toUpperCaseFirstLetter(dettaglio.descrizione));
                descrizione.setVisibility(View.VISIBLE);
                descrizione.setTypeface(Utility.HelveticaNeueNormal(getApplicationContext()));
            } else {
                descrizione.setVisibility(View.GONE);
            }

            /*Scarico mappa*/
            mDownloadMap = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        JSONArray jsonarray = Network.getJSONArray(dettaglio.linkMappe);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject obj = jsonarray.getJSONObject(i);
                            MarkerOptions mark = new MarkerOptions();
                            Double lat = obj.getDouble(Key.KEY_LATITUDINE);
                            Double lon = obj.getDouble(Key.KEY_LONGITUDINE);
                            String titolo = obj.getString((Key.KEY_TITOLO));
                            String sotto_titolo = obj.getString(Key.KEY_SOTTOTITOLO);
                            mark.position(new LatLng(lat, lon));
                            mark.title(titolo);
                            mark.snippet(sotto_titolo);
                            list_marker.add(mark);
                        }
                    } catch (Exception ex) {
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    setMap();
                }
            };

            /*Scarico la gallery*/
            mDownloadGallery = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        JSONArray jsonarray = Network.getJSONArray(dettaglio.linkFotoGallery);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject obj = jsonarray.getJSONObject(i);
                            if (!obj.getString(Key.KEY_URL_IMMAGINE).equals("")) {
                                list_image.add(obj.getString(Key.KEY_URL_IMMAGINE));
                            }
                        }
                    } catch (Exception ex) {
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    setGallery();
                }
            };

            /*Gestisco i vari fragment*/
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            if (dettaglio.lat == 0.0 && dettaglio.lon == 0.0) {
                map_container.setVisibility(View.GONE);
                fragment_map = null;
                scroll_view_container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            } else {
                if (dettaglio.linkMappe != null && !dettaglio.linkMappe.trim().equals("")) {
                    fragment_map = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_MAP);
                    if (fragment_map == null) {
                        fragment_map = new SupportMapFragment();
                        ft.addToBackStack(null);
                        ft.add(R.id.dettaglio_map_container, fragment_map, Constant.FRAGMENT_MAP);
                        mDownloadMap.execute(null, null, null);
                    }
                } else {
                    setMap();
                }

            }




            /*Footer*/
            if (dettaglio.linkFooter != null && !dettaglio.linkFooter.equals("")) {
                attivita_fragment = (AttivitaListFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_ATTIVITA);
                if (attivita_fragment == null) {
                    attivita_fragment = new AttivitaListFragment(dettaglio.linkFooter, 1);
                    ft.addToBackStack(null);
                    ft.add(R.id.dettaglio_elenco_fragment_container, attivita_fragment, Constant.FRAGMENT_ATTIVITA);
                }
            } else {
                attivita_fragment = null;
            }

            /*Gallery*/
            if (dettaglio.linkFotoGallery != null && !dettaglio.linkFotoGallery.equals("")) {
                mDownloadGallery.execute(null, null, null);
            } else {
                view_pager.setVisibility(View.GONE);
            }
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } catch (Exception ex) {
            ErrorHelper.showFatalError(DettaglioActivity.this);
        }
    }

    private void setGallery() {
        if (list_image.isEmpty()) {
            view_pager.setVisibility(View.GONE);
        } else {
            view_pager_adapter = new ViewPagerAdapter(getSupportFragmentManager());
            view_pager.setAdapter(view_pager_adapter);
            view_pager_indicator.setViewPager(view_pager);
            view_pager.setVisibility(View.VISIBLE);
            view_pager.setOnTouchListener(new View.OnTouchListener() {
                int dragthreshold = 150;
                int downX;
                int downY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = (int) event.getRawX();
                            downY = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int distanceX = Math.abs((int) event.getRawX() - downX);
                            int distanceY = Math.abs((int) event.getRawY() - downY);
                            if (distanceY > distanceX && distanceY > dragthreshold) {
                                view_pager.getParent().requestDisallowInterceptTouchEvent(false);
                                scroll_view.getParent().requestDisallowInterceptTouchEvent(true);
                            } else if (distanceX > distanceY && distanceX > dragthreshold) {
                                view_pager.getParent().requestDisallowInterceptTouchEvent(true);
                                scroll_view.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            scroll_view.getParent().requestDisallowInterceptTouchEvent(false);
                            view_pager.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private void setMap() {
        map = fragment_map.getMap();
        map.clear();
        MarkerOptions mark = new MarkerOptions();
        mark.position(new LatLng(dettaglio.lat, dettaglio.lon));
        mark.title(dettaglio.titolo);
        map.addMarker(mark);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dettaglio.lat, dettaglio.lon), 17));
        
        for (int i = 0; i < list_marker.size(); i++) {
            map.addMarker(((MarkerOptions) list_marker.get(i)));
        }
        map.setMyLocationEnabled(true);
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
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return list_image.size();
        }

        @Override
        public Fragment getItem(int position) {
            return new GalleryImage((list_image.get(position)), position);
        }
    }

    private class GalleryImage extends Fragment {

        private String image;
        private int index;

        public GalleryImage(String image, int index) {
            this.image = image;
            this.index = index;
        }

        public GalleryImage() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            LoaderImageView imageView = new LoaderImageView(DettaglioActivity.this, image);
            imageView.setImageDrawable(image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageView.setImageLayoutParams(params);
            imageView.setScaledType(ImageView.ScaleType.FIT_CENTER);
            imageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent i = new Intent(DettaglioActivity.this, FullImageActivity.class);
                   
                    i.putStringArrayListExtra("urls", list_image);
                    Log.i("DEBUG", list_image.get(0));
                    i.putExtra("index", index);
                    startActivity(i);
                }
            });
            return imageView;
        }
    }
}
