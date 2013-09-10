/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Objects;

import android.graphics.drawable.Drawable;

/**
 *
 * @author Davide
 */
public class AttivitaElement{
    
    public Drawable immagine;
    public String titolo, sotto_titolo, descrizione, url_next_view;
    public int id_next_view;
    public String url_immagine;
    
    public AttivitaElement(){
        immagine = null;
        titolo = "";
        sotto_titolo = "";
        descrizione = "";
        url_next_view = "";
        id_next_view = 0;
        url_immagine = "";
    }
    
}
