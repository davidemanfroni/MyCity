/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

/**
 *
 * @author Davide
 */
public class HeaderFragmentActivity extends SherlockFragmentActivity {

    private ImageButton support_button, logo, menu;
    private ActionBar actionBar;
    protected MenuDrawer menuDrawer;
    protected ListView menuList;
    private MenuAdapter menuAdapter;
    private List<Object> menuItems;
    private TextView search_box;

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = null;
            if (((Item) menuItems.get(position)).mComando.equals("")) {
                i = Utility.getIntentFromId(((Item) menuItems.get(position)).mNext_view, getApplicationContext());
            } else {
                i = Utility.getIntentFromCommand(((Item) menuItems.get(position)).mComando, getApplicationContext());
            }
            i.putExtra(Key.KEY_URL_CONTENUTO, ((Item) menuItems.get(position)).mUrl);
            startActivity(i);
            menuDrawer.closeMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        actionBar = getSupportActionBar();
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.custom_action_bar, null));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        logo = (ImageButton) actionBar.getCustomView().findViewById(R.id.action_bar_button_logo);
        menu = (ImageButton) actionBar.getCustomView().findViewById(R.id.action_bar_button_menu);
        support_button = (ImageButton) actionBar.getCustomView().findViewById(R.id.action_bar_button_support);
        menuDrawer = MenuDrawer.attach(this, Position.RIGHT);
        setMenuDrawerView();

        menu.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                menuDrawer.toggleMenu();
            }
        });
        logo.setClickable(Constant.COMMON_BUTTON_ENABLE);
    }

    protected void setMenuButton(Bitmap icon, ImageButton.OnClickListener listener, int visibility) {
        support_button.setOnClickListener(listener);
        support_button.setImageBitmap(icon);
        support_button.setVisibility(visibility);
    }


    protected void setMenuDrawerView() {  
        View v = getLayoutInflater().inflate(R.layout.menu_drawer, null);
        menuList = (ListView) v.findViewById(R.id.menu_drawer_list_view);
        search_box = (TextView) v.findViewById(R.id.menu_drawer_search_textview);
        selectionLanguage();
        search_box.setOnEditorActionListener(new DoneOnEditorActionListener());
        search_box.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.search_white), null, null, null);
        search_box.setCompoundDrawablePadding(10);
        downloadItems();
        menuAdapter = new MenuAdapter(menuItems);
        menuList.setAdapter(menuAdapter);
        menuList.setOnItemClickListener(mItemClickListener);
        menuList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                menuDrawer.invalidate();
            }
        });
        menuDrawer.setMenuView(v);
    }

    @Override
    public void onBackPressed() {
        final int drawerState = menuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN || drawerState == MenuDrawer.STATE_OPENING) {
            menuDrawer.closeMenu();
            return;
        }

        super.onBackPressed();
    }


    protected void setMenuView(int id) {
        menuDrawer.setContentView(id);
    }

    protected View findView(int id) {
        return (View) findViewById(id);
    }

    public void downloadItems() {
        menuItems = new ArrayList<Object>();
        menuItems.clear();
        menuItems.add("Menu");
        SharedPreferences pref = getSharedPreferences(Key.APPLICATION_KEY_STORAGE + "_" + Key.MENU_DRAWER_ITEMS, MODE_PRIVATE);
        int size = pref.getInt(Key.KEY_SIZE, 0);
        for (int i = 0; i < size; i++) {
            Item item = new Item();
            String nome = pref.getString(Key.KEY_TITOLO + String.valueOf(i), "");
            String url = pref.getString(Key.KEY_URL_CONTENUTO + String.valueOf(i), "");
            String comando = pref.getString(Key.KEY_COMANDO + String.valueOf(i), "");
            int id_view = Integer.valueOf(pref.getString(Key.KEY_VIEW_SUCCESSIVA + String.valueOf(i), "0"));
            String icon_base_64 = pref.getString(Key.KEY_URL_CONTENUTO_2 + String.valueOf(i), "");
            byte[] data = Base64.decode(icon_base_64, Base64.DEFAULT);
            Bitmap icon = BitmapFactory.decodeByteArray(data, 0, data.length);         
            item.mComando = comando;
            item.bitmap = icon;
            item.mNext_view = id_view;
            item.mTitle = nome;
            item.mUrl = url;
            menuItems.add(item);   
        }
        Item home = new Item();
        home.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.menu_home);
        home.mComando = "home";
        home.mTitle = "Home";
        menuItems.add(1, home);
        /*
        menuItems.add("Comuni");
        SharedPreferences pref_comuni = getSharedPreferences(Key.MULTI_COMUNE_ITEMS, MODE_PRIVATE);
        size = pref_comuni.getInt(Key.KEY_SIZE, 0);
        Log.w("DEBUG", String.valueOf(size));
        for (int i = 0; i < size; i++) {
            Item item = new Item();
            String nome = pref_comuni.getString(Key.KEY_TITOLO + String.valueOf(i), "");
            String url = pref_comuni.getString(Key.KEY_URL_CONTENUTO + String.valueOf(i), "");
            String comando = pref_comuni.getString(Key.KEY_COMANDO + String.valueOf(i), "");
            int id_view = Integer.valueOf(pref_comuni.getString(Key.KEY_VIEW_SUCCESSIVA + String.valueOf(i), "0"));
            String icon_base_64 = pref_comuni.getString(Key.KEY_URL_CONTENUTO_2 + String.valueOf(i), "");
            byte[] data = Base64.decode(icon_base_64, Base64.DEFAULT);
            Bitmap icon = BitmapFactory.decodeByteArray(data, 0, data.length);         
            item.mComando = comando;
            item.bitmap = icon;
            item.mNext_view = id_view;
            item.mTitle = nome;
            item.mUrl = url;
            menuItems.add(item);   
        }*/
        
    }

    private static class Item {

        String mTitle;
        int mIconRes;
        String mComando;
        int mNext_view;
        String mUrl;
        Bitmap bitmap;

        Item(String t, int i, String u, String c, int v) {
            mTitle = t;
            mIconRes = i;
            mUrl = u;
            mComando = c;
            mNext_view = v;
            bitmap = null;
        }

        Item() {
        }
    }
    

    private class MenuAdapter extends BaseAdapter {

        private List<Object> mItems;

        MenuAdapter(List<Object> items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof Item ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position) instanceof Item;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            Object item = getItem(position);
            View v = null;
            if(item instanceof Item){
            
                v = getLayoutInflater().inflate(R.layout.menu_drawer_item, parent, false);
            TextView tv = (TextView) v;
            tv.setText(((Item) item).mTitle);
            if(((Item)item).bitmap != null){
                int height = (int) (tv.getPaint().getTextSize() + 15.0);
                Drawable d = resize(((Item) item).bitmap, height, height);
                tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            }
           // Drawable d = resize(((Item)item).bitmap);
            //tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);

            }else if(item instanceof String){
                v = getLayoutInflater().inflate(R.layout.menu_drawer_comune, parent, false);
                TextView tv = (TextView) v;
                tv.setText((String)item);
            }
            
            return v;
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
    
    private class DoneOnEditorActionListener implements TextView.OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String textSearch = v.getText().toString();
                if(!textSearch.equals("")){
                String url_search = getSharedPreferences(Key.APPLICATION_KEY_STORAGE + "_" + Key.MENU_DRAWER_ITEMS, MODE_PRIVATE).getString(Key.KEY_URL_FILTRI, "");
                if(!url_search.equals("")){
                    url_search = Network.setUrlWithText(textSearch, url_search);
                }
                Intent intent = new Intent(HeaderFragmentActivity.this, MenuActivity.class);
                intent.putExtra(Key.KEY_URL_CONTENUTO, url_search);
                startActivity(intent); 
                }
            }
            return true;
        }
    }
    
    private void selectionLanguage(){
        int id_search = 0;
        switch (Memory.getLanguage(getApplicationContext())) {
            case Memory.ID_LANGUAGE_ITA:
                id_search = R.string.search_box_hint_it;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_search = R.string.search_box_hint_en;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_search = R.string.search_box_hint_de;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_search = R.string.search_box_hint_fr;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_search = R.string.search_box_hint_es;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_search = R.string.search_box_hint_it;
                break;
        }
        search_box.setHint(id_search);
        
    }
}
