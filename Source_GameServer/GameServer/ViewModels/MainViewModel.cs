using System;
using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Models;
using GameServer.Services;
using GameServer.Utils;
using PostSharp.Patterns.Model;

namespace GameServer.ViewModels
{
    [NotifyPropertyChanged]
    public class MainViewModel
    {
        #region Properties
        public Score CurrentScore { get; private set; }
        public ScoreManager ScoreManager { get; private set; }
        public ICommand SendMessageCommand { get; private set; }
        public TimerService TimerService { get; private set; }
        #endregion

        #region Fields
        private readonly NetworkServer server;
        #endregion

        #region ctor
        public MainViewModel()
        {
            ScoreManager = new ScoreManager();
            // TODO: Discuss if the toggle start stop should be refactored
            TimerService = new TimerService();
            TimerService.ToggleStartStop();
            
            SendMessageCommand = new DelegateCommand(SendMessage);

            server = new NetworkServer();
            //server.Start();

            SetTestData();
        }
        #endregion

        #region Methods
        private void SetTestData()
        {
            CurrentScore = new Score { Name = "MyPlayer", Duration = new TimeSpan() };

            ScoreManager.Add(new Score { Name = "Peter", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Klaus", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Johannes", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Bernhard", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Mathias", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Julia", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Max", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Johanna", Duration = new TimeSpan() });
            ScoreManager.Add(new Score { Name = "Mario", Duration = new TimeSpan() });
        }

        private void SendMessage(object obj)
        {
            server.SendMessage(CurrentScore.Name);
            TimerService.ToggleStartStop();
        }

        #endregion
    }
}
