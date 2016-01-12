using System.Collections.ObjectModel;
using GameServer.Models;
using PostSharp.Patterns.Model;

namespace GameServer.ViewModels
{
    [NotifyPropertyChanged]
    public class ScoreManager
    {
        #region Properties
        public ObservableCollection<Score> TopScores { get; private set; }
        public ObservableCollection<Score> Scores { get; private set; }
        #endregion

        #region ctor
        public ScoreManager()
        {
            TopScores = new ObservableCollection<Score>();
            Scores = new ObservableCollection<Score>();    
        }
        #endregion

        #region Methods
        public void Add(Score score)
        {
            if (TopScores.Count < 3)
            {
                TopScores.Add(score);
            }

            Scores.Add(score);
        }
        #endregion
    }
}
