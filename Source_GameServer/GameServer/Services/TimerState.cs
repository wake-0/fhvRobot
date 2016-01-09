namespace GameServer.Services
{
    public enum TimerState
    {
        Tracking,
        Stopped
    }

    public static class TimerStateExtensions
    {
        public static TimerState ToggleState(this TimerState state)
        {
            if (state == TimerState.Stopped)
            {
                return TimerState.Tracking;
            }

            return TimerState.Stopped;
        }
    }
}
