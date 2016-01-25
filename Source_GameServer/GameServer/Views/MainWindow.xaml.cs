using System.ComponentModel;
using GameServer.ViewModels;
using System.Windows;
using GameServer.Models;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System;
using GameServer.Managers;

namespace GameServer.Views
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private bool selectionLock;
        private Storyboard storyboard;

        public MainWindow(bool loadScores)
        {
            InitializeComponent();
            ((MainViewModel)DataContext).ScoreManager.Scores.CollectionChanged += Scores_CollectionChanged;
            if (loadScores)
            {
                ((MainViewModel)DataContext).LoadDefaultScores();
            }
        }

        void Scores_CollectionChanged(object sender, System.Collections.Specialized.NotifyCollectionChangedEventArgs e)
        {
            if (ScoreListView.Items.Count > 1)
            {
                var item = ScoreListView.Items[ScoreListView.Items.Count - 1];
                RestartAnimation(null, null);
                ((MainViewModel)DataContext).SaveDefaultScores();
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

        private void Window_Initialized(object sender, System.EventArgs e)
        {
            RestartAnimation(null, null);
        }

        private void RestartAnimation(object sender, EventArgs e)
        {
            ScrollViewer scrollViewer = ScoreScrollViewer;
            scrollViewer.UpdateLayout();
            if (scrollViewer != null && scrollViewer.ComputedVerticalScrollBarVisibility == Visibility.Visible && scrollViewer.ScrollableHeight > 0)
            {
                DoubleAnimation verticalAnimation = new DoubleAnimation();

                verticalAnimation.From = 0;
                verticalAnimation.To = scrollViewer.ScrollableHeight;
                verticalAnimation.Duration = new Duration(new TimeSpan(0, 0, 0, 0, ScoreListView.Items.Count * 500));

                if (storyboard != null)
                {
                    storyboard.Completed -= RestartAnimation;
                    storyboard.Stop();
                }
                storyboard = new Storyboard();
                storyboard.Children.Add(verticalAnimation);
                Storyboard.SetTarget(verticalAnimation, scrollViewer);
                Storyboard.SetTargetProperty(verticalAnimation, new PropertyPath(ScrollViewerBehavior.VerticalOffsetProperty));
                storyboard.Completed += RestartAnimation;
                storyboard.AutoReverse = true;
                storyboard.Begin();
            }
        }

        public class ScrollViewerBehavior
        {
            public static DependencyProperty VerticalOffsetProperty =
                DependencyProperty.RegisterAttached("VerticalOffset",
                                                    typeof(double),
                                                    typeof(ScrollViewerBehavior),
                                                    new UIPropertyMetadata(0.0, OnVerticalOffsetChanged));

            public static void SetVerticalOffset(FrameworkElement target, double value)
            {
                target.SetValue(VerticalOffsetProperty, value);
            }
            public static double GetVerticalOffset(FrameworkElement target)
            {
                return (double)target.GetValue(VerticalOffsetProperty);
            }
            private static void OnVerticalOffsetChanged(DependencyObject target, DependencyPropertyChangedEventArgs e)
            {
                ScrollViewer scrollViewer = target as ScrollViewer;
                if (scrollViewer != null)
                {
                    scrollViewer.ScrollToVerticalOffset((double)e.NewValue);
                }
            }
        }
    }
}
