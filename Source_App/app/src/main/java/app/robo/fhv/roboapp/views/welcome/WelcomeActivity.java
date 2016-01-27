package app.robo.fhv.roboapp.views.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import app.robo.fhv.roboapp.R;
import app.robo.fhv.roboapp.persistence.SharedPreferencesPersistence;
import app.robo.fhv.roboapp.views.MainActivity;

public class WelcomeActivity extends AppIntro2 implements Serializable {

    public static final String PLAYER_NAME_TAG = "PLAYER_NAME";
    private EditText name;
    private EditText nameField;
    private SharedPreferencesPersistence instance;
    private CountDownTimer countDownTimer;

    @Override
    public void init(Bundle savedInstanceState) {
        boolean firstStart = false;
        if (!SharedPreferencesPersistence.isInstanceCreated()) {
            SharedPreferencesPersistence.createInstance(this);
            firstStart = true;
        }
        instance = SharedPreferencesPersistence.getInstance();
        addSlide(new WelcomeSplashPage());
        addSlide(new WelcomeThatsMe());
        addSlide(new WelcomeHowItsDone());
        addSlide(new WelcomeLetsGo());
        setFadeAnimation();

        if(countDownTimer != null) {
            return;
        }
        final boolean fs = firstStart;
        countDownTimer = new CountDownTimer(2000, 2000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                long lastLogin = instance.getLastLoginTime();
                // 1000ms * 3600s * 24h * 3 = 3 days
                if(fs && lastLogin != 0 && (System.currentTimeMillis() - lastLogin < 1000*3600*24*3)) {
                    getPager().setCurrentItem(3, true);
                }
                instance.persistLastLoginTime(System.currentTimeMillis());
                countDownTimer.cancel();
            }
        };
        countDownTimer.start();
    }


    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        String n = nameField.getText().toString();
        if (n == null || n.trim().length() == 0) {
            // Show toast
            Toast.makeText(getApplicationContext(), "Sei nicht so schüchtern!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(PLAYER_NAME_TAG, n);
        instance.persistLoginName(n);
        startActivity(i);
    }

    @Override
    public void onSlideChanged() {
        // Always try to hide keyboard if slide
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = this.getCurrentFocus();
        if (v != null)
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void setNameField(EditText nameField) {
        this.nameField = nameField;
        nameField.setText(instance.getLoginName());
    }
}
