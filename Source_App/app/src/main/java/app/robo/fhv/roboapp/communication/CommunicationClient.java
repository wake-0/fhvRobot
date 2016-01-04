package app.robo.fhv.roboapp.communication;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import app.robo.fhv.roboapp.utils.ProgressMapper;
import communication.commands.Commands;
import communication.configurations.Configuration;
import communication.configurations.IConfiguration;
import communication.flags.Flags;
import communication.heartbeat.HeartbeatManager;
import communication.heartbeat.IHeartbeatHandler;
import communication.managers.CommunicationManager;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;
import communication.utils.NumberParser;

/**
 * Created by Kevin on 05.11.2015.
 */
public class CommunicationClient implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler, IHeartbeatHandler<IConfiguration> {

    private static final String LOG_TAG = "CommunicationClient";
    // Fields
    private boolean isRunning;
    private boolean isConnectionOpened;

    private final DatagramSocket clientSocket;

    private final ConfigurationManager configManager;
    private final CommunicationManager comManager;
    private final IConfiguration configuration;

    private final HeartbeatManager<IConfiguration> heartbeatManager;
    private static final int HEARTBEAT_TIME = 1 * 1000;

    // Constructor
    public CommunicationClient() throws SocketException, UnknownHostException {
        this.clientSocket = new DatagramSocket();

        this.configManager = new ConfigurationManager();
        this.comManager = new CommunicationManager(configManager);

        // Configuration is setup by default
        this.configuration = this.configManager.createConfiguration();

        // Add a manager which is responsible for the heartbeats
        this.heartbeatManager = new HeartbeatManager<>(configuration, this, HEARTBEAT_TIME, HEARTBEAT_TIME);
        this.heartbeatManager.run();
    }

    // Methods
    public void send(String message) {
        new SendTask(clientSocket, comManager, configuration, Flags.REQUEST_FLAG, Commands.CHANGE_NAME).execute(message.getBytes());
    }

    public void driveLeft(int leftValue) {
        sendCommand(Flags.REQUEST_FLAG, Commands.DRIVE_LEFT, leftValue);
    }

    public void driveRight(int rightValue) {
        sendCommand(Flags.REQUEST_FLAG, Commands.DRIVE_RIGHT, rightValue);
    }

    private void sendCommand(int flags, int command, int value) {
        int mappedValue = ProgressMapper.progressToDriveValue(value);
        byte byteValue = NumberParser.intToByte(mappedValue);
        new SendTask(clientSocket, comManager, configuration, flags, command).execute(new byte[]{byteValue});
    }

    @Override
    public void run() {

        isRunning = true;

        try {

            while (isRunning) {
                byte[] receiveData = new byte[GlobalSettings.RECEIVE_PACKET_SIZE];

                // Open connection
                if (!isConnectionOpened) {
                    DatagramPacket openPacket = comManager.createOpenConnectionDatagramPacket(configuration);
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
    public void answer(DatagramPacket packet) {

    }

    @Override
    public boolean handleDataReceived(DatagramPacket datagramPacket, ApplicationPDU applicationPDU, IAnswerHandler iAnswerHandler) {
        final String message = " received: payload[" + applicationPDU.getPayload() + "], command[" + applicationPDU.getCommand() + "]";
        Log.d(LOG_TAG, message);
        return true;
    }

    @Override
    public void handleNoHeartbeat(IConfiguration configuration) {
        createHeartBeat(configuration);
    }

    @Override
    public void handleHeartbeat(IConfiguration configuration) {
        createHeartBeat(configuration);
    }

    private void createHeartBeat(IConfiguration configuration) {
        String message = "0";
        new SendTask(clientSocket, comManager, configuration, Flags.REQUEST_FLAG, Commands.DEFAULT).execute(message.getBytes());
    }

    public void stop() {
        isRunning = false;
        clientSocket.close();
    }

    private class SendTask extends AsyncTask<byte[], Void, Void> {

        private final DatagramSocket clientSocket;
        private final CommunicationManager communicationManager;
        private final IConfiguration configuration;
        private final int command;
        private final int flags;

        protected SendTask(DatagramSocket clientSocket, CommunicationManager communicationManager, IConfiguration configuration, int flags, int command) {
            this.clientSocket = clientSocket;
            this.communicationManager = communicationManager;
            this.configuration = configuration;
            this.command = command;
            this.flags = flags;
        }

        @Override
        protected Void doInBackground(final byte[] ... message) {
            final DatagramPacket sendPacket = communicationManager.createDatagramPacket(configuration, flags, command, message[0]);

            Log.d(LOG_TAG, "output: payload[" + message[0] + "], command[" + command + "]");

            try {
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
