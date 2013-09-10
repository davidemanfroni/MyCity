package com.EGaspari.Mobile.Objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.EGaspari.Mobile.Android.MyCity.R;
import com.polites.android.GestureImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Free for anyone to use, just say thanks and share :-)
 *
 * @author Blundell
 *
 */
public class LoaderGestureImageView extends LinearLayout {

    private static final int COMPLETE = 0;
    private static final int FAILED = 1;
    private Context mContext;
    private Drawable mDrawable;
    private ProgressBar mSpinner;
    private GestureImageView mImage;

    /**
     * This is used when creating the view in XML To have an image load in XML
     * use the tag
     * 'image="http://developer.android.com/images/dialog_buttons.png"'
     * Replacing the url with your desired image Once you have instantiated the
     * XML view you can call setImageDrawable(url) to change the image
     *
     * @param context
     * @param attrSet
     */
    public LoaderGestureImageView(final Context context, final AttributeSet attrSet) {
        super(context, attrSet);
        final String url = attrSet.getAttributeValue(null, "image");
        if (url != null) {
            instantiate(context, url);
        } else {
            instantiate(context, null);
        }
    }

    /**
     * This is used when creating the view programatically Once you have
     * instantiated the view you can call setImageDrawable(url) to change the
     * image
     *
     * @param context the Activity context
     * @param imageUrl the Image URL you wish to load
     */
    public LoaderGestureImageView(final Context context, final String imageUrl) {
        super(context);
        instantiate(context, imageUrl);
    }

    /**
     * First time loading of the LoaderGestureImageView Sets up the LayoutParams
     * of the view, you can change these to get the required effects you want
     */
    public void setImageLayoutParams(LinearLayout.LayoutParams params) {
        mImage.setLayoutParams(params);
    }

    private void instantiate(final Context context, final String imageUrl) {
        this.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        mContext = context;
        LinearLayout.LayoutParams paramsImage, paramsSpinner;

        paramsImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsImage.gravity = Gravity.CENTER;
        this.setGravity(Gravity.CENTER);
        mImage = new GestureImageView(mContext);
        //mImage.setLayoutParams(paramsImage);

        paramsSpinner = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsSpinner.gravity = Gravity.CENTER;
        this.setGravity(Gravity.CENTER);
        mSpinner = new ProgressBar(mContext);
        mSpinner.setLayoutParams(paramsSpinner);

        mSpinner.setIndeterminate(true);

        addView(mSpinner);
        addView(mImage);

        if (imageUrl != null) {
            setImageDrawable(imageUrl);
        }
    }

    /**
     * Set's the view's drawable, this uses the internet to retrieve the image
     * don't forget to add the correct permissions to your manifest
     *
     * @param imageUrl the url of the image you wish to load
     */
    public void setImageDrawable(final String imageUrl) {
        mDrawable = null;
        mSpinner.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                try {
                    mDrawable = getDrawableFromUrl(imageUrl);
                    imageLoadedHandler.sendEmptyMessage(COMPLETE);
                } catch (MalformedURLException e) {
                    imageLoadedHandler.sendEmptyMessage(FAILED);
                    Log.i("DEBUG", e.getMessage() + "\n" + e.getCause());
                } catch (IOException e) {
                    imageLoadedHandler.sendEmptyMessage(FAILED);
                    Log.i("DEBUG", e.getMessage() + "\n" + e.getCause());
                }
            }
        ;
    }

    .start();
	}
        
        public void setImageDrawableWithoutUrl(Context context, Drawable d) {
        mImage.setImageDrawable(d);
        mImage.setVisibility(View.VISIBLE);
        mSpinner.setVisibility(View.GONE);
        Log.i("DEBUG", "SETDRAWABLE");
    }

    public void setScaledType(ScaleType type) {
        mImage.setScaleType(type);

    }
    /**
     * Callback that is received once the image has been downloaded
     */
    private final Handler imageLoadedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE:
                    mImage.setImageDrawable(mDrawable);
                    mImage.setVisibility(View.VISIBLE);
                    mSpinner.setVisibility(View.GONE);
                    break;
                case FAILED:
                    mImage.setImageDrawable(null);
                    mImage.setVisibility(View.VISIBLE);
                    mSpinner.setVisibility(View.GONE);
                    break;
                default:
                    // Could change image here to a 'failed' image
                    // otherwise will just keep on spinning
                    break;
            }
            return true;
        }
    });

    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Pass in an image url to get a drawable object
     *
     * @return a drawable object
     * @throws IOException
     * @throws MalformedURLException
     */
    private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setUseCaches(true);
        conn.connect();
        InputStream is = conn.getInputStream();
        return Drawable.createFromStream(is, "name");
    }
}
