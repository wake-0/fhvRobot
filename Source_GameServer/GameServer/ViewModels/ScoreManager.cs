using System;
using System.Collections.ObjectModel;
using System.Linq;
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

        public static readonly int NUMBER_TOP_SCORES = 3;
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
            if (score == null) throw new ArgumentNullException("score");

            if (TopScores.Count < NUMBER_TOP_SCORES)
            {
                // Insert new top score
                InsertScoreAtPosition(TopScores, score);
            } 
            else if (TopScores.Any(s => s.Duration > score.Duration))
            {
                // Take last entry
                var worstScore = TopScores.Last();
                TopScores.Remove(worstScore);

                // Insert worst score to normal list at the beginning
                Scores.Insert(0, worstScore);

                // Insert new top score
                InsertScoreAtPosition(TopScores, score);
            }
            else
            {
                // Insert into normal list
                InsertScoreAtPosition(Scores, score);
            }
        }

        private void InsertScoreAtPosition(ObservableCollection<Score> scores, Score score)
        {
            if (scores == null) throw new ArgumentNullException("scores");

            for (var i = 0; i < scores.Count; i++)
            {
                if (scores.ElementAt(i).Duration <= score.Duration) continue;

                scores.Insert(i, score);
                return;
            }

            scores.Add(score);
        }
        #endregion
    }
}
