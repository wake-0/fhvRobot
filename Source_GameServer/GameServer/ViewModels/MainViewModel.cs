using System.Windows.Input;
using GameServer.Controllers;
using GameServer.Utils;
using PostSharp.Patterns.Model;

namespace GameServer.ViewModels
{
    [NotifyPropertyChanged]
    public class MainViewModel
    {
        #region properties
        public string TestText { get; set; }
        public ICommand ChangeTextCommand { get; set; }

        private const string Text1 = "GameServer";
        private const string Text2 = "ChangedText";

        private readonly NetworkServer server;
        #endregion

        #region ctor
        public MainViewModel()
        {
            server = new NetworkServer();

            TestText = Text1;
            ChangeTextCommand = new DelegateCommand(ChangeText);
        }

        private void ChangeText(object obj)
        {
            TestText = TestText == Text1 ? Text2 : Text1;
            server.Send("Test");
        }

        #endregion

    }
}
