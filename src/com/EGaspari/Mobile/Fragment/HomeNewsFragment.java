/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.EGaspari.Mobile.Objects.LoaderImageView;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import com.viewpagerindicator.LinePageIndicator;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Davide
 *
 */
public final class HomeNewsFragment extends Fragment {

    private List<HomeNews> home_news_list;
    private String url;
    private AsyncTask<Void, Void, Void> mHomeNewsDownloader;
    private View progress_dialog;
    private ViewPager view_pager;
    private LinePageIndicator view_pager_indicator;
    private ViewPagerAdapter view_pager_adapter;
    private Timer timer;
    private int counter;
    private final int TIME_TASK = 7;
    private JSONArray jArray;

    public HomeNewsFragment() {
    }

    public HomeNewsFragment(String url) {
        this.url = url;
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
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        home_news_list = new ArrayList<HomeNews>();
        view_pager_adapter = new ViewPagerAdapter(getChildFragmentManager());
        counter = 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            if (home_news_list.isEmpty()) {
                if (Network.isNetworkAvaliable(getActivity())) {
                    download();
                } else {
                    ErrorHelper.showNetworkError(getActivity());
                }
            }

        } catch (Exception ex) {
            //...
        }
    }

    public void download() {
        progress_dialog.setVisibility(View.VISIBLE);
        view_pager.setVisibility(View.GONE);
        view_pager_indicator.setVisibility(View.GONE);
        mHomeNewsDownloader = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    jArray = Network.getJSONArray(url);
                    for (int j = 0; j < jArray.length(); j++) {
                        JSONObject jsonObject = jArray.getJSONObject(j);
                        String imgUrl = jsonObject.getString(Key.KEY_URL_IMMAGINE);
                        String descrizione = jsonObject.getString(Key.KEY_DESCRIZIONE);
                        String title = jsonObject.getString(Key.KEY_TITOLO);
                        String underTitle = jsonObject.getString(Key.KEY_SOTTOTITOLO);
                        String url = jsonObject.getString(Key.KEY_URL_CONTENUTO);
                        int idView = jsonObject.getInt(Key.KEY_VIEW_SUCCESSIVA);
                        HomeNews h = new HomeNews();
                        h.categoria = descrizione;
                        h.titolo = title;
                        h.descrizione = underTitle;
                        h.id = j;
                        h.url_next_view = url;
                        h.nextView = idView;
                        h.url_image = imgUrl;
                        home_news_list.add(h);
                    }
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                progress_dialog.setVisibility(View.GONE);
                view_pager.setVisibility(View.VISIBLE);
                view_pager_indicator.setVisibility(View.VISIBLE);
            }
        };
        mHomeNewsDownloader.execute(null, null, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        setView();
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    private void setView() {
        view_pager.setAdapter(view_pager_adapter);
        view_pager_indicator.setViewPager(view_pager);
        view_pager_indicator.setCentered(true);
        timer = new Timer();
        final Handler mHandler = new Handler();
        final Runnable mUpdateResults = new Runnable() {
            public void run() {
                counter++;
                setSelection(counter);
            }
        };
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.post(mUpdateResults);
            }
        }, 1000, TIME_TASK * 1000);
        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int i, float f, int i1) {
            }

            public void onPageSelected(int i) {
                setSelection(i);
            }

            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void setSelection(int index) {
        counter = index;
        if (index == home_news_list.size()) {
            index = 0;
            counter = 0;
        }
        view_pager.setCurrentItem(index);
        view_pager_indicator.setCurrentItem(index);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_news_view_pager, container, false);
        progress_dialog = view.findViewById(R.id.loading_spinner);
        view_pager = (ViewPager) view.findViewById(R.id.home_view_pager);
        view_pager_indicator = (LinePageIndicator) view.findViewById(R.id.home_news_pager_indicator);
        home_news_list = new ArrayList<HomeNews>();
        view_pager_adapter = new ViewPagerAdapter(getChildFragmentManager());
        counter = 0;
        return view;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return home_news_list.size();
        }

        @Override
        public Fragment getItem(int position) {
            return News.newInstance(home_news_list.get(position));
        }
    }

    public static class News extends Fragment {

        public static News newInstance(HomeNews home_news) {
            News fr = new News();
            Bundle savedInstance = new Bundle();
            String te = home_news.descrizione;
            String ti = home_news.titolo;
            String url = home_news.url_next_view;
            String cat = home_news.categoria;
            String com = home_news.comando;
            String url_image = home_news.url_image;           
            int idView = home_news.nextView;
            savedInstance.putString("url_img", url_image);
            savedInstance.putString("testo", te);
            savedInstance.putString("titolo", ti);
            savedInstance.putString("url", url);
            savedInstance.putString("categoria", cat);
            savedInstance.putString("comando", com);
            savedInstance.putInt("id_next_view", idView);
            fr.setArguments(savedInstance);
            return fr;
        }

        public News() {
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
            View view = inflater.inflate(R.layout.home_news_fragment, container, false);
            final String tt = getArguments().getString("titolo");
            final String testo = getArguments().getString("testo");
            final String url_next = getArguments().getString("url");
            final String url_img = getArguments().getString("url_img");
            final String category = getArguments().getString("categoria");
            final String comando = getArguments().getString("comando");
            final int id_next_view = getArguments().getInt("id_next_view");
            LoaderImageView immagine = (LoaderImageView) view.findViewById(R.id.home_news_image);           
            if (!url_img.trim().equals("")) {          
                immagine.setImageDrawable(url_img);
            }else{
                immagine.setImageDrawableWithoutUrl(getResources().getDrawable(R.drawable.news_image));
            }                 
            immagine.setScaledType(ImageView.ScaleType.FIT_XY);
            TextView titolo = (TextView) view.findViewById(R.id.home_news_titolo);
            titolo.setText(Utility.toUpperCaseFirstLetter(tt));
            titolo.setTypeface(Utility.ClearfaceGothicMedium(getActivity().getApplicationContext()));
            TextView categoria = (TextView) view.findViewById(R.id.home_news_categoria);
            categoria.setText(Utility.toUpperCaseFirstLetter(category));
            categoria.setTypeface(Utility.HelveticaNeueNormal(getActivity().getApplicationContext()));
            TextView descrizione = (TextView) view.findViewById(R.id.home_news_descrizione);
            descrizione.setText(Utility.toUpperCaseFirstLetter(testo));
            descrizione.setTypeface(Utility.HelveticaNeueNormal(getActivity().getApplicationContext()));
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i;
                    if (comando.equals("")) {
                        i = Utility.getIntentFromId(id_next_view, getActivity());
                    } else {
                        i = Utility.getIntentFromCommand(comando, getActivity());
                    }
                    if (i != null) {
                        i.putExtra(Key.KEY_URL_CONTENUTO, url_next);
                        startActivity(i);
                    }
                }
            });
            return view;
        }
    }

    private class HomeNews {

        public String categoria, titolo, descrizione, url_next_view, url_image, comando;
        public Bitmap immagine;
        public int id, nextView;

        public HomeNews() {
            categoria = "";
            titolo = "";
            descrizione = "";
            url_next_view = "";
            url_image = "";
            comando = "";
            immagine = null;
            id = 0;
            nextView = 0;
        }
    }
}