using System;
using GameServer.Interfaces;
using PostSharp.Patterns.Model;

namespace GameServer.Mocks
{
    [NotifyPropertyChanged]
    public class TriggerSystemMock : ITriggerSystem
    {
        public bool IsSystemActive { get; private set; }

        public event EventHandler TriggerRaised;

        public bool ActivateSystem()
        {
            IsSystemActive = true;
            return true;
        }

        public bool DeactivateSystem()
        {
            IsSystemActive = false;
            return true;
        }
    }
}
