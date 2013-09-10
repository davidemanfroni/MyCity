/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.EGaspari.Mobile.Utiliy.Utility;

/**
 *
 * @author Davide
 */
public class ErrorFragment extends DialogFragment{
    private String message;
    private int style = DialogFragment.STYLE_NO_TITLE;
    private int theme = android.R.style.Theme_Holo_Light_Dialog;
    public ErrorFragment() {
        // Empty constructor required for DialogFragment
    }
    
    public ErrorFragment(String message){
        this.message = message;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container);
        TextView text = (TextView) view.findViewById(R.id.error_dialog_text);
        //ImageView image = (ImageView) view.findViewById(R.id.error_dialog_image);
        setStyle(style, theme);
        text.setText(Utility.toUpperCaseFirstLetter(message));
        text.setTypeface(Utility.HelveticaNeueBold(getActivity()));
        
        return view;
    }
    
    
}
