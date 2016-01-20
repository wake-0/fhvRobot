package app.robo.fhv.roboapp.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import app.robo.fhv.roboapp.utils.ProgressMapper;
import app.robo.fhv.roboapp.views.IHighScoreManager;
import communication.commands.Commands;
import communication.configurations.IConfiguration;
import communication.flags.Flags;
import communication.heartbeat.HeartbeatManager;
import communication.heartbeat.IHeartbeatHandler;
import communication.managers.CommunicationManager;
import communication.managers.DatagramFactory;
import communication.managers.IAnswerHandler;
import communication.managers.IDataReceivedHandler;
import communication.pdu.ApplicationPDU;
import communication.utils.NumberParser;

/**
 * Created by Kevin on 05.11.2015.
 */
public class CommunicationClient implements Runnable, IDataReceivedHandler<ApplicationPDU>, IAnswerHandler, IHeartbeatHandler<IConfiguration> {

    public interface ISendTaskFinished {
        void onFinish();
    }

    private final ICommunicationCallback callback;

    public interface ICommunicationCallback {
        void startConnectionEstablishment();
        void startSession();
        void sessionCreated();
        void registering();
        void generalMessageReceived(String message);
        void signalStrengthChange(SignalStrength newStrength);
        void registered();

        void startSteering();
        void stopSteering();
    }

    private static final String LOG_TAG = "CommunicationClient";
    // Fields
    private boolean isRunning;
    private boolean isConnectionOpened;

    private long lastMessageReceiveTime;
    private TimerTask signalStrengthTimerTask;
    private Timer signalStrengthTimer;
    private final DatagramSocket clientSocket;

    private final ConfigurationManager configManager;
    private final CommunicationManager comManager;
    private final IConfiguration configuration;

    private IHighScoreManager highScoreManager;
    private final HeartbeatManager<IConfiguration> heartbeatManager;
    private static final int HEARTBEAT_TIME = 1 * 1000;

    // Constructor
    public CommunicationClient(ICommunicationCallback callback, IHighScoreManager highScoreManager) throws SocketException, UnknownHostException {
        this.highScoreManager = highScoreManager;
        this.clientSocket = new DatagramSocket();
        this.callback = callback;
        this.configManager = new ConfigurationManager();
        this.comManager = new CommunicationManager(configManager);

        // Configuration is setup by default
        this.configuration = this.configManager.createConfiguration();

        // Add a manager which is responsible for the heartbeats
        this.heartbeatManager = new HeartbeatManager<>(configuration, this, HEARTBEAT_TIME, HEARTBEAT_TIME);
        this.heartbeatManager.run();
    }

    // Methods
    public void sendChangeName(String message) {
        callback.registering();
        new SendTask(clientSocket, comManager, configuration, Flags.REQUEST_FLAG, Commands.CHANGE_NAME, null).execute(message.getBytes());
    }

    public void sendHighScoreRequest() {
        new SendTask(clientSocket, comManager, configuration, Flags.REQUEST_FLAG, Commands.REQUEST_PERSISTED_DATA, null).execute("0".getBytes());
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
        new SendTask(clientSocket, comManager, configuration, flags, command, null).execute(new byte[]{byteValue});
    }

    @Override
    public void run() {

        isRunning = true;
        initSignalStrengthTask();
        callback.startConnectionEstablishment();

        try {

            while (isRunning) {
                byte[] receiveData = new byte[GlobalSettings.RECEIVE_PACKET_SIZE];

                // Open connection
                if (!isConnectionOpened) {
                    callback.startSession();
                    DatagramPacket openPacket = DatagramFactory.createOpenConnectionDatagramPacket(configuration);
                    clientSocket.send(openPacket);
                    DatagramPacket receiveSessionPacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receiveSessionPacket);

                    comManager.readDatagramPacket(receiveSessionPacket, this, this);

                    isConnectionOpened = true;
                    callback.sessionCreated();
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

    private void initSignalStrengthTask() {
        if (signalStrengthTimer != null) {
            signalStrengthTimer.cancel();
            signalStrengthTimer.purge();
        }
        callback.signalStrengthChange(SignalStrength.FULL_SIGNAL);
        lastMessageReceiveTime = System.currentTimeMillis();
        // Start timer task to check signal strength (heartbeat)
        signalStrengthTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isRunning == false) return;
                long currentTime = System.currentTimeMillis();
                long timeDiff = currentTime - lastMessageReceiveTime;
                if (timeDiff > 4500) {
                    callback.signalStrengthChange(SignalStrength.DEAD_SIGNAL);
                } else if (timeDiff > 3500) {
                    callback.signalStrengthChange(SignalStrength.NO_SIGNAL);
                } else if (timeDiff > 2000) {
                    callback.signalStrengthChange(SignalStrength.NEARLY_LOW_SIGNAL);
                } else if (timeDiff > 1500) {
                    callback.signalStrengthChange(SignalStrength.HALF_FULL_SIGNAL);
                } else if (timeDiff > 1100) {
                    callback.signalStrengthChange(SignalStrength.NEARLY_FULL_SIGNAL);
                } else {
                    callback.signalStrengthChange(SignalStrength.FULL_SIGNAL);
                }
            }
        };
        signalStrengthTimer = new Timer();
        signalStrengthTimer.schedule(signalStrengthTimerTask, 300, 300);
    }

    @Override
    public void answer(DatagramPacket packet) {

    }

    @Override
    public boolean handleDataReceived(DatagramPacket datagramPacket, ApplicationPDU applicationPDU, IAnswerHandler iAnswerHandler) {
        final String message = " received: payload[" + applicationPDU.getPayload() + "], command[" + applicationPDU.getCommand() + "]";
        Log.d(LOG_TAG, message);
        lastMessageReceiveTime = System.currentTimeMillis();

        int command = applicationPDU.getCommand();
        switch (command) {
            case Commands.GENERAL_MESSAGE:
                String serverMessage = new String(applicationPDU.getPayload());
                callback.generalMessageReceived(serverMessage);
                break;
            case Commands.CHANGE_NAME:
                callback.registered();
                break;
            case Commands.ROBO_STEARING:
                callback.startSteering();
                break;
            case Commands.ROBO_NOT_STEARING:
                callback.stopSteering();
                break;
            case Commands.REQUEST_PERSISTED_DATA:
                if (applicationPDU.isAnswer()) {
                    highScoreManager.updateScores(new String(applicationPDU.getPayload()));
                }
            default:
                break;
        }
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
        new SendTask(clientSocket, comManager, configuration, Flags.REQUEST_FLAG, Commands.DEFAULT, null).execute(message.getBytes());
    }

    public void stop() {
        isRunning = false;
        if (signalStrengthTimer != null) {
            signalStrengthTimer.cancel();
            signalStrengthTimer.purge();
        }
        heartbeatManager.stopHeartbeat();
        clientSocket.close();
    }

    public void sendDisconnect(ISendTaskFinished callback) {
        new SendTask(clientSocket, comManager, configuration, Flags.REQUEST_FLAG, Commands.REQUEST_DISCONNECT, callback).execute(new byte[0]);
    }

    private class SendTask extends AsyncTask<byte[], Void, Void> {

        private final ISendTaskFinished callback;

        private final DatagramSocket clientSocket;
        private final CommunicationManager communicationManager;
        private final IConfiguration configuration;
        private final int command;
        private final int flags;

        protected SendTask(DatagramSocket clientSocket, CommunicationManager communicationManager, IConfiguration configuration, int flags, int command, ISendTaskFinished callback) {
            this.clientSocket = clientSocket;
            this.communicationManager = communicationManager;
            this.configuration = configuration;
            this.callback = callback;
            this.command = command;
            this.flags = flags;
        }

        @Override
        protected Void doInBackground(final byte[] ... message) {
            final DatagramPacket sendPacket = DatagramFactory.createDatagramPacket(configuration, flags, command, message[0]);

            Log.d(LOG_TAG, "output: payload[" + message[0] + "], command[" + command + "]");

            try {
                clientSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null)
                callback.onFinish();
        }
    }
}
