using System.ComponentModel;
using GameServer.ViewModels;
using System.Windows;
using GameServer.Models;

namespace GameServer.Views
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private bool selectionLock;

        public MainWindow()
        {
            InitializeComponent();
            ((MainViewModel)DataContext).ScoreManager.Scores.CollectionChanged += Scores_CollectionChanged;
        }

        void Scores_CollectionChanged(object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e)
        {
            if (ScoreListView.Items.Count > 1)
            {
                var item = ScoreListView.Items[ScoreListView.Items.Count - 1];
                ScoreListView.ScrollIntoView((Score)item);
            }
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            base.OnClosing(e);
            System.Environment.Exit(1);
        }

        private void ScoreListView_Selected(object sender, RoutedEventArgs e)
        {
            if (!selectionLock)
            {
                selectionLock = true;
                TopScoreListView.SelectedIndex = -1;
                selectionLock = false;
                ((MainViewModel)DataContext).SelectedScore = TopScoreListView.Items.Count + ScoreListView.SelectedIndex;
            }
        }

        private void TopScoreListView_Selected(object sender, RoutedEventArgs e)
        {
            if (!selectionLock)
            {
                selectionLock = true;
                ScoreListView.SelectedIndex = -1;
                selectionLock = false;
                ((MainViewModel)DataContext).SelectedScore = TopScoreListView.SelectedIndex;
            }
        }
    }
}
