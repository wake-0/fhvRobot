using System;
using System.Windows;
using System.Windows.Media;
using PostSharp.Patterns.Model;

namespace GameServer.Controls
{
    public partial class DigitalClockControl
    {
        protected delegate void RefreshDelegate();

        public DigitalClockControl()
        {
            InitializeComponent();

            UpdateTime();
        }

        #region styling
        public Brush DigitBrush
        {
            set
            {
                p0.RenderBrush = value;
                p1.RenderBrush = value;
                p3.RenderBrush = value;
                p4.RenderBrush = value;
                p6.RenderBrush = value;
                p7.RenderBrush = value;
            }
        }

        public Brush DotBrush
        {
            set
            {
                p2.RenderBrush = value;
                p5.RenderBrush = value;
            }
        }

        public Brush ClockBackground
        {
            get
            {
                return masterBorder.Background;
            }
            set
            {
                masterBorder.Background = value;
            }
        }
        #endregion

        // Dependency Property
        public static readonly DependencyProperty CurrentTimeProperty =
             DependencyProperty.Register("CurrentTime", typeof(TimeSpan),
             typeof(DigitalClockControl), new FrameworkPropertyMetadata(new TimeSpan(), OnCurrentTimePropertyChanged));

        public TimeSpan CurrentTime
        {
            get { return (TimeSpan)GetValue(CurrentTimeProperty); }
            set
            {
                SetValue(CurrentTimeProperty, value);
            }
        }

        private static void OnCurrentTimePropertyChanged(DependencyObject source, DependencyPropertyChangedEventArgs e)
        {
            DigitalClockControl clock = source as DigitalClockControl;

            if (clock != null)
            {
                clock.UpdateTime();
            }
        }

        private void UpdateTime()
        {
            var value = CurrentTime;

            #region minutes

            if (value.Minutes > 9)
            {
                p0.Value = int.Parse(value.Minutes.ToString()[0].ToString());
                p1.Value = int.Parse(value.Minutes.ToString()[1].ToString());
            }
            else
            {
                p0.Value = 0;
                p1.Value = int.Parse(value.Minutes.ToString()[0].ToString());
            }

            #endregion

            #region seconds

            if (value.Seconds > 9)
            {
                p3.Value = int.Parse(value.Seconds.ToString()[0].ToString());
                p4.Value = int.Parse(value.Seconds.ToString()[1].ToString());
            }
            else
            {
                p3.Value = 0;
                p4.Value = int.Parse(value.Seconds.ToString()[0].ToString());
            }
            #endregion

            #region milliseconds

            if (value.Milliseconds > 99)
            {
                p6.Value = int.Parse(value.Milliseconds.ToString()[0].ToString());
                p7.Value = int.Parse(value.Milliseconds.ToString()[1].ToString());
            }
            else if (value.Milliseconds > 9)
            {
                p6.Value = 0;
                p7.Value = int.Parse(value.Milliseconds.ToString()[0].ToString());
            }
            else
            {
                p6.Value = 0;
                p7.Value = 0;
            }
            #endregion
        }
    }
}
