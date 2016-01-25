using System;
using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Interfaces;
using GameServer.Managers;
using GameServer.Mocks;
using GameServer.Models;
using GameServer.Services;
using GameServer.Utils;
using Microsoft.Win32;
using PostSharp.Patterns.Model;
using GameServer.Views;

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
                }

                isTriggerSystemActive = TriggerSystem.IsSystemActive;
            }
        }
        public string ExampleText { get; set; }

        public ICommand SendHighscoreCommand { get; private set; }
        public ICommand SendOperatorCommand { get; private set; }
        public ICommand SendMessageCommand { get; private set; }
        public ICommand TestCommand { get; private set; }
        public ICommand OpenCommand { get; private set; }
        public ICommand SaveCommand { get; private set; }
        public ICommand ShowTMWindowCommand { get; private set; }

        public ITriggerSystem TriggerSystem { get; private set; }
        #endregion

        #region ctor
        public SettingsViewModel(NetworkServer server, TimerService timerService, ScoreManager scoreManager)
        {
            this.timerService = timerService;
            this.server = server;
            this.scoreManager = scoreManager;
            persistencyManager = new PersistencyManager();
            TriggerSystem = CameraTriggerService.Instance;
            TriggerSystem.TriggerRaised += TimeTrigger;

            SendMessageCommand = new DelegateCommand(SendMessage);
            SendHighscoreCommand = new DelegateCommand(SendHighscore);
            SendOperatorCommand = new DelegateCommand(SendOperator);

            TestCommand = new DelegateCommand(o => SetTestData());
            OpenCommand = new DelegateCommand(o => LoadScore());
            SaveCommand = new DelegateCommand(o => SaveScore());

            ShowTMWindowCommand = new DelegateCommand(o => ShowTimeMeasurementWindow());

            ExampleText = "Test";
        }
        #endregion

        #region Methods

        private void TimeTrigger(object sender, EventArgs e)
        {
            timerService.ToggleStartStop();
        }

        private void SetTestData()
        {
            scoreManager.CurrentScore = new Score { Name = "MyPlayer", Duration = new TimeSpan() };

            scoreManager.Clear();
            scoreManager.Add(new Score { Name = "Peter", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Klaus", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Johannes", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Bernhard", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Mathias", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Julia", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Max", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Johanna", Duration = new TimeSpan() });
            scoreManager.Add(new Score { Name = "Mario", Duration = new TimeSpan() });
        }

        private void SendMessage(object obj)
        {
            server.SendMessage(ExampleText);
            timerService.ToggleStartStop();
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
