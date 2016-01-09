using System.Windows.Media;

namespace GameServer.Controls
{
    public partial class DigitalClockDots
    {
        private Brush renderBrush;

        public DigitalClockDots()
        {
            InitializeComponent();

            RenderBrush = new SolidColorBrush(Color.FromRgb(0, 0, 0));
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
                p0.Fill = renderBrush;
                p1.Fill = renderBrush;
            }
        }
    }
}
