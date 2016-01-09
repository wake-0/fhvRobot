package app.robo.fhv.roboapp.views;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import app.robo.fhv.roboapp.R;

public class ReconnectActivity extends FragmentActivity {

    public static final int REQUEST_CODE = 1001;
    public static final int RESULT_CODE_CANCEL_CONNECTION = 1001 + 1;
    public static final int RESULT_CODE_RECONNECT = 1001 + 2;

    private TextView lblReconnectTimer;
    private CountDownTimer countDownTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconnect);

        lblReconnectTimer = (TextView)(findViewById(R.id.lblReconnectCountdown));

        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                lblReconnectTimer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                lblReconnectTimer.setText("" + 0);
                ReconnectActivity.this.setResult(RESULT_CODE_RECONNECT);
                ReconnectActivity.this.finish();
            }

        };
        countDownTimer.start();
    }

    public void reconnectNow(View v) {
        countDownTimer.cancel();
        setResult(RESULT_CODE_RECONNECT);
        finish();
    }

    public void cancelReconnect(View v) {
        setResult(RESULT_CODE_CANCEL_CONNECTION);
        finish();
    }
}
