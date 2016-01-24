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
    }
}
