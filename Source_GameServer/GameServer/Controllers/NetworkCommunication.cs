using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using GameServer.Utils;

namespace GameServer.Controllers
{
    public class NetworkCommunication
    {
        #region Fields
        private readonly IPEndPoint ipEndPoint;
        private readonly Socket serverSocket;

        private byte sessionId;
        private bool running = true;

        private static readonly int BUFFER_SIZE = 4096;
        #endregion

        #region Events
        public event EventHandler<byte[]> MessageReceived;
        #endregion

        #region ctor
        public NetworkCommunication()
        {
            ipEndPoint = new IPEndPoint(NetworkSettings.SERVER_ADDRESS, NetworkSettings.SERVER_PORT);
            serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp)
            {
                ReceiveTimeout = -1
            };
        }
        #endregion

        #region Methods
        public void Run()
        {
            if (!ConnectToServer())
            {
                // Connection not established than return
                return;
            }

            while (running)
            {
                var buffer = new byte[BUFFER_SIZE];
                var value = serverSocket.Receive(buffer);

                // The received message is a heartbeat or another not allowed message
                if (buffer.Length <= 5 || buffer[4] == 0) continue;

                OnMessageReceived(buffer);

               

                // Print received message
                Console.WriteLine(@"return [" + value + @"]");
                Console.WriteLine(BitConverter.ToString(buffer));
            }
        }

        private void OnMessageReceived(byte[] message)
        {
            if (MessageReceived != null)
            {
                MessageReceived.Invoke(this, message);
            }
        }

        public void SendCommand(int command, string message)
        {
            var payload = Encoding.ASCII.GetBytes(message);
            var isExtendedPayloadSize = payload.Length > 255;

            // Size of send data: flag, session id, flag, flag, command id, length, payload
            var headerSize = isExtendedPayloadSize ? 7 : 6;
            var sizeOfSendData = headerSize + payload.Length;
            var sendData = new byte[sizeOfSendData];

            // Set headers from the below layers
            sendData[0] = 0;                        // Flags [Network]
            sendData[1] = sessionId;                // Session id [Session]
            sendData[2] = 0;                        // Flags [Presentation]
            sendData[3] = isExtendedPayloadSize     // Flags with extended payload [Application]
                ? (byte)2
                : (byte)0;
            sendData[4] = (byte)command;            // General message command [Application]

            // Set payload length
            var lengthAsByteArr = LengthConverter.ConvertLength(payload.Length);
            const int lengthHeaderStart = 5;
            for (var i = lengthHeaderStart; i < headerSize; i++)
            {
                sendData[i] = lengthAsByteArr[i - lengthHeaderStart];
            }

            // Set payload
            for (var i = headerSize; i < sizeOfSendData; i++)
            {
                sendData[i] = payload[i - headerSize];
            }

            serverSocket.SendTo(sendData, sizeOfSendData, SocketFlags.None, ipEndPoint);
        }

        public void SendMessage(string message)
        {
            SendCommand(Commands.GENERAL_MESSAGE, message);
        }

        public void Stop()
        {
            running = false;
            serverSocket.Close();
        }

        private bool ConnectToServer()
        {
            // First time send open message
            var data = new byte[]{ 1, 0 };
            serverSocket.SendTo(data, data.Length, SocketFlags.None, ipEndPoint);

            // Receive session id
            var buffer = new byte[BUFFER_SIZE];
            serverSocket.Receive(buffer);
            sessionId = buffer[1];

            // Display if the connection was successfull
            Console.WriteLine(sessionId > 0 ? @"Connection successfull ..." : @"Connection unsuccessfull ...");

            return sessionId > 0;
        }
        #endregion
    }
}
