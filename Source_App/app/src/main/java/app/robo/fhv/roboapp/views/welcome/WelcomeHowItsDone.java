package app.robo.fhv.roboapp.views.welcome;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.robo.fhv.roboapp.R;
import app.robo.fhv.roboapp.views.custom.CyclicTransitionDrawable;


public class WelcomeHowItsDone extends Fragment {
    private ImageView imgTutorial;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome_how_its_done, container, false);

        imgTutorial = (ImageView) v.findViewById(R.id.imgTutorial);

        final CountDownTimer ct = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (isAdded() == false) {
                    return;
                }

                new AsyncTask<Void, Void, Drawable[]>() {

                    @Override
                    protected Drawable[] doInBackground(Void... params) {
                        Drawable[] dss = new Drawable[12];
                        for (int i = 0; i < dss.length; i++) {
                            dss[i] = getAssetImage(WelcomeHowItsDone.this.getContext(), "intro_" + i);
                        }
                        return dss;
                    }

                    @Override
                    protected void onPostExecute(Drawable[] dss) {
                        for (int i = 0; i < dss.length; i++) {
                            if (dss[i] == null) {
                                Log.e("WELCOME", "Cannot load tutorial");
                                return;
                            }
                        }
                        CyclicTransitionDrawable ctd = new CyclicTransitionDrawable(dss);
                        imgTutorial.setImageDrawable(ctd);
                        ctd.startTransition(500, 3000);
                    }
                }.execute();
                this.cancel();
            }
        };
        ct.start();
        return v;
    }

    public static Drawable getAssetImage(Context context, String filename) {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = null;
        try {
            buffer = new BufferedInputStream((assets.open(("drawable/" + filename + ".png"))));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeStream(buffer, null, options);
            return new BitmapDrawable(context.getResources(), bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
