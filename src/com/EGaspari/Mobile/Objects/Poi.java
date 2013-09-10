/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Objects;

import android.graphics.Bitmap;

/**
 *
 * @author Davide
 */
public class Poi {
    public double lat, lon;
    public String titolo, sotto_titolo, descrizione;
    public Bitmap bitmap;
    
    public Poi(){
        lat = 0.0;
        lon = 0.0;
        titolo = "";
        sotto_titolo = "";
        bitmap = null;
    }
}
