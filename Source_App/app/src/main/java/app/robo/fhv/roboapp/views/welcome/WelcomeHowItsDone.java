package app.robo.fhv.roboapp.views.welcome;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                final Drawable[] ds = new Drawable[] {
                        getResources().getDrawable(R.drawable.intro_0),
                        getResources().getDrawable(R.drawable.intro_1),
                        getResources().getDrawable(R.drawable.intro_2),
                        getResources().getDrawable(R.drawable.intro_3),
                        getResources().getDrawable(R.drawable.intro_4),
                        getResources().getDrawable(R.drawable.intro_5),
                        getResources().getDrawable(R.drawable.intro_6),
                        getResources().getDrawable(R.drawable.intro_7),
                        getResources().getDrawable(R.drawable.intro_8),
                        getResources().getDrawable(R.drawable.intro_9),
                        getResources().getDrawable(R.drawable.intro_10),
                        getResources().getDrawable(R.drawable.intro_11)
                };
                CyclicTransitionDrawable ctd = new CyclicTransitionDrawable(ds);
                imgTutorial.setImageDrawable(ctd);
                ctd.startTransition(500, 3000);
            }
        }.start();
        return v;
    }
}
