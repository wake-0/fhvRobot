using System;
using System.Linq;
using GameServer.Utils;

namespace GameServer.Controllers.MessageHandlers
{
    public class ExtendedMessageHandler : IMessageHandler
    {
        #region Singleton
        private static ExtendedMessageHandler instance;

        public static IMessageHandler Instance
        {
            get { return instance ?? (instance = new ExtendedMessageHandler()); }
        }

        private ExtendedMessageHandler() { }
        #endregion

        #region Methods
        public byte[] Handle(byte[] message)
        {
            if (message == null || message.Length < 7) throw new ArgumentNullException("message");

            var length = LengthConverter.ConvertLength(message.Skip(4).Take(2).ToArray());
            const int offset = 7;
            return message.Skip(offset).Take(length).ToArray();
        }
        #endregion
    }
}
