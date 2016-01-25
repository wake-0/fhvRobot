using System;
using PostSharp.Patterns.Model;

namespace GameServer.Models
{
    [NotifyPropertyChanged]
    public class Score
    {
        #region Properties
        public int Rank { get; set; }
        public string Name { get; set; }
        public TimeSpan Duration { get; set; }
        #endregion
    }
}
