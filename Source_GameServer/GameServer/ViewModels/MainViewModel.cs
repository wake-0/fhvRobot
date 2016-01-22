using System;
using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Managers;
using GameServer.Models;
using GameServer.Services;
using GameServer.Utils;
using GameServer.Views;
using PostSharp.Patterns.Model;

namespace GameServer.ViewModels
{
    [NotifyPropertyChanged]
    public class MainViewModel
    {
        #region Fields

        private readonly NetworkServer server;
        private SettingsWindow settingsWindow;
        #endregion

        #region Properties
        public ScoreManager ScoreManager { get; private set; }
        
        public TimerService TimerService { get; private set; }
        public ICommand OpenSettingsWindowCommand { get; private set; }
        #endregion

        #region ctor
        public MainViewModel()
        {
            ScoreManager = new ScoreManager();

            // TODO: Discuss if the toggle start stop should be refactored
            TimerService = new TimerService();
            //TimerService.ToggleStartStop();

            server = new NetworkServer(ScoreManager);
            server.NewPlayerReceived += NewPlayerReceived;
            server.Start();

            OpenSettingsWindowCommand = new DelegateCommand(OpenSettingsWindow);
            OpenSettingsWindowCommand.Execute(null);
        }
        #endregion

        #region Methods

        private void OpenSettingsWindow(object obj)
        {
            if (settingsWindow != null)
            {
                settingsWindow.Close();
            }

            settingsWindow = new SettingsWindow(server, TimerService, ScoreManager);
            settingsWindow.Show(); 
        }

        private void NewPlayerReceived(object sender, string playerName)
        {
            ScoreManager.CurrentScore = new Score { Name = playerName, Duration = new TimeSpan() };
        }
        #endregion
    }
}
