using System;
using System.Timers;
using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Managers;
using GameServer.Models;
using GameServer.Services;
using GameServer.Utils;
using GameServer.Views;
using PostSharp.Patterns.Model;
using GameServer.Interfaces;

namespace GameServer.ViewModels
{
    [NotifyPropertyChanged]
    public class MainViewModel
    {
        public const string DEFAULT_SCORE_FILENAME = "scores.xml";

        #region Fields
        private readonly NetworkServer server;
        private readonly Timer updateCurrentPlayerTimer;
        private SettingsWindow settingsWindow;
        private ITriggerSystem triggerSystem;
        #endregion

        #region Properties
        public ScoreManager ScoreManager { get; private set; }

        public TimerService TimerService { get; private set; }
        public ICommand OpenSettingsWindowCommand { get; private set; }
        public int SelectedScore { get; internal set; }
        public string CurrentPlayer { get; internal set; }
        public TimeSpan CurrentBestTime { get; internal set; }
        #endregion

        #region ctor
        public MainViewModel()
        {
            ScoreManager = new ScoreManager();
            SelectedScore = -1;
            CurrentPlayer = "-";

            triggerSystem = CameraTriggerService.Instance;
            triggerSystem.TriggerRaised += TimeTrigger;

            TimerService = new TimerService();
            TimerService.TimeTracked += TimeTracked;

            server = new NetworkServer(ScoreManager);
            server.NewPlayerReceived += NewPlayerReceived;
            server.Start();

            OpenSettingsWindowCommand = new DelegateCommand(OpenSettingsWindow);
            OpenSettingsWindowCommand.Execute(null);

            // Each 100 milliseconds get player
            updateCurrentPlayerTimer = new Timer(2000);
            updateCurrentPlayerTimer.Elapsed += OnUpdateCurrentPlayerTimerElapsed;
            updateCurrentPlayerTimer.Start();
        }
        #endregion

        #region Methods

        internal void LoadDefaultScores()
        {
            Console.WriteLine("Loading scores...");
            PersistencyManager p = new PersistencyManager();
            ScoreManager.SetAllScores(p.LoadScores(DEFAULT_SCORE_FILENAME));
        }

        internal void SaveDefaultScores()
        {
            PersistencyManager p = new PersistencyManager();
            p.SaveScores(ScoreManager.GetAllScores(), DEFAULT_SCORE_FILENAME);
        }

        private void TimeTracked(object sender, TimeTrackedEventArgs e)
        {
            TimeSpan time = e.EndTime.Subtract(e.StartTime);
            server.SendTimeMeasurementStopped(time.ToString("mm\\:ss\\.ff"));
            if (triggerSystem.IsSystemActive && (ScoreManager.CurrentScore == null || ScoreManager.CurrentScore.Duration.CompareTo(time) > 0))
            {
                ScoreManager.CurrentScore = new Score { Name = CurrentPlayer, Duration = time };
            }
        }

        private void TimeTrigger(object sender, EventArgs e)
        {
            TimerService.ToggleStartStop();
            if (TimerService.TimerState == TimerState.Tracking)
            {
                server.SendTimeMeasurementStarted();
            }
        }

        private void OnUpdateCurrentPlayerTimerElapsed(object sender, ElapsedEventArgs e)
        {
            server.RequestOperator();
        }

        private void OpenSettingsWindow(object obj)
        {
            if (settingsWindow != null)
            {
                settingsWindow.Close();
            }

            settingsWindow = new SettingsWindow(server, triggerSystem, TimerService, ScoreManager, this);
            settingsWindow.Show(); 
        }

        private void NewPlayerReceived(object sender, string playerName)
        {
            if (string.IsNullOrEmpty(playerName)) { return;}

            if (playerName.Equals(CurrentPlayer) == false)
            {
                CurrentPlayer = playerName;
                ScoreManager.CurrentScore = null;
            }
        }
        #endregion
    }
}
