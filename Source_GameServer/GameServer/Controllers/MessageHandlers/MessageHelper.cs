using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GameServer.Controllers.MessageHandlers
{
    public static class MessageHelper
    {
        public static bool IsExtendedMessage(byte[] message)
        {
            return (message[3] & 2) == 1 && message.Length > 6;
        }

        public static bool IsReceivedOperatorMessage(byte[] message)
        {
            return message[4] == Commands.GET_OPERATOR && ((message[3] & 1) != 0);
        }
    }
}
