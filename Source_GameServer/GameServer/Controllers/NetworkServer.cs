using System;
using System.Threading;

namespace GameServer.Controllers
{
    public class NetworkServer
    {
        #region Fields
        private readonly NetworkCommunication communication;
        private readonly Thread networkThread;
        #endregion

        #region Events
        public event EventHandler<string> NewPlayerReceived;
        #endregion

        #region ctor
        public NetworkServer()
        {
            communication = new NetworkCommunication();
            communication.MessageReceived += OnMessageReceived;
            networkThread = new Thread(communication.Run);
        }
        #endregion

        #region Methods
        public void Start()
        {
            networkThread.Start();
        }

        public void SendMessage(string message)
        {
            communication.SendMessage(message);
        }

        public void Stop()
        {
            communication.Stop();
            networkThread.Join();
        }

        public void RequestOperator()
        {
            communication.SendCommand(Commands.GET_OPERATOR, "");
        }

        private void OnMessageReceived(object sender, byte[] message)
        {
            // Check get operator command and answer bit set
            if (message[4] == Commands.GET_OPERATOR && ((message[3] & 1) != 0))
            {
                var name = "Test";
                if (NewPlayerReceived != null)
                {
                    NewPlayerReceived.Invoke(sender, name);
                }
            }
        }
        #endregion
    }
}
