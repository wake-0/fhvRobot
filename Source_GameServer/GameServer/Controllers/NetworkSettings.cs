using System.Net;

namespace GameServer.Controllers
{
    public static class NetworkSettings
    {
        public static readonly int SERVER_PORT = 999;
        public static IPAddress SERVER_ADDRESS = IPAddress.Parse("127.0.0.1");
    }
}
