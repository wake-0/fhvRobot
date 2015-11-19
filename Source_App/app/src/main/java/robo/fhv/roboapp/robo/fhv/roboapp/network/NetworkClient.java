package robo.fhv.roboapp.robo.fhv.roboapp.network;

import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Kevin on 05.11.2015.
 */
public class NetworkClient implements Runnable {

    private boolean isRunning = true;
    private final int port;
    private final DatagramSocket clientSocket;
    private TextView textView;

    public NetworkClient(TextView textView) throws SocketException {
        this(997, textView);
    }

    public NetworkClient(int port, TextView textView) throws SocketException {
        this.port = port;
        this.clientSocket = new DatagramSocket(port);
        this.textView = textView;
    }

    public void send(String message) {

    }

    @Override
    public void run() {

        try {
            byte[] receiveData = new byte[1024];

            while (isRunning) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                textView.setText(receivePacket.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
