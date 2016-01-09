using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace GameServer.Controllers
{
    public class NetworkServer
    {
        public void Send(string message)
        {
            IPEndPoint RemoteEndPoint = new IPEndPoint(NetworkSettings.SERVER_ADDRESS, NetworkSettings.SERVER_PORT);
            Socket server = new Socket(AddressFamily.InterNetwork, SocketType.Dgram, ProtocolType.Udp);

            // First time send open message
            //int sessionId = ConfigurationSettings.DEFAULT_SESSION_ID;
            //int flags = ConfigurationSettings.REQUEST_SESSION_FLAGS;
            //String openMessage = ConfigurationSettings.OPEN_MESSAGE;
            //return new NetworkPDU(new TransportPDU(new SessionPDU(flags, sessionId, new PresentationPDU(new ApplicationPDU(new PDU(openMessage))))));
            byte[] data = { 1, 0 };
            server.SendTo(data, data.Length, SocketFlags.None, RemoteEndPoint);

            byte[] buffer = new byte[4096];
            server.Receive(buffer);

            Console.WriteLine(BitConverter.ToString(buffer));

            byte sessionId = buffer[1];
            if (sessionId == 0)
            {
                // 
            }
            else
            {
                byte[] test = Encoding.ASCII.GetBytes("Hallo");
                data = new byte[] {0, sessionId, 0, 0, 2, 5, test[0], test[1], test[2], test[3], test[4]};
                server.SendTo(data, data.Length, SocketFlags.None, RemoteEndPoint);

                while (true)
                {
                    buffer = new byte[4096];
                    server.ReceiveTimeout = -1;
                    int value = server.Receive(buffer);

                    if (buffer[4] != 0)
                    {
                        Console.WriteLine("return [" + value + "]");
                        Console.WriteLine(BitConverter.ToString(buffer));
                    }
                   
                }
            }


            // Second send some simple text 
            //return new NetworkPDU(new TransportPDU(new SessionPDU(configuration.getSessionId(), new PresentationPDU(new ApplicationPDU(flag, command, new PDU(payload))))));
        }

        public void Receive()
        {
            // Only accept the packages received from the server
            IPEndPoint sender = new IPEndPoint(NetworkSettings.SERVER_ADDRESS, NetworkSettings.SERVER_PORT);
            UdpClient receiveSocket = new UdpClient(sender);

            while (true)
            {
                byte[] data = new byte[1024];
                data = receiveSocket.Receive(ref sender);

                Console.WriteLine(@"Message received from {0}:", sender.ToString());
                Console.WriteLine(Encoding.ASCII.GetString(data, 0, data.Length));

            }

        }

       

    }
}
