using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Services;
using GameServer.Utils;
using PostSharp.Patterns.Model;

namespace GameServer.ViewModels
{
    [NotifyPropertyChanged]
    public class MainViewModel
    {
        #region properties
        public string Name { get; set; }
        public ICommand SendMessageCommand { get; private set; }
        public TimerService TimerService { get; private set; }
        #endregion

        #region Fields
        private readonly NetworkServer server;
        #endregion

        #region ctor
        public MainViewModel()
        {
            TimerService = new TimerService();
            // TODO: Discuss if the toggle start stop should be refactored
            TimerService.ToggleStartStop();

            server = new NetworkServer();
            server.Start();

            Name = "Player";
            SendMessageCommand = new DelegateCommand(SendMessage);
        }

        private void SendMessage(object obj)
        {
            server.SendMessage(Name);
            TimerService.ToggleStartStop();
        }

        #endregion
    }
}
