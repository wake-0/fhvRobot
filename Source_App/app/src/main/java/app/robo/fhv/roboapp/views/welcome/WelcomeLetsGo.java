package app.robo.fhv.roboapp.views.welcome;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import app.robo.fhv.roboapp.R;


public class WelcomeLetsGo extends Fragment {

    private EditText name;
    private WelcomeActivity parent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome_lets_go, container, false);
        init(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        init(getView());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            parent = (WelcomeActivity) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException("Can only be attached to WelcomeActivity");
        }
    }

    private void init(View v) {
        ImageView ad = (ImageView) v.findViewById(R.id.imgArrowDown);
        name = (EditText) v.findViewById(R.id.name);
        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(name.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        });
        parent.setNameField(name);
    }
}
