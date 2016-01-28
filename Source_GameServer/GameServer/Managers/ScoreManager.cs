using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Threading;
using System.Xml;
using GameServer.Models;
using PostSharp.Patterns.Model;

namespace GameServer.Managers
{
    [NotifyPropertyChanged]
    public class ScoreManager
    {
        #region Properties
        public Score CurrentScore { get; set; }
        public ObservableCollection<Score> TopScores { get; private set; }
        public ObservableCollection<Score> Scores { get; private set; }

        public static readonly int NUMBER_TOP_SCORES = 3;
        #endregion

        #region ctor
        public ScoreManager()
        {
            TopScores = new ObservableCollection<Score>();
            Scores = new ObservableCollection<Score>();
            CurrentScore = null;
        }
        #endregion

        #region Methods
        public void Add(Score score)
        {
            if (score == null) throw new ArgumentNullException("score");

            Application.Current.Dispatcher.Invoke(() =>
            {
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

                UpdateRank();
            });
        }

        public void DeleteScore(int index)
        {
            if (TopScores.Count + Scores.Count - 1 < index || index < 0)
            {
                return;
            }
            try {
                if (index <= NUMBER_TOP_SCORES - 1)
                {
                    TopScores.RemoveAt(index);
                    var bestScores = Scores.First();
                    Scores.Remove(bestScores);
                    TopScores.Add(bestScores);
                }
                else
                {
                    Scores.RemoveAt(index - NUMBER_TOP_SCORES);
                }
            } catch
            {

            }
            UpdateRank();
        }

        private void UpdateRank()
        {
            for (var i = 0; i < TopScores.Count; i++)
            {
                TopScores[i].Rank = i + 1;
            }

            for (var i = 0; i < Scores.Count; i++)
            {
                Scores[i].Rank = i + 1 + NUMBER_TOP_SCORES;
            }
        }

        public List<Score> GetAllScores()
        {
            var list = new List<Score>();
            list.AddRange(TopScores);
            list.AddRange(Scores);
            return list;
        }

        public void SetAllScores(IEnumerable<Score> scores)
        {
            // Clear old scores
            Clear();

            // Add new scores
            foreach (var score in scores)
            {
                Add(score);
            }
        }

        public string GetScoresAsXmlString()
        {
            var builder = new StringBuilder();
            using (var writer = XmlWriter.Create(builder))
            {
                writer.WriteStartDocument();
                writer.WriteStartElement("Scores");

                foreach (var score in GetAllScores())
                {
                    writer.WriteStartElement("Score");
                    writer.WriteAttributeString("Name", score.Name);
                    writer.WriteAttributeString("Duration", score.Duration.ToString("mm\\:ss\\.ff"));
                    writer.WriteEndElement();
                }

                writer.WriteEndElement();
                writer.WriteEndDocument();
            }

            return builder.ToString();
        }

        public void Clear()
        {

            Application.Current.Dispatcher.Invoke(() =>
            {
                TopScores.Clear();
                Scores.Clear();
            });
        }

        private void InsertScoreAtPosition(ObservableCollection<Score> scores, Score score)
        {
            if (scores == null) throw new ArgumentNullException("scores");

            Application.Current.Dispatcher.Invoke(() =>
            {
                for (var i = 0; i < scores.Count; i++)
                {
                    if (scores.ElementAt(i).Duration <= score.Duration) continue;

                    scores.Insert(i, score);
                    return;
                }

                scores.Add(score);
            });
        }
        #endregion
    }
}
