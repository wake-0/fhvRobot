using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using GameServer.Controllers;
using GameServer.Managers;
using GameServer.Services;
using GameServer.ViewModels;
using GameServer.Interfaces;

namespace GameServer.Views
{
    /// <summary>
    /// Interaction logic for SettingsWindow.xaml
    /// </summary>
    public partial class SettingsWindow : Window
    {
        public SettingsWindow(NetworkServer server, ITriggerSystem triggerSystem, TimerService timerService, ScoreManager scoreManager, MainViewModel mainView)
        {
            InitializeComponent();
            this.DataContext = new SettingsViewModel(server, triggerSystem, timerService, scoreManager, mainView);
        }
    }
}
