using System;

namespace GameServer.Interfaces
{
    public interface ITimeMeasurement
    {
        bool IsSystemActive { get; }
        event EventHandler<TimeSpan> TimeMeasured;
 
        bool ActivateSystem();
        bool DeactivateSystem();
    }
}
