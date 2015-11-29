package app.robo.fhv.roboapp.communication;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import communication.IConfiguration;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;

/**
 * Created by Kevin on 05.11.2015.
 */
public class NetworkClient implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler {

    private boolean isRunning;
    private boolean isConnectionOpened;

    private final DatagramSocket clientSocket;
    private TextView textView;

    private final ConfigurationManager configManager;
    private final CommunicationManager comManager;
    private final IConfiguration configuration;

    public NetworkClient(TextView textView) throws SocketException, UnknownHostException {
        int port = 997;
        //String address = "83.212.127.13";
        String address = "10.0.2.2";
        this.clientSocket = new DatagramSocket();
        this.textView = textView;

        this.configManager = new ConfigurationManager();
        this.comManager = new CommunicationManager(configManager);

        this.configuration = this.configManager.createConfiguration();

        // Setup configuration
        // TODO: Add server ip address
        //this.configuration.setIpAddress("127.0.0.1");
        this.configuration.setIpAddress(address);
        this.configuration.setPort(port);
    }

    public void send(String message) {
        SendTask task = new SendTask(clientSocket, comManager);
        task.execute(message);
    }

    @Override
    public void run() {

        isRunning = true;

        try {

            while (isRunning) {
                byte[] receiveData = new byte[1024];

                // Open connection
                if (!isConnectionOpened) {
                    DatagramPacket openPacket = comManager.createOpenConnectionDatagramPacket(configuration, "open");
                    clientSocket.send(openPacket);

                    DatagramPacket receiveSessionPacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receiveSessionPacket);

                    comManager.readDatagramPacket(receiveSessionPacket, this, this);

                    isConnectionOpened = true;
                } else {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket);

                    comManager.readDatagramPacket(receivePacket, this, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void answer(IConfiguration iConfiguration, byte[] bytes) {

    }

    @Override
    public void answer(IConfiguration iConfiguration, DatagramPacket datagramPacket) {

    }

    @Override
    public boolean handleDataReceived(DatagramPacket datagramPacket, ApplicationPDU applicationPDU, IAnswerHandler iAnswerHandler) {
        final String message = "payload[" + applicationPDU.getPayload() + "], command[" + applicationPDU.getCommand() + "]";
        Handler updateView = new Handler(Looper.getMainLooper());
        updateView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
            }
        });

        return true;
    }

    private class SendTask extends AsyncTask<String, Void, Void> {

        private final CommunicationManager comManager;
        private final DatagramSocket clientSocket;

        protected SendTask(DatagramSocket clientSocket, CommunicationManager communicationManager) {
            this.comManager = communicationManager;
            this.clientSocket = clientSocket;
        }

        @Override
        protected Void doInBackground(String... message) {
            // TODO: check message
            DatagramPacket sendPacket = comManager.createDatagramPacket(configuration, message[0]);
            try {
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
