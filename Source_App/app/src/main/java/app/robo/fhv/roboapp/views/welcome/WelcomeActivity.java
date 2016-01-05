package app.robo.fhv.roboapp.views.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;

import java.io.Serializable;

import app.robo.fhv.roboapp.R;
import app.robo.fhv.roboapp.views.MainActivity;

public class WelcomeActivity extends AppIntro2 implements Serializable {

    public static final String PLAYER_NAME_TAG = "PLAYER_NAME";
    private EditText name;
    private EditText nameField;

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new WelcomeSplashPage());
        addSlide(new WelcomeThatsMe());
        addSlide(new WelcomeHowItsDone());
        addSlide(new WelcomeLetsGo());

        setFadeAnimation();
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
    }
}
