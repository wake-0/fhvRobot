package app.robo.fhv.roboapp;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import app.robo.fhv.roboapp.communication.CommunicationClient;
import app.robo.fhv.roboapp.communication.MediaStreaming;
import app.robo.fhv.roboapp.communication.NetworkClient;

public class MainActivity extends Activity implements CommunicationClient.ICommunicationCallback, MediaStreaming.IFrameReceived {

    private static final String LOG_TAG = "MainActivity";
    private static final long SNAP_BACK_TIME_MS = 300;
    private static final int MOTOR_SEEK_BAR_ZERO_VALUE = 100;

    private EditText editText;
    private ImageButton button;
    private SeekBar sbLeft;
    private SeekBar sbRight;

    private ImageView camCanvas;

    private int stepSize = 10;
    private NetworkClient networkClient;
    private ValueAnimator leftSnapBackAnimator;
    private ValueAnimator rightSnapBackAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        camCanvas = (ImageView) findViewById(R.id.imgCamCanvas);
        editText = (EditText) findViewById(R.id.editText);
        button = (ImageButton) findViewById(R.id.button);

        editText.setText("edit text");

        sbLeft = (SeekBar) findViewById(R.id.sbLeft);
        sbRight = (SeekBar) findViewById(R.id.sbRight);

        sbLeft.setProgress(MOTOR_SEEK_BAR_ZERO_VALUE);
        sbRight.setProgress(MOTOR_SEEK_BAR_ZERO_VALUE);

        try {
            networkClient = new NetworkClient(this, this);
            networkClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Could not instantiate NetworkClient");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkClient.getCommunicationClient().send(editText.getText().toString());
            }
        });


        sbLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int lastProgress = sbLeft.getProgress();

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (progress / stepSize) * stepSize;
                if (progress == lastProgress) {
                    return;
                }

                seekBar.setProgress(progress);
                Log.v(LOG_TAG, "Left: " + String.valueOf(progress));
                networkClient.getCommunicationClient().driveLeft(progress);

                lastProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (leftSnapBackAnimator != null && leftSnapBackAnimator.isRunning()) {
                    leftSnapBackAnimator.cancel();
                }
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int value = seekBar.getProgress();
                leftSnapBackAnimator = ValueAnimator.ofInt(value, MOTOR_SEEK_BAR_ZERO_VALUE);
                leftSnapBackAnimator.setDuration(SNAP_BACK_TIME_MS);
                leftSnapBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(
                            ValueAnimator animation) {
                        int value = (Integer) animation
                                .getAnimatedValue();
                        seekBar.setProgress(value);
                    }
                });
                leftSnapBackAnimator.start();
            }
        });
        sbRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int lastProgress = sbRight.getProgress();

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (progress / stepSize) * stepSize;
                if (progress == lastProgress) {
                    return;
                }

                seekBar.setProgress(progress);
                Log.v(LOG_TAG, "Right: " + String.valueOf(progress));
                networkClient.getCommunicationClient().driveRight(progress);

                lastProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (rightSnapBackAnimator != null && rightSnapBackAnimator.isRunning()) {
                    rightSnapBackAnimator.cancel();
                }
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int value = seekBar.getProgress();
                rightSnapBackAnimator = ValueAnimator.ofInt(value, MOTOR_SEEK_BAR_ZERO_VALUE);
                rightSnapBackAnimator.setDuration(SNAP_BACK_TIME_MS);
                rightSnapBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(
                            ValueAnimator animation) {
                        int value = (Integer) animation
                                .getAnimatedValue();
                        seekBar.setProgress(value);
                    }
                });
                rightSnapBackAnimator.start();
            }
        });
    }


    @Override
    public void frameReceived(Bitmap i) {
        final BitmapDrawable d = new BitmapDrawable(getResources(), i);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                camCanvas.setBackground(d);
            }
        });
    }

    @Override
    public void generalMessageReceived(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
