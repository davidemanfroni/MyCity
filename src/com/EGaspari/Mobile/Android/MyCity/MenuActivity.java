/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.EGaspari.Mobile.Fragment.MenuFragment;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.ErrorHelper;
import com.EGaspari.Mobile.Utiliy.Key;

/**
 *
 * @author Davide
 */
public class MenuActivity extends HeaderFragmentActivity {

    private String url;
    private MenuFragment menu_fragment;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);
        super.setMenuView(R.layout.menu);
        url = getIntent().getExtras().getString(Key.KEY_URL_CONTENUTO);
        manageFragment();
    }

    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction().detach(menu_fragment).remove(menu_fragment).commit();
        this.finish();
        super.onBackPressed();
    }

    private void manageFragment() {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            menu_fragment = (MenuFragment) getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_MENU);
            if (menu_fragment == null) {
                menu_fragment = new MenuFragment(url);
                ft.addToBackStack(null);
                ft.add(R.id.menu_layout, menu_fragment, Constant.FRAGMENT_MENU);
            }
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } catch (Exception ex) {
            ErrorHelper.showFatalError(MenuActivity.this);
        }
    }
}
