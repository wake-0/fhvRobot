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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    private View lytHighscore;
    private ListView listHighscore;

    private TextView statusText;
    private TextView spectatorText;

    private int stepSize = 10;
    private NetworkClient networkClient;
    private ValueAnimator leftSnapBackAnimator;
    private ValueAnimator rightSnapBackAnimator;

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
        statusText = (TextView) findViewById(R.id.lblStatusText);
        spectatorText = (TextView) findViewById(R.id.txtSpecatatorWelcome);
        setSpectatorText("Zuschauermodus");

        sbLeft = (SeekBar) findViewById(R.id.sbLeft);
        sbRight = (SeekBar) findViewById(R.id.sbRight);

        lytHighscore = findViewById(R.id.lytHighscoreLayout);

        sbLeft.setProgress(MOTOR_SEEK_BAR_ZERO_VALUE);
        sbRight.setProgress(MOTOR_SEEK_BAR_ZERO_VALUE);

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

        playerName = getIntent().getExtras().getString(WelcomeActivity.PLAYER_NAME_TAG);
        Log.d(LOG_TAG, "Using player name " + playerName);
    }

    private void setSpectatorText(String value) {
        spectatorText.setText(value);
    }

    @Override
    public void updateScores(String highScore) {
        Log.d(LOG_TAG, "Received highScore: " + highScore);

        Score[] scores = XmlHelper.parseHighScoreString(highScore);
        listHighscore = (ListView) lytHighscore.findViewById(R.id.listHighscore);
        listHighscore.setAdapter(new ScoreArrayAdapter(lytHighscore.getContext(), scores));
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
                Animation animFadeOut = AnimationUtils.loadAnimation(MainActivity.this.getApplicationContext(), R.anim.fade_out_animation);
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
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void signalStrengthChange(final SignalStrength newStrength) {
        if (newStrength == SignalStrength.DEAD_SIGNAL) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (!reconnectActivityStarted)
                        Toast.makeText(MainActivity.this, "Verbindung abgebrochen!", Toast.LENGTH_SHORT).show();
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!reconnectActivityStarted) {
                reconnectActivityStarted = true;
                networkClient.disconnect();
                Intent intent = new Intent(this, ReconnectActivity.class);
                startActivityForResult(intent, ReconnectActivity.REQUEST_CODE);
                return;
            }
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
                                if(!isOperator) {
                                    return;
                                }

                                if(millis > 5000) {
                                    setSpectatorText("Bereit machen!");
                                } else if(millis > 4000) {
                                    setSpectatorText("3");
                                } else if(millis > 3000) {
                                    setSpectatorText("2");
                                } else if(millis > 2000) {
                                    setSpectatorText("1");
                                } else if(millis > 1000){
                                    setSpectatorText("LOS!");
                                }
                            }

                            @Override
                            public void onFinish() {
                                if(isOperator) {
                                    setSpectatorText("");
                    sbLeft.setVisibility(View.VISIBLE);
                    sbRight.setVisibility(View.VISIBLE);
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
                        sbLeft.setVisibility(View.INVISIBLE);
                        sbRight.setVisibility(View.INVISIBLE);
                        setSpectatorText("Zuschauermodus");
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
}
