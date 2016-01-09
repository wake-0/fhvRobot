using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace GameServer.Controls
{
    /// <summary>
    /// Interaction logic for DigitalClockElement.xaml
    /// </summary>
    public partial class DigitalClockElement : UserControl
    {
        private Brush renderBrush = null;
        private int displayValue = 8;

        public DigitalClockElement()
        {
            InitializeComponent();

            this.RenderBrush = new SolidColorBrush(Color.FromRgb(0, 0, 0));
        }

        public Brush RenderBrush
        {
            get
            {
                return renderBrush;
            }
            set
            {
                renderBrush = value;
                p1.Fill = renderBrush;
                p2.Fill = renderBrush;
                p3.Fill = renderBrush;
                p4.Fill = renderBrush;
                p5.Fill = renderBrush;
                p6.Fill = renderBrush;
                p7.Fill = renderBrush;
            }
        }

        public int Value
        {
            get
            {
                return displayValue;
            }
            set
            {
                if (value > 9 || value < 0)
                {
                    throw new Exception("Out of range (0 -> 9)");
                }

                #region hide all
                p1.Visibility = Visibility.Hidden;
                p2.Visibility = Visibility.Hidden;
                p3.Visibility = Visibility.Hidden;
                p4.Visibility = Visibility.Hidden;
                p5.Visibility = Visibility.Hidden;
                p6.Visibility = Visibility.Hidden;
                p7.Visibility = Visibility.Hidden;
                #endregion

                #region show parts
                switch (value)
                {
                    case 0:
                        p1.Visibility = Visibility.Visible;
                        p2.Visibility = Visibility.Visible;
                        p3.Visibility = Visibility.Visible;
                        p5.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;
                        p7.Visibility = Visibility.Visible;

                        break;
                    case 1:
                        p3.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;

                        break;
                    case 2:
                        p1.Visibility = Visibility.Visible;
                        p3.Visibility = Visibility.Visible;
                        p4.Visibility = Visibility.Visible;
                        p5.Visibility = Visibility.Visible;
                        p7.Visibility = Visibility.Visible;

                        break;
                    case 3:
                        p1.Visibility = Visibility.Visible;
                        p3.Visibility = Visibility.Visible;
                        p4.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;
                        p7.Visibility = Visibility.Visible;

                        break;
                    case 4:
                        p2.Visibility = Visibility.Visible;
                        p3.Visibility = Visibility.Visible;
                        p4.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;

                        break;
                    case 5:
                        p1.Visibility = Visibility.Visible;
                        p2.Visibility = Visibility.Visible;
                        p4.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;
                        p7.Visibility = Visibility.Visible;

                        break;
                    case 6:
                        p1.Visibility = Visibility.Visible;
                        p2.Visibility = Visibility.Visible;
                        p4.Visibility = Visibility.Visible;
                        p5.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;
                        p7.Visibility = Visibility.Visible;

                        break;
                    case 7:
                        p1.Visibility = Visibility.Visible;
                        p3.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;

                        break;
                    case 8:
                        p1.Visibility = Visibility.Visible;
                        p2.Visibility = Visibility.Visible;
                        p3.Visibility = Visibility.Visible;
                        p4.Visibility = Visibility.Visible;
                        p5.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;
                        p7.Visibility = Visibility.Visible;

                        break;
                    case 9:
                        p1.Visibility = Visibility.Visible;
                        p2.Visibility = Visibility.Visible;
                        p3.Visibility = Visibility.Visible;
                        p4.Visibility = Visibility.Visible;
                        p6.Visibility = Visibility.Visible;

                        break;
                }
                #endregion

                displayValue = value;
            }
        }
    }
}
