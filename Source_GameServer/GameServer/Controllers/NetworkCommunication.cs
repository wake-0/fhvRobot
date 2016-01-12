﻿using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

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

                // Print received message
                Console.WriteLine(@"return [" + value + @"]");
                Console.WriteLine(BitConverter.ToString(buffer));
            }
        }

        public void SendMessage(string message)
        {
            byte[] payload = Encoding.ASCII.GetBytes(message);
            
            // Size of send data: flag, session id, flag, flag, command id, length, payload
            int sizeOfSendData = 6 + payload.Length;
            byte[] sendData = new byte[sizeOfSendData];

            // Set sendData
            sendData[0] = 0; // Flags
            sendData[1] = sessionId;
            sendData[2] = 0; // Flags
            sendData[3] = 0; // Flags
            sendData[4] = 2; // General message command
            sendData[5] = (byte)payload.Length;

            // Set payload
            for (var i = 6; i < sizeOfSendData; i++)
            {
                sendData[i] = payload[i - 6];
            }

            serverSocket.SendTo(sendData, sizeOfSendData, SocketFlags.None, ipEndPoint);
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