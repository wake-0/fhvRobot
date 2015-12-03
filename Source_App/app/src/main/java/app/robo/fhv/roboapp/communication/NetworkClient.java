package app.robo.fhv.roboapp.communication;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import app.robo.fhv.roboapp.utils.ProgressMapper;
import communication.IConfiguration;
import communication.commands.Commands;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;
import communication.utils.NumberParser;

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
        String address = "83.212.127.13";
        //String address = "10.0.2.2";
        this.clientSocket = new DatagramSocket();
        this.textView = textView;

        this.configManager = new ConfigurationManager();
        this.comManager = new CommunicationManager(configManager);

        this.configuration = this.configManager.createConfiguration();

        // Setup configuration
        this.configuration.setIpAddress(address);
        this.configuration.setPort(port);
    }

    public void send(String message) {
        new SendTask(clientSocket, comManager, configuration, Commands.CHANGE_NAME).execute(message.getBytes());
    }

    public void driveLeft(int leftValue) {
        sendCommand(Commands.DRIVE_LEFT, leftValue);
    }

    public void driveRight(int rightValue) {
        sendCommand(Commands.DRIVE_RIGHT, rightValue);
    }

    private void sendCommand(int command, int value) {
        int mappedValue = ProgressMapper.progressToDriveValue(value);
        byte byteValue = NumberParser.intToByte(mappedValue);
        new SendTask(clientSocket, comManager, configuration, command).execute(new byte[]{byteValue});
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

    private class SendTask extends AsyncTask<byte[], Void, Void> {

        private final DatagramSocket clientSocket;
        private final CommunicationManager communicationManager;
        private final IConfiguration configuration;
        private final int command;

        protected SendTask(DatagramSocket clientSocket, CommunicationManager communicationManager, IConfiguration configuration, int command) {
            this.clientSocket = clientSocket;
            this.communicationManager = communicationManager;
            this.configuration = configuration;
            this.command = command;
        }

        @Override
        protected Void doInBackground(byte[] ... message) {
            DatagramPacket sendPacket = communicationManager.createDatagramPacket(configuration, command, message[0]);

            try {
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
