using System;

namespace GameServer.Services
{
    public class TimeTrackedEventArgs : EventArgs
    {
        #region properties
        public DateTime StartTime { get; private set; }
        public DateTime EndTime { get; private set; }
        #endregion

        #region ctor
        public TimeTrackedEventArgs(DateTime startTime, DateTime endTime)
        {
            StartTime = startTime;
            EndTime = endTime;
        }
        #endregion
    }
}
