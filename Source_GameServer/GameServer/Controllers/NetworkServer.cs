using System.Threading;

namespace GameServer.Controllers
{
    public class NetworkServer
    {
        #region Fields
        private readonly NetworkCommunication communication;
        private readonly Thread networkThread;
        #endregion

        #region ctor
        public NetworkServer()
        {
            communication = new NetworkCommunication();
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
        #endregion
    }
}
