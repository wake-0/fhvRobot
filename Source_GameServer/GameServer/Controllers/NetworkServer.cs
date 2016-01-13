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
            communication.NewPlayerReceived += OnNewPlayerReceived;
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

        private void OnNewPlayerReceived(object sender, string value)
        {
            if (NewPlayerReceived != null)
            {
                NewPlayerReceived.Invoke(sender, value);
            }
        }
        #endregion
    }
}
