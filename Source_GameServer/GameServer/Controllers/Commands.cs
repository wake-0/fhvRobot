namespace GameServer.Controllers
{
    public static class Commands
    {
        public static readonly int GENERAL_MESSAGE = 2;
        public static readonly int FORWARD_GENERAL_MESSAGE = 3;
        public static readonly int GET_OPERATOR = 30;

        public static readonly int PERSIST_DATA = 60;
        public static readonly int REQUEST_PERSIST_DATA = 61;

        public static readonly int TIME_MEASUREMENT_STARTED = 40;
        public static readonly int TIME_MEASUREMENT_STOPPED = 41;
        public static readonly int TIME_MEASUREMENT_DISMISSED = 42;
    }
}
