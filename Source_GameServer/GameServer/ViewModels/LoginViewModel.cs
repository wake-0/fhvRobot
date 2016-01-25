using System.Net;
using System.Windows.Forms;
using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Interfaces;
using GameServer.Utils;
using GameServer.Views;

namespace GameServer.ViewModels
{
    public class LoginViewModel
    {
        #region Fields

        private readonly IView view;
        #endregion

        #region Properties
        public ICommand LoginCommand { get; private set; }
        public string Text { get; set; }
        public bool LoadScores { get; set; }
        #endregion

        #region ctor

        public LoginViewModel(IView view)
        {
            this.view = view;
            Text = NetworkSettings.SERVER_ADDRESS.ToString();
            LoginCommand = new DelegateCommand(Login);
        }
        #endregion

        #region Methods
        private void Login(object obj)
        {
            IPAddress address;
            if (IPAddress.TryParse(Text, out address))
            {
                // Set the ip address
                NetworkSettings.SERVER_ADDRESS = address;

                // Create new game window
                var gameWindow = new MainWindow(LoadScores);
                gameWindow.Show();

                view.Close();
            }
            else
            {
                MessageBox.Show(@"Ip Address not valid");
            }

            
        }
        #endregion
    }
}
