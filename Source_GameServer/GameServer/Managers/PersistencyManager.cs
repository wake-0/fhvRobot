using System;
using System.Collections.Generic;
using System.Xml;
using GameServer.Models;
using PostSharp.Patterns.Model;

namespace GameServer.Managers
{
    [NotifyPropertyChanged]
    public class PersistencyManager
    {
        public void SaveScores(IEnumerable<Score> scores, string fileName)
        {
            using (var writer = XmlWriter.Create(fileName))
            {
                writer.WriteStartDocument();
                writer.WriteStartElement("Scores");

                foreach (var score in scores)
                {
                    writer.WriteStartElement("Score");
                    writer.WriteAttributeString("Name", score.Name);
                    writer.WriteAttributeString("Duration", score.Duration.ToString());
                    writer.WriteEndElement();
                }

                writer.WriteEndElement();
                writer.WriteEndDocument();
            }
        }

        public IEnumerable<Score> LoadScores(string fileName)
        {
            var scores = new List<Score>();

            try {
                using (var reader = XmlReader.Create(fileName))
                {
                    while (reader.Read())
                    {
                        // Only detect start elements.
                        if (!reader.IsStartElement()) continue;

                        // Get element name and switch on it.
                        if (reader.Name == "Score")
                        {
                            var score = new Score();

                            var nameAttribute = reader["Name"];
                            if (nameAttribute != null)
                            {
                                score.Name = nameAttribute;
                            }

                            var durationAttribute = reader["Duration"];
                            if (durationAttribute != null)
                            {
                                score.Duration = TimeSpan.Parse(durationAttribute);
                            }

                            scores.Add(score);
                        }
                    }
                }
            }
            catch
            {
                Console.WriteLine("Could not load scores");
            }
            return scores;
        }
    }
}
