package app.robo.fhv.roboapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import app.robo.fhv.roboapp.communication.NetworkClient;

public class MainActivity extends Activity {

    private NetworkClient client;

    private TextView inputTextView;
    private TextView outputTextView;

    private EditText editText;
    private Button button;
    private SeekBar sbLeft;
    private SeekBar sbRight;
    private TextView tvLeft;
    private TextView tvRight;

    private int stepSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        inputTextView = (TextView) findViewById(R.id.inputTextView);
        outputTextView = (TextView) findViewById(R.id.outputTextView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        inputTextView.setText("text view");
        editText.setText("edit text");

        sbLeft = (SeekBar) findViewById(R.id.sbLeft);
        sbRight = (SeekBar) findViewById(R.id.sbRight);
        tvLeft = (TextView) findViewById(R.id.tvLeft);
        tvRight = (TextView) findViewById(R.id.tvRight);

        sbLeft.setProgress(100);
        sbRight.setProgress(100);

        try {
            client = new NetworkClient(inputTextView, outputTextView);
            new Thread(client).start();
        } catch (Exception e) {
            e.printStackTrace();

            inputTextView.setText("Error");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send(editText.getText().toString());
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
                tvLeft.setText(String.valueOf(progress));
                client.driveLeft(progress);

                lastProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int lastProgress = sbRight.getProgress();

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (progress/stepSize)*stepSize;
                if (progress == lastProgress) {return;}

                seekBar.setProgress(progress);
                tvRight.setText(String.valueOf(progress));
                client.driveRight(progress);

                lastProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


}
