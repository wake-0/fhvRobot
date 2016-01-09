using System.Windows.Controls;
using System.Windows.Media;

namespace GameServer.Controls
{
    /// <summary>
    /// Interaction logic for DigitalClockDots.xaml
    /// </summary>
    public partial class DigitalClockSingleDot : UserControl
    {
        private Brush renderBrush = null;

        public DigitalClockSingleDot()
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
            }
        }
    }
}
