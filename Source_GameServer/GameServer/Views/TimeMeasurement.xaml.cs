using Emgu.CV;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace GameServer.Views
{
    /// <summary>
    /// Interaction logic for TimeMeasurement.xaml
    /// </summary>
    public partial class TimeMeasurement : Window
    {
        Capture _capture;
        private bool _captureInProgress;

        public TimeMeasurement()
        {
            //             var window = new TimeMeasurement();
            //             window.Show();
            InitializeComponent();
            try
            {
                _capture = new Capture();
                _capture.ImageGrabbed += ProcessFrame;
            }
            catch (NullReferenceException excpt)
            {   //show errors if there is any
                MessageBox.Show(excpt.Message);
            }
        }


        private void Button_Click(object sender, System.Windows.RoutedEventArgs e)
        {
            if (_capture != null)
            {
                if (_captureInProgress)
                {  //stop the capture
                    _capture.Pause();
                }
                else
                {
                    //start the capture
                    _capture.Start();
                }

                _captureInProgress = !_captureInProgress;
            }
        }

        private void ProcessFrame(object sender, EventArgs e)
        {
            Mat image = new Mat();

            _capture.Retrieve(image);
            this.Dispatcher.Invoke((Action)(() => {
                try
                {
                    imgTestCapture.Source = GameServer.Converters.BitmapSourceConvert.ToBitmapSource(image);
                }
                catch (Exception ex) { Console.WriteLine(ex.Message); }
            }));
        }
    }
}
