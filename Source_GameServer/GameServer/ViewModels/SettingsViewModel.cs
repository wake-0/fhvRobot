using System;
using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Interfaces;
using GameServer.Managers;
using GameServer.Models;
using GameServer.Services;
using GameServer.Utils;
using Microsoft.Win32;
using PostSharp.Patterns.Model;
using GameServer.Views;
using System.Windows;

namespace GameServer.ViewModels
{
    [NotifyPropertyChanged]
    public class SettingsViewModel
    {
        #region Fields
        private bool isTriggerSystemActive;

        private readonly NetworkServer server;
        private readonly TimerService timerService;
        private readonly PersistencyManager persistencyManager;
        private readonly ScoreManager scoreManager;
        private TimeMeasurement tmWindow;
        private MainViewModel mainView;
        #endregion

        #region Properties
        public bool IsTriggerSystemActive
        {
            get
            {
                return isTriggerSystemActive;
            }
            set
            {
                if (value == isTriggerSystemActive)
                {
                    return;
                }

                if (value)
                {
                    TriggerSystem.ActivateSystem();
                }
                else
                {
                    TriggerSystem.DeactivateSystem();
                    if (timerService.TimerState == TimerState.Tracking)
                    {
                        timerService.ToggleStartStop();
                    }
                }

                isTriggerSystemActive = TriggerSystem.IsSystemActive;
            }
        }
        public string ExampleText { get; set; }
        public int SelectedScore { get; set; }

        public ICommand SendHighscoreCommand { get; private set; }
        public ICommand SendOperatorCommand { get; private set; }
        public ICommand SendMessageCommand { get; private set; }
        public ICommand TestCommand { get; private set; }
        public ICommand OpenCommand { get; private set; }
        public ICommand SaveCommand { get; private set; }
        public ICommand ShowTMWindowCommand { get; private set; }
        public ICommand SaveScoreCommand { get; private set; }
        public ICommand DeleteScoreCommand { get; private set; }
        public ICommand DiscardScoreCommand { get; private set; }

        public ITriggerSystem TriggerSystem { get; private set; }
        #endregion

        #region ctor
        public SettingsViewModel(NetworkServer server, ITriggerSystem triggerSystem, TimerService timerService, ScoreManager scoreManager, MainViewModel mainView)
        {
            this.timerService = timerService;
            this.server = server;
            this.scoreManager = scoreManager;
            this.mainView = mainView;
            persistencyManager = new PersistencyManager();
            TriggerSystem = triggerSystem;

            SendMessageCommand = new DelegateCommand(SendMessage);
            SendHighscoreCommand = new DelegateCommand(SendHighscore);
            SendOperatorCommand = new DelegateCommand(SendOperator);

            TestCommand = new DelegateCommand(o => SetTestData());
            OpenCommand = new DelegateCommand(o => LoadScore());
            SaveCommand = new DelegateCommand(o => SaveScore());

            ShowTMWindowCommand = new DelegateCommand(o => ShowTimeMeasurementWindow());
            SaveScoreCommand = new DelegateCommand(o => SaveCurrentScore());
            DeleteScoreCommand = new DelegateCommand(o => DeleteScore());
            DiscardScoreCommand = new DelegateCommand(o => DiscardScore());

            ExampleText = "Test";
        }
        #endregion

        #region Methods


        private void DiscardScore()
        {
            scoreManager.CurrentScore = null;
        }

        private void SaveCurrentScore()
        {
            if (scoreManager.CurrentScore != null && timerService.TimerState == TimerState.Stopped)
            {
                scoreManager.Add(scoreManager.CurrentScore);
                scoreManager.CurrentScore = null;
                server.SendHighScore();
            }
        }

        private void DeleteScore()
        {
            int index = mainView.SelectedScore;
            if (index >= 0)
            {
                Score s = scoreManager.GetAllScores()[index];
                if (s != null)
                {
                    // Ask for deletion
                    MessageBoxResult result = MessageBox.Show("Eintrag löschen?\n" + s.Rank + ". " + s.Name + " - " + s.Duration.ToString(), "Löschen", MessageBoxButton.YesNo, MessageBoxImage.Question);
                    if (result == MessageBoxResult.Yes)
                    {
                        scoreManager.DeleteScore(index);
                    }
                }
            }
        }

        private void SetTestData()
        {
            //scoreManager.CurrentScore = new Score { Name = "MyPlayer", Duration = new TimeSpan() };

            scoreManager.Clear();
            scoreManager.Add(new Score { Name = "Peter", Duration = new TimeSpan(1, 12, 13) });
            scoreManager.Add(new Score { Name = "Klaus", Duration = new TimeSpan(0, 55, 23) });
            scoreManager.Add(new Score { Name = "Johannes", Duration = new TimeSpan(0, 33, 12) });
            scoreManager.Add(new Score { Name = "Bernhard", Duration = new TimeSpan(0, 11, 3) });
            scoreManager.Add(new Score { Name = "Mathias", Duration = new TimeSpan(0, 2, 37) });
            scoreManager.Add(new Score { Name = "Julia", Duration = new TimeSpan(0, 7, 7) });
            scoreManager.Add(new Score { Name = "Max", Duration = new TimeSpan(0, 12, 34) });
            scoreManager.Add(new Score { Name = "Johanna", Duration = new TimeSpan(0, 11, 47) });
            scoreManager.Add(new Score { Name = "Mario", Duration = new TimeSpan(0, 44, 12) });
            scoreManager.Add(new Score { Name = "Mario", Duration = new TimeSpan(0, 17, 7) });
            scoreManager.Add(new Score { Name = "Mario", Duration = new TimeSpan(0, 8, 15) });
            scoreManager.Add(new Score { Name = "Mario", Duration = new TimeSpan(0, 47, 11) });

        }

        private void SendMessage(object obj)
        {
            server.SendMessage(ExampleText);
        }

        private void SendOperator(object obj)
        {
            server.RequestOperator();
        }

        private void SendHighscore(object obj)
        {
            server.SendHighScore();
        }

        private void SaveScore()
        {
            var saveFileDialog = new SaveFileDialog
            {
                Filter = "XML Files (*.xml)|*.xml",
                Title = "Save as Xml File"
            };

            saveFileDialog.ShowDialog();

            // If the file name is not an empty string open it for saving.
            var fileName = saveFileDialog.FileName;
            if (fileName != "")
            {
                persistencyManager.SaveScores(scoreManager.GetAllScores(), fileName);
            }
        }

        private void LoadScore()
        {
            var openFileDialog = new OpenFileDialog
            {
                InitialDirectory = "c:\\",
                Filter = "XML Files (*.xml)|*.xml",
                Title = "Load from Xml File",
                RestoreDirectory = true
            };

            openFileDialog.ShowDialog();

            var fileName = openFileDialog.FileName;
            if (fileName != "")
            {
                scoreManager.SetAllScores(persistencyManager.LoadScores(fileName));
            }
        }

        private void ShowTimeMeasurementWindow()
        {
            if (tmWindow == null || !tmWindow.IsVisible)
            {
                tmWindow = new TimeMeasurement();
                tmWindow.Show();
            }
        }
        #endregion
    }
}
