package app.robo.fhv.roboapp.views.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.robo.fhv.roboapp.R;


public class WelcomeHowItsDone extends Fragment {

    private TextView description;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome_how_its_done, container, false);

        return v;
    }
}
