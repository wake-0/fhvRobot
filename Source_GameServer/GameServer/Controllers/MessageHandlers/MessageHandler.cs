using System;
using System.Linq;

namespace GameServer.Controllers.MessageHandlers
{
    public class MessageHandler : IMessageHandler
    {
        #region Singleton
        private static MessageHandler instance;

        public static IMessageHandler Instance
        {
            get { return instance ?? (instance = new MessageHandler()); }
        }

        private MessageHandler() { }
        #endregion

        #region Methods
        public byte[] Handle(byte[] message)
        {
            if (message == null || message.Length < 6) throw new ArgumentNullException("message");

            var length = message[5];
            const int offset = 6;
            return message.Skip(offset).Take(length).ToArray();
        }
        #endregion
    }
}
