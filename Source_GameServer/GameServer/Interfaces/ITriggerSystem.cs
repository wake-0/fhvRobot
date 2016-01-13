using System;

namespace GameServer.Interfaces
{
    public interface ITriggerSystem
    {
        bool IsSystemActive { get; }
        event EventHandler TriggerRaised;
 
        bool ActivateSystem();
        bool DeactivateSystem();
    }
}
