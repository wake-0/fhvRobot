package app.robo.fhv.roboapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.SocketException;

import app.robo.fhv.roboapp.communication.NetworkClient;

public class MainActivity extends AppCompatActivity {

    private NetworkClient client;

    private TextView textView;
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);

        textView.setText("text view");
        editText.setText("edit text");

        try {
            client = new NetworkClient(textView);
            new Thread(client).start();
        } catch (Exception e) {
            e.printStackTrace();

            textView.setText("Error");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send(editText.getText().toString());
            }
        });
    }
}
