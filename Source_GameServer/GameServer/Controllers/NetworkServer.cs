using System;
using System.Threading;
using GameServer.Managers;

namespace GameServer.Controllers
{
    public class NetworkServer
    {
        #region Fields
        private readonly NetworkCommunication communication;
        private readonly Thread networkThread;
        private readonly ScoreManager scoreManager;
        #endregion

        #region Events
        public event EventHandler<string> NewPlayerReceived;
        #endregion

        #region ctor
        public NetworkServer(ScoreManager scoreManager)
        {
            this.scoreManager = scoreManager;

            communication = new NetworkCommunication();
            networkThread = new Thread(communication.Run);

            communication.MessageReceived += OnMessageReceived;
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

        public void SendHighScore()
        {
            communication.SendCommand(Commands.PERSIST_DATA, scoreManager.GetScoresAsXmlString());            
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
