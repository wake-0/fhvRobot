package app.robo.fhv.roboapp.views.welcome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.robo.fhv.roboapp.R;
import app.robo.fhv.roboapp.persistence.SharedPreferencesPersistence;


public class WelcomeLetsGo extends Fragment {

    private EditText name;
    private WelcomeActivity parent;

    private long lastTouch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome_lets_go, container, false);
        init(v);
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (lastTouch != 0 && (lastTouch + 1000) > System.currentTimeMillis()) {
                    // Show dialog
                    showTextDialog();
                }
                lastTouch = System.currentTimeMillis();
                return false;
            }
        });
        return v;
    }

    private void showTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Change Server's IP address");
        LayoutInflater lf = LayoutInflater.from(getContext());
/*
        final EditText input = new EditText(this.getContext());

        input.setEnabled(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHeight(100);
        input.setWidth(340);
        input.setGravity(Gravity.LEFT);

        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        builder.setView(input);
*/
        View v = lf.inflate(R.layout.dialog_change_server_address, null);
        final EditText input = (EditText) v.findViewById(R.id.txtServerAddress);
        input.setText(SharedPreferencesPersistence.getInstance().getServerAddress());
        builder.setView(v);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newAddress = input.getText().toString();
                SharedPreferencesPersistence.getInstance().persistServerAddress(newAddress);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
