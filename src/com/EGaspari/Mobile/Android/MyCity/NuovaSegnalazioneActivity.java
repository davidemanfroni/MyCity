/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.EGaspari.Mobile.Android.MyCity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.EGaspari.Mobile.Utiliy.Constant;
import com.EGaspari.Mobile.Utiliy.Key;
import com.EGaspari.Mobile.Utiliy.Memory;
import com.EGaspari.Mobile.Utiliy.Network;
import com.EGaspari.Mobile.Utiliy.Utility;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 *
 * @author Davide
 */
public class NuovaSegnalazioneActivity extends HeaderActivity{

    private GpsTracker gps;
    private EditText nome, email, oggetto, luogo, descrizione;
    private TextView nomeLbl, oggettoLbl, luogoLbl, categoriaLbl;
    private ImageButton scatta_foto, scegli_foto;
    private LinearLayout layout_lista_foto;
    private List<File> lista_foto;
    private LinearLayout.LayoutParams layoutButton;
    private Spinner categoria;
    private String username, password;
    private int id_categorie[], id_comuni[];
    private int id_scatta_foto, id_scegli_foto, id_nome, id_luogo, id_oggetto, id_email, id_descrizione,
            id_categoria, id_riempi_campi, id_invio_successo, id_invio_errore;
    
    private class mSender extends AsyncTask<Void, Void, Void>  {
        @Override
        protected Void doInBackground(Void... params) {
            String n = nome.getText().toString();
            String e = email.getText().toString();
            String o = oggetto.getText().toString();
            String l = luogo.getText().toString();
            String d = descrizione.getText().toString();
            int j = id_categorie[categoria.getSelectedItemPosition()];
            //int k = id_comuni[comuni.getSelectedItemPosition()];
            double lat = gps.getLatitude();
            double lon = gps.getLongitude();
            try {
                if (!n.equals("") && !e.equals("") && !o.equals("") && !l.equals("") && !d.equals("")) {

                    String urlRichiesta = Memory.getUrlRadice(getApplicationContext());
                    urlRichiesta = urlRichiesta.replace("?urlcommand=%3F", "");
                    String urlParametri = "?action=150";
                    urlParametri = Network.addUidLanguageServerTime(urlParametri, getApplicationContext());
                    urlParametri += "&nome=" + n + "&email=" + e + "&oggetto=" + o + "&luogo=" + l;
                    urlParametri += "&categoria=" + j + "&descrizione=" + d + "&lat=" + lat + "&lng=" + lon;
                    urlParametri += "&dispositivo=" + "Android" + "&username=" + username + "&password=" + password;
                    urlParametri = Network.addIdComune(urlParametri, Constant.ID_COMUNE);
                    if (lista_foto.size() > 0) {
                        for (int i = 1; i <= lista_foto.size(); i++) {
                            urlParametri += "&immagine" + String.valueOf(i) + "=" + lista_foto.get(i - 1).getName();
                            urlParametri += "&immagineupload" + String.valueOf(i) + "=";
                            Bitmap compressed = Utility.decodeFile(lista_foto.get(i - 1));
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            compressed.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            String ba1 = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
                            urlParametri += ba1;
                        }
                    }
                    urlParametri = urlParametri.replace("%26", "&");
                    urlParametri = urlParametri.replace("%3D", "=");
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("urlcommand", urlParametri));
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(urlRichiesta);
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
                    entity.setContentEncoding(HTTP.UTF_8);
                    entity.setContentType("application/x-www-form-urlencoded");
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                    httppost.setHeader("Accept", "application/x-www-form-urlencoded");
                    HttpResponse response = httpclient.execute(httppost);
                    Toast.makeText(NuovaSegnalazioneActivity.this, "Segnalazione inviata con successo", Toast.LENGTH_LONG);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
    private ImageButton.OnClickListener scatta_foto_listener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            try {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + Constant.APP_NAME);
                dir.mkdirs();
                String name = String.valueOf(System.currentTimeMillis()) + ".jpg";
                File f = new File(dir, name);
                lista_foto.add(f);
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(i, Constant.CAMERA_INTENT_REQUEST_CODE);
            } catch (Exception ex) {
            }
        }
    };
    private ImageButton.OnClickListener scegli_foto_listener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, Constant.LOAD_IMAGE_INTENT_RQUEST_CODE);
        }
    };
    private ImageButton.OnClickListener invio_segnalazione_listener = new ImageButton.OnClickListener() {
        public void onClick(View v) {
            String n = nome.getText().toString();
            String e = email.getText().toString();
            String o = oggetto.getText().toString();
            String l = luogo.getText().toString();
            String d = descrizione.getText().toString();
            
            if (!n.equals("") && !e.equals("") && !o.equals("") && !l.equals("") && !d.equals("")) {
                mSender s = new mSender();
                s.execute(null, null, null);
                finish();
            } else {
                new AlertDialog.Builder(NuovaSegnalazioneActivity.this)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage(id_riempi_campi).create().show();
            }
           
        }
    };
    private TextView comuniLbl;
    private Spinner comuni;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
    
    @Override
    protected void onResume(){
        super.onResume();
        if(!gps.canGetLocation()){
            gps.showSettingsAlert();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        overridePendingTransition(R.anim.slide_in_left, R.anim.hold);
        super.setMenuView(R.layout.nuova_segnalazione);
        super.setMenuButton(BitmapFactory.decodeResource(getResources(), R.drawable.send), invio_segnalazione_listener, View.VISIBLE);
        gps = new GpsTracker(NuovaSegnalazioneActivity.this);
        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        int w = p.x / 2;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, w);
        scatta_foto = (ImageButton) super.findView(R.id.nuova_segnalazione_scatta_foto);
        scatta_foto.setOnClickListener(scatta_foto_listener);
        scatta_foto.setLayoutParams(params);
        scatta_foto.setScaleType(ImageView.ScaleType.FIT_XY);
        scegli_foto = (ImageButton) super.findView(R.id.nuova_segnalazione_scegli_foto);
        scegli_foto.setOnClickListener(scegli_foto_listener);
        scegli_foto.setLayoutParams(params);
        scegli_foto.setScaleType(ImageView.ScaleType.FIT_XY);
        nome = (EditText) super.findView(R.id.segnalazioninometxt);
        nomeLbl = (TextView) super.findView(R.id.segnalazioninomelbl);
        email = (EditText) super.findView(R.id.segnalazioniemailtxt);
        oggetto = (EditText) super.findView(R.id.segnalazionioggettotxt);
        oggettoLbl = (TextView) super.findView(R.id.segnalazionioggettolbl);
        luogo = (EditText) super.findView(R.id.segnalazioniluogotxt);
        luogoLbl = (TextView) super.findView(R.id.segnalazioniluogolbl);
        categoriaLbl = (TextView) super.findView(R.id.segnalazionicategorialbl);
        comuniLbl = (TextView) super.findView(R.id.segnalazionicomunilbl);
        comuni = (Spinner) super.findView(R.id.segnalazionicomunispinner);
        comuni.setVisibility(Constant.VISIBILITY_COMUNE_SEGNALAZIONE);
        comuniLbl.setVisibility(Constant.VISIBILITY_COMUNE_SEGNALAZIONE);
        descrizione = (EditText) super.findView(R.id.segnalazionidescrizionetxt);
        categoria = (Spinner) super.findView(R.id.segnalazionicategoriaspinner);
        layout_lista_foto = (LinearLayout) super.findView(R.id.nuova_segnalazione_lista_foto_layout);
        ArrayAdapter<String> categorie_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getIntent().getExtras().getStringArray(Key.KEY_CATEGORIE));
        categorie_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(categorie_adapter);
        ArrayAdapter<String> comuni_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getIntent().getExtras().getStringArray(Key.KEY_COMUNI));
        comuni_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comuni.setAdapter(comuni_adapter);
        id_categorie = getIntent().getExtras().getIntArray(Key.KEY_ID_CATEGORIE);
        id_comuni = getIntent().getExtras().getIntArray(Key.KEY_ID_COMUNI);
        username = getIntent().getExtras().getString(Key.KEY_USERNAME);
        password = getIntent().getExtras().getString(Key.KEY_PASSWORD);
        lista_foto = new ArrayList<File>();
        selectionLanguage();
    }

    private void controlFromCamera(Bundle savedInstance) {
        if (savedInstance != null) {
            String[] s = savedInstance.getStringArray("OUTPUT");
            for (int i = 0; i < s.length; i++) {
                lista_foto.add(new File(s[i]));
                ImageView image = new ImageView(this);
                image.setImageURI(Uri.fromFile(new File(s[i])));
                image.setMaxHeight(layoutButton.height);
                image.setMaxWidth(layoutButton.width);
                image.setLayoutParams(layoutButton);
                image.setScaleType(ImageView.ScaleType.CENTER);
                layout_lista_foto.addView(image);
                layout_lista_foto.invalidate();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.LOAD_IMAGE_INTENT_RQUEST_CODE && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                File f = new File(picturePath);

                lista_foto.add(f);
            }

        } else if (resultCode == RESULT_CANCELED && requestCode == Constant.CAMERA_INTENT_REQUEST_CODE) {
            lista_foto.remove(lista_foto.size() - 1);
        }
        layout_lista_foto.removeAllViewsInLayout();
        for (int i = 0; i < lista_foto.size(); i++) {
            ImageView image = new ImageView(this);
            Bitmap b = Utility.decodeFile(lista_foto.get(i));
            image.setImageBitmap(b);
            image.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setPadding(5, 5, 5, 5);
            layout_lista_foto.addView(image);
            layout_lista_foto.invalidate();
        }

    }

    private void selectionLanguage() {
        switch (Memory.getLanguage(getApplicationContext())) {
            case Memory.ID_LANGUAGE_ITA:
                id_nome = R.string.nome_it;
                id_oggetto = R.string.oggetto_it;
                id_luogo = R.string.luogo_it;
                id_categoria = R.string.categoria_it;
                id_descrizione = R.string.descrizione_it;
                id_scatta_foto = R.drawable.segnalazioni_scattafoto_it;
                id_scegli_foto = R.drawable.segnalazioni_sceglifoto_it;
                id_riempi_campi = R.string.riempi_campi_it;
                break;
            case Memory.ID_LANGUAGE_EN:
                id_nome = R.string.nome_en;
                id_luogo = R.string.luogo_en;
                id_oggetto = R.string.oggetto_en;
                id_categoria = R.string.categoria_en;
                id_descrizione = R.string.descrizione_en;
                id_riempi_campi = R.string.riempi_campi_en;
                id_scatta_foto = R.drawable.segnalazioni_scattafoto_en;
                id_scegli_foto = R.drawable.segnalazioni_sceglifoto_en;
                break;
            case Memory.ID_LANGUAGE_DE:
                id_nome = R.string.nome_de;
                id_luogo = R.string.luogo_de;
                id_oggetto = R.string.oggetto_de;
                id_categoria = R.string.categoria_de;
                id_descrizione = R.string.descrizione_de;
                id_riempi_campi = R.string.riempi_campi_de;
                id_scatta_foto = R.drawable.segnalazioni_scattafoto_de;
                id_scegli_foto = R.drawable.segnalazioni_sceglifoto_de;
                break;
            case Memory.ID_LANGUAGE_FR:
                id_nome = R.string.nome_fr;
                id_luogo = R.string.luogo_fr;
                id_oggetto = R.string.oggetto_fr;
                id_categoria = R.string.categoria_fr;
                id_descrizione = R.string.descrizione_fr;
                id_riempi_campi = R.string.riempi_campi_fr;
                id_scatta_foto = R.drawable.segnalazioni_scattafoto_fr;
                id_scegli_foto = R.drawable.segnalazioni_sceglifoto_fr;
                break;
            case Memory.ID_LANGUAGE_ES:
                id_nome = R.string.nome_es;
                id_luogo = R.string.luogo_es;
                id_oggetto = R.string.oggetto_es;
                id_categoria = R.string.categoria_es;
                id_descrizione = R.string.descrizione_es;
                id_riempi_campi = R.string.riempi_campi_es;
                id_scatta_foto = R.drawable.segnalazioni_scattafoto_es;
                id_scegli_foto = R.drawable.segnalazioni_sceglifoto_es;
                break;
            case Memory.ID_LANGUAGE_DEFAULT:
                id_nome = R.string.nome_it;
                id_luogo = R.string.luogo_it;
                id_oggetto = R.string.oggetto_it;
                id_categoria = R.string.categoria_it;
                id_descrizione = R.string.descrizione_it;
                id_riempi_campi = R.string.riempi_campi_it;
                id_scatta_foto = R.drawable.segnalazioni_scattafoto_it;
                id_scegli_foto = R.drawable.segnalazioni_sceglifoto_it;
                break;
        }
        scegli_foto.setImageResource(id_scegli_foto);
        scatta_foto.setImageResource(id_scatta_foto);
        nomeLbl.setText(id_nome);
        nome.setHint(id_nome);
        oggettoLbl.setText(id_oggetto);
        oggetto.setHint(id_oggetto);
        luogoLbl.setText(id_luogo);
        luogo.setHint(id_luogo);
        categoriaLbl.setText(id_categoria);
        descrizione.setHint(id_descrizione);
    }

   
}
