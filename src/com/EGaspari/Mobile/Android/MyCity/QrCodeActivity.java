/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.DatabaseConnector;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import java.util.Date;

/**
 *
 * @author Davide
 */
public class QrCodeActivity extends HeaderActivity {

    int id_nuovo_qr = 0, id_elenco = 0, id_nome = 0, id_annulla, id_salva;
    private LinearLayout qr_save_view;
    private TextView nome_label, url_label;
    private EditText nome_txt, url_txt;
    private ImageButton qr_code_nuovo, qr_code_archivio;
    private ImageButton.OnClickListener qr_code_nuovo_listener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            Intent i;
            i = new Intent("com.google.zxing.client.android.SCAN");
            i.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(i, Constant.QR_CODE_REQUEST_CODE);
        }
    };
    
    private ImageButton.OnClickListener qr_code_archivio_listener = new ImageButton.OnClickListener() {

        public void onClick(View v) {
            Intent i = new Intent(QrCodeActivity.this, ElencoActivity.class);
            i.putExtra(Key.KEY_ELENCO_SELECTOR_VIEW, Constant.ELENCO_QR_CODE);
            startActivity(i);
        }
    };
    private String result = "";
    
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);     
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);   
        super.setMenuView(R.layout.qr_code_scelta);
       // super.setActivePosition(5);
        qr_code_archivio = (ImageButton) super.findView(R.id.qr_code_archivio);
        qr_code_nuovo = (ImageButton) super.findView(R.id.qr_code_nuovo);
        selectionLanguage();
        qr_code_nuovo.setOnClickListener(qr_code_nuovo_listener);
        qr_code_archivio.setOnClickListener(qr_code_archivio_listener);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        int w = p.x/2;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, w);
        qr_code_archivio.setScaleType(ImageView.ScaleType.FIT_XY);
        qr_code_nuovo.setScaleType(ImageView.ScaleType.FIT_XY);
        qr_code_archivio.setLayoutParams(params);
        qr_code_nuovo.setLayoutParams(params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            super.onActivityResult(requestCode, resultCode, intent);
            if (resultCode == RESULT_OK) {
                if (requestCode == Constant.QR_CODE_REQUEST_CODE) {
                    result = intent.getStringExtra("SCAN_RESULT");
                    if (result.startsWith("http://") || result.startsWith("https://")) {
                        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(result)), Constant.WEB_VIEW_REQUEST_CODE);
                    } else {
                        if (result.startsWith("www.")) {
                            result = "http://" + result;
                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(result)), Constant.WEB_VIEW_REQUEST_CODE);
                        } else {
                            showSavePopUp(result);
                        }
                    }
                }
            } else if (requestCode == Constant.WEB_VIEW_REQUEST_CODE) {
                showSavePopUp(result);
            }

        } catch (Exception ex) {
        }
    }

    private void showSavePopUp(String data) {
        qr_save_view = new LinearLayout(QrCodeActivity.this);
        qr_save_view.setOrientation(LinearLayout.VERTICAL);
        nome_label = new TextView(QrCodeActivity.this);
        nome_label.setText(id_nome);
        url_label = new TextView(QrCodeActivity.this);
        url_label.setText("Url");
        nome_txt = new EditText(QrCodeActivity.this);
        url_txt = new EditText(QrCodeActivity.this);
        url_txt.setText(data);
        url_txt.setEnabled(false);
        qr_save_view.addView(nome_label);
        qr_save_view.addView(nome_txt);
        qr_save_view.addView(url_label);
        qr_save_view.addView(url_txt);
        new AlertDialog.Builder(QrCodeActivity.this)
                .setView(qr_save_view).setNegativeButton(id_annulla, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(id_salva, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String nome, url;
                nome = nome_txt.getText().toString();
                url = url_txt.getText().toString();
                if (nome.equals("") || url.equals("")) {
                } else {
                    DatabaseConnector db = new DatabaseConnector(QrCodeActivity.this);
                    java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    db.insertQrCode(nome, dateFormat.format(new Date()), url);
                }
                dialog.dismiss();
                
            }
        }).create().show();
    }
    
    private void selectionLanguage() {
        
        switch (Memory.getLanguage(getApplicationContext())) {
            case Memory.ID_LANGUAGE_ITA:
                id_nome = R.string.nome_it;
                id_annulla = R.string.annulla_it;
                id_salva = R.string.salva_it;
                id_nuovo_qr = R.drawable.qr_nuovo_it;
                id_elenco = R.drawable.qr_archivio_it;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_nome = R.string.nome_en;
                id_annulla = R.string.annulla_en;
                id_salva = R.string.salva_en;
                id_nuovo_qr = R.drawable.qr_nuovo_en;
                id_elenco = R.drawable.qr_archivio_en;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_nome = R.string.nome_de;
                id_annulla = R.string.annulla_de;
                id_salva = R.string.salva_de;
                id_nuovo_qr = R.drawable.qr_nuovo_de;
                id_elenco = R.drawable.qr_archivio_de;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_nome = R.string.nome_fr;
                id_annulla = R.string.annulla_fr;
                id_salva = R.string.salva_fr;
                id_nuovo_qr = R.drawable.qr_nuovo_fr;
                id_elenco = R.drawable.qr_archivio_fr;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_nome = R.string.nome_es;
                id_annulla = R.string.annulla_es;
                id_salva = R.string.salva_es;
                id_nuovo_qr = R.drawable.qr_nuovo_es;
                id_elenco = R.drawable.qr_archivio_es;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_nome = R.string.nome_it;
                id_annulla = R.string.annulla_it;
                id_salva = R.string.salva_it;
                id_nuovo_qr = R.drawable.qr_nuovo_it;
                id_elenco = R.drawable.qr_archivio_it;
                break;
        }
        qr_code_archivio.setImageResource(id_elenco);
        qr_code_nuovo.setImageResource(id_nuovo_qr);
    }
}
