using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;
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
        #endregion

        #region ctor
        public MainViewModel()
        {
            TestText = Text1;
            ChangeTextCommand = new DelegateCommand(ChangeText);
        }

        private void ChangeText(object obj)
        {
            TestText = TestText == Text1 ? Text2 : Text1;
        }

        #endregion

    }
}
