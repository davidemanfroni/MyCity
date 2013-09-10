/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

//**insert package name**
import android.app.Activity;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.EGaspari.Mobile.Objects.LoaderGestureImageView;
import com.EGaspari.Mobile.Objects.LoaderImageView;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Network;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;

public class GalleryActivity extends HeaderFragmentActivity {

    private final LinkedList<String> listThumbnail = new LinkedList<String>();
    private final LinkedList<String> listUrl = new LinkedList<String>();
    private PagerAdapter view_pager_adapter;
    private GridView grid;
    private String url;
    private AdapterGridView adapterGridView;
    private ViewPager view_pager;
    private int nFoto, index = 0;
    private RelativeLayout layout_view_pager, layout_grid_view;
    private View progress_dialog;
    private JSONArray json_array_thumbnail;
    private boolean full_screen;

    @Override
    protected void onPause() {
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);
        super.setMenuView(R.layout.gallery);
        grid = (GridView) super.findView(R.id.gallery_grid_view);
        view_pager = (ViewPager) super.findView(R.id.gallery_view_pager);
        layout_grid_view = (RelativeLayout) super.findView(R.id.layout_grid_view);
        layout_view_pager = (RelativeLayout) super.findView(R.id.layout_view_pager);
        progress_dialog = super.findView(R.id.loading_spinner);
        progress_dialog.setVisibility(View.VISIBLE);
        layout_grid_view.setVisibility(View.GONE);
        layout_view_pager.setVisibility(View.GONE);
        view_pager_adapter = new ViewPagerAdapter(getSupportFragmentManager());
        url = getIntent().getExtras().getString(Key.KEY_URL_CONTENUTO);
        full_screen = false;
        getGallery();

    }

    private void getGallery() {
        if (Network.isNetworkAvaliable(getApplicationContext())) {
            new HttpGetSize().execute();
        } else {
            ErrorHelper.showNetworkError(GalleryActivity.this);
        }
    }

    private class HttpGetSize extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                json_array_thumbnail = Network.getJSONArray(url);
                if (json_array_thumbnail != null) {
                    for (int i = 0; i < json_array_thumbnail.length(); i++) {
                        JSONObject obj = json_array_thumbnail.getJSONObject(i);
                        listUrl.add(obj.getString(Key.KEY_URL_CONTENUTO));
                        listThumbnail.add(obj.getString(Key.KEY_URL_IMMAGINE));
                    }
                }

            } catch (Exception ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            setDefaultAdapter();
        }
    }

    private void setDefaultAdapter() {
        adapterGridView = new AdapterGridView(GalleryActivity.this, listThumbnail, 0);
        grid.setAdapter(adapterGridView);
        progress_dialog.setVisibility(View.GONE);
        grid.setVisibility(View.VISIBLE);
        layout_grid_view.setVisibility(View.VISIBLE);
        view_pager.setAdapter(view_pager_adapter);
        setup();
    }

    private void setup() {
        try {
            adapterGridView = new AdapterGridView(GalleryActivity.this, listThumbnail, 1);
            grid.setAdapter(adapterGridView);
            grid.setOnItemClickListener(new GridView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    grid.setVisibility(View.GONE);
                    index = position;
                    layout_view_pager.setVisibility(View.VISIBLE);
                    view_pager.setCurrentItem(index);
                    full_screen = true;

                }
            });
        } catch (Exception ex) {
        }
    }

    @Override
    public void onBackPressed() {
        if (full_screen) {
            layout_view_pager.setVisibility(View.GONE);
            grid.setVisibility(View.VISIBLE);
            full_screen = false;
        } else {
            this.finish();
        }
    }

    public class AdapterGridView extends BaseAdapter {

        private Activity mContext;
        private LinkedList<String> bitmaps;
        private int modeview;

        // Constructor
        public AdapterGridView(Activity c, LinkedList<String> bitmaps, int modeView) {
            mContext = c;
            this.bitmaps = bitmaps;
            this.modeview = modeView;
        }

        public void setList(LinkedList<String> bitmaps) {
            this.bitmaps = bitmaps;
        }

        @Override
        public int getCount() {
            return bitmaps.size();
        }

        @Override
        public Object getItem(int position) {
            return bitmaps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LoaderImageView imageView = new LoaderImageView(mContext, bitmaps.get(position));


            try {
                

                int Measuredwidth = 0;
                Point size = new Point();
                WindowManager w = mContext.getWindowManager();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    w.getDefaultDisplay().getSize(size);
                    Measuredwidth = size.x;
                } else {
                    Display d = w.getDefaultDisplay();
                    Measuredwidth = d.getWidth();
                }
                Measuredwidth = (Measuredwidth - 18) / 3;
                imageView.setImageLayoutParams(new LinearLayout.LayoutParams(Measuredwidth, Measuredwidth));
                imageView.setPadding(3, 3, 3, 3);
                imageView.setScaledType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageDrawable(bitmaps.get(position));
            } catch (Exception ex) {
            }
            return imageView;
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return listUrl.size();
        }

        @Override
        public Fragment getItem(int position) {
            return new GalleryFullImageFragment(listUrl.get(position));
        }
    }

    private class GalleryFullImageFragment extends Fragment {

        private String url;
        private LoaderGestureImageView image_view;

        public GalleryFullImageFragment(String url) {
            this.url = url;
        }

        public GalleryFullImageFragment() {
            this.url = "";
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.gallery_full_image, container, false);
            image_view = (LoaderGestureImageView) v.findViewById(R.id.gallery_full_image_imageview);
            image_view.setImageDrawable(url);
            image_view.setScaledType(ImageView.ScaleType.FIT_CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            image_view.setImageLayoutParams(params);
            return v;

        }
    }
}
