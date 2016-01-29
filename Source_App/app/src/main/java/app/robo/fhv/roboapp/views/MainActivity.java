package app.robo.fhv.roboapp.views;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.robo.fhv.roboapp.R;
import app.robo.fhv.roboapp.communication.CommunicationClient;
import app.robo.fhv.roboapp.communication.MediaStreaming;
import app.robo.fhv.roboapp.communication.NetworkClient;
import app.robo.fhv.roboapp.communication.SignalStrength;
import app.robo.fhv.roboapp.domain.Score;
import app.robo.fhv.roboapp.utils.ScoreArrayAdapter;
import app.robo.fhv.roboapp.utils.XmlHelper;
import app.robo.fhv.roboapp.views.custom.CompassView;
import app.robo.fhv.roboapp.views.welcome.WelcomeActivity;

public class MainActivity extends FragmentActivity implements CommunicationClient.ICommunicationCallback, MediaStreaming.IFrameReceived, IHighScoreManager {

    private static final String LOG_TAG = "MainActivity";
    private static final long SNAP_BACK_TIME_MS = 300;
    private static final int MOTOR_SEEK_BAR_ZERO_VALUE = 100;

    private Map<SignalStrength, Drawable> signalStrengthMap;

    private boolean isOperator = false;

    private String playerName;
    private SeekBar sbLeft;
    private SeekBar sbRight;

    private ImageView camCanvas;
    private ImageView signalStrength;
    private ImageView highScores;
    private ImageView lamp;
    private ImageView messages;

    private View lytHighscore;
    private ListView listHighscore;

    private TextView statusText;
    private TextView spectatorText;
    private TextView timeText;

    private int stepSize = 10;
    private NetworkClient networkClient;
    private ValueAnimator leftSnapBackAnimator;
    private ValueAnimator rightSnapBackAnimator;

    private View lytMessages;
    private CompassView compass;

    private boolean reconnectActivityStarted;

    @Override
    protected void onPause() {
        super.onPause();
        if (networkClient != null) {
            networkClient.disconnect();
        }
        if (!reconnectActivityStarted) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        initSignalStrengthHashMap();

        camCanvas = (ImageView) findViewById(R.id.imgCamCanvas);
        signalStrength = (ImageView) findViewById(R.id.imgSignalStrength);
        highScores = (ImageView) findViewById(R.id.imgHighScores);
        lamp = (ImageView) findViewById(R.id.imgLamp);
        statusText = (TextView) findViewById(R.id.lblStatusText);
        spectatorText = (TextView) findViewById(R.id.txtSpecatatorWelcome);
        setSpectatorText("Zuschauermodus");

        sbLeft = (SeekBar) findViewById(R.id.sbLeft);
        sbRight = (SeekBar) findViewById(R.id.sbRight);

        compass = (CompassView) findViewById(R.id.cmpRobotCompass);

        lytHighscore = findViewById(R.id.lytHighscoreLayout);
        timeText = (TextView) findViewById(R.id.txtTimeMeasurement);

        sbLeft.setProgress(MOTOR_SEEK_BAR_ZERO_VALUE);
        sbRight.setProgress(MOTOR_SEEK_BAR_ZERO_VALUE);

        lytMessages = (View) findViewById(R.id.lytMessages);
        messages = (ImageView) findViewById(R.id.imgMessage);

        messages.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (lytMessages.getVisibility() == View.VISIBLE) {
                    Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_animation);
                    lytMessages.startAnimation(a);
                    lytMessages.setVisibility(View.INVISIBLE);
                } else {
                    Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_animation);
                    lytMessages.startAnimation(a);
                    lytMessages.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        try {
            networkClient = new NetworkClient(this, this, this);
            networkClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Could not instantiate NetworkClient");
        }

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

        highScores.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (lytHighscore.getVisibility() == View.VISIBLE) {
                    lytHighscore.setVisibility(View.GONE);
                } else {
                    lytHighscore.setVisibility(View.VISIBLE);
                    networkClient.getCommunicationClient().sendHighScoreRequest();
                }
                return false;
            }
        });

        lamp.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (isOperator) {
                    networkClient.getCommunicationClient().triggerLed();
                }
                return false;
            }
        });
        switchToSpectatorMode();
        playerName = getIntent().getExtras().getString(WelcomeActivity.PLAYER_NAME_TAG);
        Log.d(LOG_TAG, "Using player name " + playerName);
    }

    public void sendMessage(View v) {
        if (v instanceof Button == true) {
            Button b = (Button)(v);
            String message = b.getText().toString();
            this.networkClient.getCommunicationClient().sendMessage(message);
            Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_animation);
            lytMessages.startAnimation(a);
            lytMessages.setVisibility(View.INVISIBLE);
        }
    }

    private void setSpectatorText(String value) {
        spectatorText.setVisibility(View.VISIBLE);
        spectatorText.setText(value);
    }

    @Override
    public void updateScores(String highScore) {
        Log.d(LOG_TAG, "Received highScore: " + highScore);

        final Score[] scores = XmlHelper.parseHighScoreString(highScore);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listHighscore = (ListView) lytHighscore.findViewById(R.id.listHighscore);
                listHighscore.setEmptyView(lytHighscore.findViewById(R.id.empty));
                listHighscore.setAdapter(new ScoreArrayAdapter(lytHighscore.getContext(), scores));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (lytHighscore.getVisibility() == View.VISIBLE) {
            lytHighscore.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void initSignalStrengthHashMap() {
        signalStrengthMap = new HashMap<>();
        signalStrengthMap.put(SignalStrength.FULL_SIGNAL, getResources().getDrawable(R.drawable.ss_full));
        signalStrengthMap.put(SignalStrength.NEARLY_FULL_SIGNAL, getResources().getDrawable(R.drawable.ss_good));
        signalStrengthMap.put(SignalStrength.HALF_FULL_SIGNAL, getResources().getDrawable(R.drawable.ss_normal));
        signalStrengthMap.put(SignalStrength.NEARLY_LOW_SIGNAL, getResources().getDrawable(R.drawable.ss_low));
        signalStrengthMap.put(SignalStrength.LOW_SIGNAL, getResources().getDrawable(R.drawable.ss_low));
        signalStrengthMap.put(SignalStrength.NO_SIGNAL, getResources().getDrawable(R.drawable.ss_dead));
        signalStrengthMap.put(SignalStrength.DEAD_SIGNAL, getResources().getDrawable(R.drawable.ss_dead));
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

    private void setStatusText(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Animation animFadeOut = AnimationUtils.loadAnimation(MainActivity.this.getApplicationContext(), R.anim.fade_out_animation_delay);
                statusText.setAlpha(1.0f);
                statusText.setText(text);
                statusText.startAnimation(animFadeOut);
            }
        });
    }

    @Override
    public void startConnectionEstablishment() {
        setStatusText("Starte Verbindung...");
    }

    @Override
    public void startSession() {
        setStatusText("Starte Session...");
    }

    @Override
    public void sessionCreated() {
        setStatusText("Session erstellt!");
        networkClient.getCommunicationClient().sendChangeName(playerName);
    }

    @Override
    public void registering() {
        setStatusText("Registriere Name...");
    }

    @Override
    public void generalMessageReceived(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showToast(message);
            }
        });
    }

    private void showToast(String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    private void showToast(String message, int len) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.setDuration(len);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void signalStrengthChange(final SignalStrength newStrength) {
        if (newStrength == SignalStrength.DEAD_SIGNAL) {
            if (reconnectActivityStarted) return;

            reconnectActivityStarted = true;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    switchToSpectatorMode();
                    showToast("Verbindung abgebrochen!", Toast.LENGTH_SHORT);
                }
            });
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            networkClient.disconnect();
            Intent intent = new Intent(this, ReconnectActivity.class);
            startActivityForResult(intent, ReconnectActivity.REQUEST_CODE);
                            return;
                        }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // TODO: It would be nice to alpha blend the new image
                    signalStrength.setImageDrawable(signalStrengthMap.get(newStrength));
                }
            });
        }

    @Override
    public void registered() {
        setStatusText("Name registriert!");
    }

    @Override
    public void startSteering() {

        if(isOperator) {
            return;
        }

            isOperator = true;

            new Handler(Looper.getMainLooper()).post(
                    new Runnable() {
                        @Override
                        public void run() {
                            CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millis) {
                                    if (!isOperator) {
                                        return;
                                    }

                                    if (millis > 5000) {
                                        setSpectatorText("Bereit machen!");
                                    } else if (millis > 4000) {
                                        setSpectatorText("3");
                                    } else if (millis > 3000) {
                                        setSpectatorText("2");
                                    } else if (millis > 2000) {
                                        setSpectatorText("1");
                                    } else if (millis > 1000) {
                                        setSpectatorText("LOS!");
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    if (isOperator) {
                                        setSpectatorText("");
                                        spectatorText.setVisibility(View.INVISIBLE);
                                        sbLeft.setVisibility(View.VISIBLE);
                                        sbRight.setVisibility(View.VISIBLE);
                                        lamp.setVisibility(View.VISIBLE);
                                        compass.setVisibility(View.VISIBLE);
                                    }
                                    this.cancel();
                                }
                            };
                            countDownTimer.start();
                        }
                    }
            );
    }

    @Override
    public void stopSteering() {
        isOperator = false;

        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        switchToSpectatorMode();
                    }
                }
        );
    }

    private void switchToSpectatorMode() {
        sbLeft.setVisibility(View.INVISIBLE);
        sbRight.setVisibility(View.INVISIBLE);
        compass.setVisibility(View.INVISIBLE);
        lamp.setVisibility(View.INVISIBLE);
        setSpectatorText("Zuschauermodus");
    }

    @Override
    public void orientationChange(float roll, float pitch, final float yaw) {
        new Handler(Looper.getMainLooper()).post(
            new Runnable() {
                @Override
                public void run() {
                    if(isOperator) {
                        compass.setAngle(yaw);
                    }
                }
            }
        );
    }

    @Override
    public void startTimer() {
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        timeText.setText("00:00.00");
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_animation);
                        timeText.startAnimation(animation);
                        timeText.setVisibility(View.VISIBLE);
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                    }
                }
        );
    }

    @Override
    public void stopTimer(final String timeMessage) {
        if (timeText.getVisibility() != View.VISIBLE) { return; }
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        customHandler.removeCallbacks(updateTimerThread);
                        timeText.setText(timeMessage);
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_animation_delayed);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                timeText.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        timeText.startAnimation(animation);
                    }
                }
        );
    }

    @Override
    public void dismissTimer() {
        if (timeText.getVisibility() != View.VISIBLE) { return; }
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_animation);
                        timeText.startAnimation(animation);
                        timeText.setText("00:00.00");
                        timeText.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ReconnectActivity.REQUEST_CODE:
                reconnectActivityStarted = false;
                switch (resultCode) {
                    case 0:
                    case ReconnectActivity.RESULT_CODE_CANCEL_CONNECTION:
                        finish();
                        break;
                    case ReconnectActivity.RESULT_CODE_RECONNECT:
                        try {
                            networkClient = new NetworkClient(this, this, this);
                            networkClient.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG, "Could not instantiate NetworkClient");
                        }
                        break;
                }
        }
    }

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 100);
            timeText.setText("" + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs) + "."
                    + String.format("%02d", milliseconds));
            customHandler.postDelayed(this, 55);
        }

    };
}
