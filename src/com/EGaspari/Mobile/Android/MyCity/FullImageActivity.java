/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.EGaspari.Mobile.Objects.LoaderGestureImageView;
import java.util.ArrayList;

/**
 *
 * @author Davide
 */
public class FullImageActivity extends HeaderFragmentActivity{
    
    private ViewPager view_pager;
    private int start_index;
    private ArrayList<String> list;
    private ViewPagerAdapter view_pager_adapter;
    
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        super.setMenuView(R.layout.full_image);
        view_pager = (ViewPager) super.findView(R.id.full_image_view_pager);
        list = getIntent().getExtras().getStringArrayList("urls");
        Log.i("DEBUG", list.get(0));
        view_pager_adapter = new ViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(view_pager_adapter);
        start_index = getIntent().getExtras().getInt("index");
        view_pager.setCurrentItem(start_index);
        
        
    }
    
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int position) {
            return new FullImageFragment(list.get(position));
        }
    }

    private class FullImageFragment extends Fragment {

        private String url;

        public FullImageFragment(String url) {
            this.url = url;
        }

        public FullImageFragment() {
            
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            LoaderGestureImageView img = new LoaderGestureImageView(getActivity(), url);
            LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            img.setImageLayoutParams(par);
            img.setScaledType(ImageView.ScaleType.FIT_CENTER);
            return img;

        }
    }
}
