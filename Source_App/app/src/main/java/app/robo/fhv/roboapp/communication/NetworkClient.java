package app.robo.fhv.roboapp.communication;

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
        int port = 998;
        String address = "10.0.2.2";
        this.clientSocket = new DatagramSocket(port, InetAddress.getByName(address));
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
        DatagramPacket sendPacket = comManager.createDatagramPacket(configuration, message);
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        textView.setText("payload[" + applicationPDU.getPayload() + "], command[" + applicationPDU.getCommand() + "]");
        return true;
    }
}
